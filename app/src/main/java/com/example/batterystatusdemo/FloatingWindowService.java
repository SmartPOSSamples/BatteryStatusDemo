package com.example.batterystatusdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class FloatingWindowService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private TextView info;
    private BatteryReceiver receiver;
    private final String TAG = "myTag...";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate...");

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Intent.ACTION_BATTERY_CHANGED));
        Log.d(TAG, "sendBroadcast: ACTION_BATTERY_CHANGED");

        receiver = new BatteryReceiver();
        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, filter);
        Log.d(TAG, "RegisterReceiver");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stopMyService(){
        try{
            Log.d(TAG, "Stop Service");
            windowManager.removeView(floatingView);
            Log.d(TAG, "View removed successfully");
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            Log.d(TAG, "Unregister Receiver successfully");
            Intent intent = new Intent("PROGRAM_STOPPED");
            sendBroadcast(intent);
            stopSelf();
        }catch(Exception e){
            Log.e(TAG, "Error during onDestroyFloatingWindowService");
        }
    }

    public void setWindow(String string){
        if(windowManager == null){
            Log.d(TAG, "setWindow...");
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            floatingView = LayoutInflater.from(this).inflate(R.layout.window, null);
            info = floatingView.findViewById(R.id.info);
            TextView ok = floatingView.findViewById(R.id.ok);

            floatingView.clearFocus();

            final WindowManager.LayoutParams params = setParams();
            windowManager.addView(floatingView, params);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopMyService();
                }
            });
        }
        info.setText(string);
        Log.d(TAG,"setText");
    }

    public WindowManager.LayoutParams setParams(){
        Log.d(TAG, "setWindowParams...");
        final WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

// 设置视图的初始位置
        params.gravity = Gravity.CENTER;

        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);

        params.width = (int) (300 * dm.density);
        params.height = (int) (450 * dm.density);
        params.x = 0;
        params.y = 0;

        return params;
    }

    private class BatteryReceiver extends BroadcastReceiver {
        private String stringInfo = "";
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                Log.d(TAG, "onReceive...");
                update(context, intent);
            }
            else{
                if(intent.ACTION_POWER_CONNECTED.equals(intent.getAction())){
                    Log.d(TAG, "StartCharging...");
                    Toast toast = Toast.makeText(context, "Start Charging!", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d(TAG, "StartChargingSuccessfully!");
                }
                else{
                    Log.d(TAG, "StopCharging...");
                    Toast toast = Toast.makeText(context, "Stop Charging!", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d(TAG, "StopChargingSuccessfully!");
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        private void update(Context context, Intent intent){
            BatteryInfo batteryInfo = new BatteryInfo();
            updateExtra(batteryInfo, intent);
            batteryInfo.updateInfo((BatteryManager) context.getSystemService(Context.BATTERY_SERVICE));
            batteryInfo.setPowerSaveMode((PowerManager) context.getSystemService(Context.POWER_SERVICE));

            stringInfo = batteryInfo.toString();
            setWindow(stringInfo);
        }

        private void updateExtra(BatteryInfo batteryInfo, Intent intent){
            batteryInfo.setStatus(intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1));
            batteryInfo.setChargePlug(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1));
            batteryInfo.setBatteryPresent((intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false) ? "Yes":"No"));
            batteryInfo.setLevel(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
            batteryInfo.setScale(intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
            batteryInfo.setHealth(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
            batteryInfo.setVoltage(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
            batteryInfo.setTemperature((float) (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)/10.0));
            batteryInfo.setTechnology(intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));
        }
    }
}

