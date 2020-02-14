package be.uhasselt.smartglasses;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ///////////////
    //DATAMEMBERS//
    ///////////////

    private ImageView button;
    private TextView textView;

    private BluetoothAdapter adapter;
    private BluetoothLeScanner scanner;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic characteristic;

    private Handler handler;
    private Runnable runnable;

    private boolean status;

    /////////////
    //ONCREATE//
    ////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textView = findViewById(R.id.textview);

        if(savedInstanceState != null && savedInstanceState.containsKey("status")) {
            status = savedInstanceState.getBoolean("status");
            update();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    ///////////////
    //PERMISSIONS//
    ///////////////

    private void checkPermissionsAndAccesses() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            textView.setText(getString(R.string.connectionfailed_string));
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
        }

        adapter = ((BluetoothManager) getBaseContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if (adapter == null) {
            textView.setText(getString(R.string.connectionfailed_string));
            return;
        }
        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1000);
            return;
        }

        if (!isNLServiceEnabled()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivityForResult(intent, 1000);
        }

        scanBluetooth();

    }

    private void scanBluetooth() {
        scanner = adapter.getBluetoothLeScanner();
        final ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (result.getDevice().getName() != null && result.getDevice().getName().equals("HMSoft") && result.getDevice().getAddress().equals("D8:A9:8B:96:78:7E")) {
                    if(handler != null && runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                    scanner.stopScan(this);

                    Log.i("SmartGlassesSCANNER", "Found: " + result.getDevice().getAddress());
                    textView.setText("Found");

                    connectBluetoothModule(result.getDevice());
                }
            }
        };

        runnable = new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(scanCallback);
                Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_SHORT).show();
                recreate();
            }
        };

        handler = new Handler();
        handler.postDelayed(runnable,10000);

        scanner.startScan(scanCallback);
        textView.setText("Searching...");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissionsAndAccesses();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkPermissionsAndAccesses();
    }

    //////////////
    //CONNECTION//
    //////////////

    public void connectBluetoothModule(BluetoothDevice device) {
        gatt = device.connectGatt(this, false, gattCallback);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int statuss, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("SmartGlassesCONNECTION","Connected");
                    status = true;
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.i("SmartGlassesCONNECTION","Disconnected");
                    status = false;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                BluetoothGattService service = services.get(2);
                characteristic = services.get(2).getCharacteristics().get(0);
                if(characteristic != null) {
                    String text = "Connected__" + Build.MANUFACTURER.replace(" ","__") + "__~~";
                    sendNotification(text);
                }
                Log.i("SmartGlassesBEGIN", "Service: " + service.getUuid().toString());
                Log.i("SmartGlassesBEGIN", "Characteristic: " + characteristic.getUuid().toString());
                startService(new Intent(MainActivity.this, NotificationService.class));
            }
        }
    };

    ////////////
    //RECEIVER//
    ////////////

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("SmartGlassesBCTRECEIVER", "Received");
            String title = intent.getStringExtra("title");
            if (gatt != null && characteristic != null) {
                Log.i("SmartGlassesBCTRECEIVER", "Wrote");
                byte[] bytes = title.getBytes();
                characteristic.setValue(bytes);
                gatt.writeCharacteristic(characteristic);
            }
        }
    };

    //////////
    //OTHERS//
    //////////

    private void update() {
        if (status) {
            status = false;
            button.setColorFilter(Color.BLACK);
            textView.setText("");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
            if (gatt != null) {
                gatt.disconnect();
            }
        } else {
            status = true;
            button.setColorFilter(Color.RED);
            textView.setText("Waiting");
            LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
            checkPermissionsAndAccesses();
        }
    }

    private boolean isNLServiceEnabled() {
        ComponentName cn = new ComponentName(getApplicationContext(), NotificationService.class);
        String flat = Settings.Secure.getString(getApplicationContext().getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }

    /////////////
    //LIFECYCLE//
    /////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("status",status);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
    }

    //////////
    //OTHERS//
    //////////

    private void sendNotification(String string) {
        final ArrayList<String> stringList;
        if(string.length() > 20) {
            stringList = getStringList(string);
            for(String s: stringList) {
                Log.i("SmartGlassesSTRINGLIST","String: " + s);
            }
        }
        else {
            stringList = new ArrayList<>();
            stringList.add(string);
        }

        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.i("SmartGlassesNOTIF","Send: " + stringList.get(0));
                byte[] bytes = stringList.get(0).getBytes();
                characteristic.setValue(bytes);
                gatt.writeCharacteristic(characteristic);
                stringList.remove(0);
                if(stringList.isEmpty()) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(task,500,500);
    }

    private ArrayList<String> getStringList(String string) {
        ArrayList<String> stringList = new ArrayList<>();
        String s = string;
        boolean end = false;
        while(!end) {
            if(s.length() >= 20) {
                String substring = string.substring(0,19);
                s = string.substring(19);
                stringList.add(substring);
            }
            else {
                stringList.add(s);
                end = true;
            }
        }
        return stringList;
    }
}
