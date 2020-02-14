package be.uhasselt.bleutooth_key_chain;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends NotificationListenerService {

    private Context context;
    private long precedent;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        precedent = Calendar.getInstance().getTime().getTime();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        long difference = Math.abs(Calendar.getInstance().getTimeInMillis() - precedent);
        long seconds = difference/1000;
        if(seconds < 1) {
            Log.i("BT_Key_ChainNOTIF","Too fast");
            return;
        }

        precedent = Calendar.getInstance().getTimeInMillis();

        if(sbn.getTag() == null && !sbn.getPackageName().contains("snapchat")) {
            Log.i("BT_Key_ChainNOTIF","Wrong notif");
            return;
        }

        String title = "";
        String text = "";
        if(sbn.getNotification().extras != null && sbn.getNotification().extras.containsKey("android.title") && sbn.getNotification().extras.containsKey("android.text")) {
            title = sbn.getNotification().extras.getString("android.title");
            title = Objects.requireNonNull(title).replace(" ", "__");
            text = Objects.requireNonNull(sbn.getNotification().extras.getCharSequence("android.text")).toString();
            text = text.replace(" ", "__");
        }

        if(title.charAt(0) == '_') {
            title = title.substring(1);
        }
        if(title.charAt(title.length() - 1) == '_') {
            title = title.substring(0,title.length()-2);
        }
        if(text.charAt(0) == '_') {
            title = title.substring(1);
        }
        if(text.charAt(text.length() - 1) == '_') {
            title = title.substring(0,title.length()-2);
        }


        //Gecontroleerd, niet getest
        if(sbn.getPackageName().contains("facebook.katana")) {
            Log.i("SmartGlassesNOTIF","Facebook");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            sendNotification("Facebook__" + text + "__~~");
        }
        //Goed
        else if(sbn.getPackageName().contains("facebook.orca")) {
            Log.i("SmartGlassesNOTIF","Messenger");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            if(title.contains(",")) {
                sendNotification("Messenger__Groepchat" + text + "__~~");
            }
            else {
                sendNotification("Messenger__" + title + "__~~");
            }
        }
        //Gecontroleerd, maar niet getest
        else if(sbn.getPackageName().contains("twitter")) {
            Log.i("SmartGlassesNOTIF","Twitter");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            sendNotification("Twitter__" + text + "__~~");
        }
        //Goed
        else if(sbn.getPackageName().contains("snapchat")) {
            Log.i("SmartGlassesNOTIF","Snapchat");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            sendNotification("Snapchat__" + text + "__~~");
        }
        //Gecontroleerd, maar niet getest
        else if(sbn.getPackageName().contains("instagram")) {
            Log.i("SmartGlassesNOTIF","Instagram");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            sendNotification("Instagram__" + title + "__~~");
        }
        //Goed
        else if(sbn.getPackageName().contains("outlook")) {
            Log.i("SmartGlassesNOTIF","Outlook");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            sendNotification("Outlook__" + title + "__~~");
        }
        //Gecontroleerd, maar niet getest
        else if(sbn.getPackageName().contains("gm")) {
            Log.i("SmartGlassesNOTIF","Gmail");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            sendNotification("Gmail__" + title + "__~~");
        }
        //Gecontroleerd, maar niet getest
        else if(sbn.getPackageName().contains("whatsapp")) {
            Log.i("SmartGlassesNOTIF","Whatsapp");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            sendNotification("Whatsapp__" + title + "__~~");
        }
        //Goed
        else if(sbn.getPackageName().contains("messaging") || sbn.getPackageName().contains("mms")) {
            Log.i("SmartGlassesNOTIF","Bericht");
            Log.i("SmartGlassesNOTIF",sbn.getPackageName());
            sendNotification("Bericht__" + title + "__~~");
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    private void sendNotification(String string) {
        final ArrayList<String> stringList;
        if(string.length() > 20) {
            stringList = getStringList(string);
            for(String s: stringList) {
                Log.i("BT_Key_ChainSTRINGLIST","String: " + s);
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
                Log.i("BT_Key_ChainNOTIF","Send: " + stringList.get(0));
                Intent intent = new Intent("Msg");
                intent.putExtra("title",stringList.get(0));
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                stringList.remove(0);
                if(stringList.isEmpty()) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(task,3000,3000);
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
