package com.example.batterystatusdemo;

import android.annotation.SuppressLint;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class BatteryInfo {
    private BatteryManager mBatteryManager;
    private int status;
    private int chargePlug;
    private boolean usbCharge;
    private boolean acCharge;
    private String batteryPresent;
    private int level;
    private int scale;
    private int battery;
    private int capacity;
    private String health = "";
    private int voltage;
    private float temperature;
    private String technology;
    private long chargeCounter;
    private long energyCounter;
    private long currentNow;
    private long currentAverage;
    private long chargeTimeRemaining;
    private boolean powerSaveMode;
    private final String TAG = "myBatteryInfo";

    public void setStatus(int status) {
        this.status = status;
    }

    public void setChargePlug(int chargePlug) {
        this.chargePlug = chargePlug;
    }

    public void setUsbCharge(boolean usbCharge) {
        this.usbCharge = usbCharge;
    }

    public void setAcCharge(boolean acCharge) {
        this.acCharge = acCharge;
    }

    public void setBatteryPresent(String batteryPresent) {
        this.batteryPresent = batteryPresent;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setBattery(int battery){
        this.battery = battery;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setHealth(int health) {
        switch (health){
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                this.health = "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                this.health = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                this.health = "OverHeat";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                this.health = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                this.health = "OverVoltage";
                break;
            default:
                this.health = "Failed";
                break;
        }
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public void setChargeCounter(long chargeCounter) {
        this.chargeCounter = chargeCounter;
    }

    public void setEnergyCounter(long energyCounter) {
        this.energyCounter = energyCounter;
    }

    public void setCurrentNow(long currentNow) {
        this.currentNow = currentNow;
    }

    public void setCurrentAverage(long currentAverage) {
        this.currentAverage = currentAverage;
    }

    public void setChargeTimeRemaining(long chargeTimeRemaining) {
        this.chargeTimeRemaining = chargeTimeRemaining;
    }

    public void setPowerSaveMode(PowerManager powerManager) {
        this.powerSaveMode = powerManager.isPowerSaveMode();
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.P)
    public synchronized void updateInfo(BatteryManager mBatteryManager){
        try{
            Log.d(TAG, "updateInfo...");

            setUsbCharge(chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
            setAcCharge(chargePlug == BatteryManager.BATTERY_PLUGGED_AC);
            setBattery((int)(this.level * 100 / (float) this.scale));
            setCapacity(mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
            setChargeCounter(mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER));
            setEnergyCounter(mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER));
            setCurrentNow(mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW));
            setCurrentAverage(mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && mBatteryManager.computeChargeTimeRemaining() != -1) {
                setChargeTimeRemaining(mBatteryManager.computeChargeTimeRemaining());
            }
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    public synchronized String toString() {
        Log.d(TAG, "toString...");
        StringBuilder batteryInfoBuilder = new StringBuilder();

        if (status == BatteryManager.BATTERY_STATUS_UNKNOWN || status == -1){
            batteryInfoBuilder.append("Waiting...");
        } else{
            if (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL){
                batteryInfoBuilder.append("Charging Status: Charging\n");
            } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                batteryInfoBuilder.append("Charging Status: Discharging\n");
            } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                batteryInfoBuilder.append("Charging Status: Not charging\n");
            }

            if (chargePlug != -1){
                batteryInfoBuilder.append("Charging over USB: ").append((usbCharge) ? "Yes" : "No").append("\n");
                batteryInfoBuilder.append("Charging over AC: ").append((acCharge) ? "Yes" : "No").append("\n");
            }

            batteryInfoBuilder.append("Battery Present: ").append(batteryPresent).append("\n");

            if (battery < 0 || battery > 100 || scale == -1 || level == -1){
                Log.e(TAG, "'Battery' is incorrect!");
                batteryInfoBuilder.append("Battery: Invalid Value\n");
            }else {
                batteryInfoBuilder.append("Battery: ").append(battery).append("%\n");
            }

            if (capacity < 0 || capacity > 100){
                Log.e(TAG, "'Capacity' is incorrect!");
                batteryInfoBuilder.append("Capacity: Invalid Value\n");
            }
            else {
                batteryInfoBuilder.append("Capacity: ").append(capacity).append("%\n");
            }

            if(health.equals("Failed")){
                Log.e(TAG, "'Battery Health' is incorrect!");
            }
            batteryInfoBuilder.append("Battery Health: ").append(health).append("\n");

            if((3 <= voltage  && voltage <= 10)){
                batteryInfoBuilder.append("Voltage: ").append(voltage).append("V\n");
            } else if (3000 <= voltage && voltage <= 10000) {
                batteryInfoBuilder.append("Voltage: ").append(voltage/1000.0).append("V\n");
            }else {
                Log.e(TAG, "'Voltage' is incorrect!");
                batteryInfoBuilder.append("Voltage: Invalid Value\n");
            }

            if (temperature < 0 || temperature > 100){
                Log.e(TAG, "'Temperature' is incorrect!");
                batteryInfoBuilder.append("Temperature: Invalid Value\n");
            }
            else {
                batteryInfoBuilder.append("Temperature: ").append(temperature).append("℃\n");
            }

            if(technology == null || technology.isEmpty()){
                batteryInfoBuilder.append("Technology: Unknown\n");
            }else {
                batteryInfoBuilder.append("Technology: ").append(technology).append("\n");
            }

            if(chargeCounter < 0 || chargeCounter > 10000000){//0~10000mA
                Log.e(TAG, "'Charge Counter' is incorrect!");
            }
            else {
                batteryInfoBuilder.append("Charge Counter: ").append(String.format("%3.3f", chargeCounter / 1000000.0)).append("Wh\n");
            }

            if(energyCounter < 0 || energyCounter > 100000){
                Log.e(TAG, "'Energy Counter' is incorrect!");
            }
            else {
                batteryInfoBuilder.append("Energy Counter: ").append(String.format("%3.3f", energyCounter / 1000000000.0)).append("Wh\n");
            }

            if(Math.abs(currentNow) <10000 || Math.abs(currentNow) > 10000000){//10mA~10A
                Log.e(TAG, "'Current Now' is incorrect!");
            }
            else {
                batteryInfoBuilder.append("Current Now: ").append(String.format("%3.3f", currentNow / 1000000.0)).append("A\n");
            }
            if(Math.abs(currentAverage) < 1000 || Math.abs(currentAverage) > 10000000){//1mA~10A
                Log.e(TAG, "'Current Average' is incorrect!");
            }
            else {
                batteryInfoBuilder.append("Current Average: ").append(String.format("%3.3f", currentAverage / 1000000.0)).append("A\n");
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && chargeTimeRemaining != -1) {
                batteryInfoBuilder.append("Charge Time Remaining: ").append(chargeTimeRemaining).append("ms\n");
                if(chargeTimeRemaining <= 60000 && chargeTimeRemaining >= 0){
                    batteryInfoBuilder.append("Charge Time Remaining: ").append(String.format("%3.3f", chargeTimeRemaining / 1000.0)).append("s\n");

                }else {
                    int seconds = (int) (chargeTimeRemaining / 1000);
                    int minute = seconds / 60;
                    int restSeconds = seconds - (minute * 60);
                    if(minute >= 60){
                        int hour = minute / 60;
                        int restMinute = minute - (hour * 60);
                        batteryInfoBuilder.append("Charge Time Remaining: ").append(String.format("%d h %d min %d s\n", hour, restMinute, restSeconds));
                    }
                    else{
                        batteryInfoBuilder.append("Charge Time Remaining: ").append(String.format("%d min %d s\n", minute, restSeconds));

                    }
                }
            }
            else {
                batteryInfoBuilder.append("Charge Time Remaining: Unknown\n");
            }

            batteryInfoBuilder.append("Power Save Mode: ").append(powerSaveMode ? "Enabled" : "Disabled").append("\n");
        }
        String stringInfo = batteryInfoBuilder.toString();
        Log.i(TAG, stringInfo);
        return stringInfo;
    }
}
