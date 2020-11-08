//package com.example.blesmartbulb;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanResult;
//import android.util.Log;
//
//import java.util.List;
//
//
//public class CallbackScan extends ScanCallback {
//
//    BluetoothAdapter bluetoothAdapter;
//    BluetoothDevice bluetoothDevice;
//
//
//    public CallbackScan() {
//        super();
//    }
//
//    public BluetoothAdapter getBluetoothAdapter() {
//        return bluetoothAdapter;
//    }
//
//    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
//        this.bluetoothAdapter = bluetoothAdapter;
//    }
//
//    public BluetoothDevice getBluetoothDevice() {
//        return bluetoothDevice;
//    }
//
//    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
//        this.bluetoothDevice = bluetoothDevice;
//    }
//
//    @Override
//    public void onScanResult(int callbackType, ScanResult result) {
//        super.onScanResult(callbackType, result);
//        Log.d("demo", result.getDevice().toString());
//        setBluetoothDevice(result.getDevice());
//    }
//
//
//
//    @Override
//    public void onScanFailed(int errorCode) {
//        super.onScanFailed(errorCode);
//    }
//}