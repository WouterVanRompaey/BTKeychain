package be.uhasselt.bleutooth_key_chain;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity {

    //datamembers
    private ImageView on_off_button;
    private TextView infoText;

    //datamembers of the listview

    private Button btnPaired;
    private ListView devicelist;

    // Bt connection
    private BluetoothAdapter adapter = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    private BluetoothLeScanner scanner;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic characteristic;

    private Handler handler;
    private Runnable runnable;

    private boolean status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        on_off_button = findViewById(R.id.button);
        infoText = findViewById(R.id.textview);
        btnPaired = findViewById(R.id.PDbutton);
        devicelist = (ListView)findViewById(R.id.listView);

        adapter = BluetoothAdapter.getDefaultAdapter();

        if(adapter == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!adapter.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });



/*
        if(savedInstanceState != null && savedInstanceState.containsKey("status")) {
            status = savedInstanceState.getBoolean("status");
            update();
        }

        on_off_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        */
    }


    private void pairedDevicesList()
    {
        pairedDevices = adapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity.
            Intent i = new Intent(MainActivity.this, MainActivity.class);

            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            startActivity(i);
        }
    };
















/*




    ///////////////
    //PERMISSIONS//
    ///////////////

    private void checkPermissionsAndAccesses() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            infoText.setText(getString(R.string.connectionfailed_string));
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
            infoText.setText(getString(R.string.connectionfailed_string));
            return;
        }
        if (!adapter.isEnabled()) {
            infoText.setText(" zit nu in de  adapdter is not enabled sectie " +
                "                 en ik ga zo dadelijk returnen ");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1000);
            return;
        }
/*

        this section makes the app go to a permission location in the phone's settings
        if (!isNLServiceEnabled()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivityForResult(intent, 1000);
        }
*/
/*
        scanBluetooth();

    }

    private void scanBluetooth() {
        infoText.setText(" ga nu scanBT doen");
        scanner = adapter.getBluetoothLeScanner();
        final ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                //infoText.setText("scanning: " + result.getDevice().getName());
                */
                /*if (result.getDevice().getName() != null && result.getDevice().getName().equals("HMSoft") && result.getDevice().getAddress().equals("D8:A9:8B:96:78:7E")) {
                    if(handler != null && runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                    scanner.stopScan(this);

                    Log.i("KeyChainSCANNER", "Found: " + result.getDevice().getAddress());
                    infoText.setText("Found: " + result.getDevice().getName() );
                    if(characteristic !=null && gatt != null) {
                        characteristic.setValue(("tik found").getBytes());
                        gatt.writeCharacteristic(characteristic);
                    }
                    connectBluetoothModule(result.getDevice());
                }else */ /*
                    if (result.getDevice().getName() != null && result.getDevice().getName().equals("HC-06") || result.getDevice().getAddress().equals("98:D3:21:F4:75:F9")) {
                    if(handler != null && runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                    scanner.stopScan(this);

                    Log.i("KeyChainSCANNER", "Found: " + result.getDevice().getAddress());
                    infoText.setText("Found: " + result.getDevice().getName() );
                    if(characteristic !=null && gatt != null) {
                        characteristic.setValue(("tik found").getBytes());
                        gatt.writeCharacteristic(characteristic);
                    }
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
        infoText.setText("Searching...");
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

                    characteristic.setValue(("tik conn").getBytes());
                    gatt.writeCharacteristic(characteristic);
                    Log.i("KeyChainCONNECTION","Connected");
                    infoText.setText("Connected");
                    status = true;
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.i("KeyChainCONNECTION","Disconnected");
                    infoText.setText("Disconnected");
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
                Log.i("BT_Key_ChainBEGIN", "Service: " + service.getUuid().toString());
                Log.i("BT_Key_ChainBEGIN", "Characteristic: " + characteristic.getUuid().toString());
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
            Log.i("KeyChainBCTRECEIVER", "Received");
            String title = intent.getStringExtra("title");
            if (gatt != null && characteristic != null) {
                Log.i("KeyChainBCTRECEIVER", "Wrote");
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
            on_off_button.setColorFilter(Color.BLACK);
            infoText.setText("OFF");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(onNotice);
            if (gatt != null) {
                gatt.disconnect();
            }
        } else {
            status = true;
            on_off_button.setColorFilter(Color.RED);
            infoText.setText("Waiting");
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

        final Timer timerl = new Timer();
        TimerTask taskl = new TimerTask() {
            @Override
            public void run() {
                characteristic.setValue(("tik").getBytes());
                gatt.writeCharacteristic(characteristic);
            }
        };

        timerl.schedule(taskl,500,500);
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

*/

}
