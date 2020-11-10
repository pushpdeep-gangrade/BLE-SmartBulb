package com.example.blesmartbulb;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static String uid = "dfb2f1bc-ccb1-2891-7b0f-db6331b3247d";
    public static String bulb_uid = "FB959362-F26E-43A9-927C-7E17D8FB2D8D";
    public static String temp_uid = "0CED9345-B31F-457D-A6A2-B3DB9B03E39A";
    public static String beep_uid = "EC958823-F26E-43A9-927C-7E17D8F32A90";

    String WRITE_ONE = "1";
    String WRITE_ZERO = "0";

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    TextView tv_temp;
    TextView tv_on;
    TextView tv_off;
    TextView beep;
    BluetoothGatt mBluetoothGatt;
    BluetoothDevice bluetoothDevice;
    private int REQUEST_ENABLE_BT = 101;
    private static final long SCAN_PERIOD = 100000;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_temp = findViewById(R.id.tv_temp);
        tv_on = findViewById(R.id.tv_on);
        tv_off = findViewById(R.id.tv_of);
        beep = findViewById(R.id.tv_beep);
        mHandler = new Handler();

        tv_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBluetoothGatt !=null)
                writeData(mBluetoothGatt.getService(UUID.fromString(uid)).getCharacteristic(UUID.fromString(bulb_uid)), WRITE_ONE);
                else
                    Toast.makeText(MainActivity.this, "Bluetooth not connected", Toast.LENGTH_SHORT).show();
            }
        });

        tv_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBluetoothGatt !=null)
                writeData(mBluetoothGatt.getService(UUID.fromString(uid)).getCharacteristic(UUID.fromString(bulb_uid)), WRITE_ZERO);
                else
                    Toast.makeText(MainActivity.this, "Bluetooth not connected", Toast.LENGTH_SHORT).show();
            }
        });
        beep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBluetoothGatt !=null)
                writeData(mBluetoothGatt.getService(UUID.fromString(uid)).getCharacteristic(UUID.fromString(beep_uid)), WRITE_ONE);
                else
                Toast.makeText(MainActivity.this, "Bluetooth not connected", Toast.LENGTH_SHORT).show();

            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,

                    Manifest.permission.ACCESS_COARSE_LOCATION,

                    Manifest.permission.BLUETOOTH,

                    Manifest.permission.BLUETOOTH_ADMIN}, 101);

        } else {
            System.out.println("Location permissions available, starting location");
        }

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
//        else
//            scanLeDevice();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if(mBluetoothGatt != null){
            charactersticNotify(mBluetoothGatt);
        }
       else
        scanLeDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       close();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            scanLeDevice();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPause() {
        super.onPause();
        //scanLeDevice(false);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scanLeDevice() {
        final List<ScanFilter> scanFilterList = new ArrayList<>();
        ScanFilter filter = new ScanFilter.Builder()
                .setDeviceAddress("BC:2F:3D:3E:4E:F1")
                .build();
        scanFilterList.add(filter);

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setReportDelay(0L)
                .build();


        if (!mScanning) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanFilterList, scanSettings, leScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);
        }
    }


    private ScanCallback leScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            bluetoothDevice = result.getDevice();
            connect(bluetoothDevice);
            Log.d("demo", result.getDevice().toString());
        }
    };

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Location permissions granted, starting location");
            } else
                finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void connect(BluetoothDevice device) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(leScanCallback);

        final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.i("onConnectionStateChange", "Status: " + status);
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        Log.d("demo", "STATE_CONNECTED");
                        gatt.discoverServices();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        scanLeDevice();
                        clearUi();
                        break;
                    default:
                        Log.d("demo", "STATE_OTHER");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                List<BluetoothGattService> services = gatt.getServices();
                Log.i("onServicesDiscovered", services.toString());

                BluetoothGattCharacteristic tempCharacterstics = gatt.getService(UUID.fromString(uid)).getCharacteristic(UUID.fromString(temp_uid));

                Log.i("demo", tempCharacterstics.getUuid().toString());
                Boolean enabled = true;
                gatt.setCharacteristicNotification(tempCharacterstics, enabled);

            }


            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    final String s = new String(data, StandardCharsets.UTF_8);
                    Log.d("demo", "On Char read" + s);
                }
                Log.d("demo", "On char read" + characteristic.getUuid().toString());
                Log.d("demo", "On char read" + characteristic.toString());
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);

                Log.d("demo", characteristic.getUuid().toString());
                String s = new String(characteristic.getValue(), StandardCharsets.UTF_8);
                Log.d("demo", s);

            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    final String s = new String(data, StandardCharsets.UTF_8);
                    text(s);
                }
            }
        };

        mBluetoothGatt = device.connectGatt(this, false, gattCallback);
    }

    public void text(final String t) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_temp.setText(t + " \u2109");
            }
        });
    }

    public void clearUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Bluetooth Disconnected", Toast.LENGTH_SHORT).show();
                tv_temp.setText("");
            }
        });
    }

    public void writeData(BluetoothGattCharacteristic characteristic, String value) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w("demo", "BluetoothAdapter not initialized");
            return;
        }
        characteristic.setValue(value);
        Log.d("demo", value);
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void charactersticNotify(BluetoothGatt gatt){

        gatt.setCharacteristicNotification(gatt.getService(UUID.fromString(uid)).getCharacteristic(UUID.fromString(temp_uid)), true);
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
      //  mBluetoothGatt.disconnect();
        mBluetoothGatt.close();

        mBluetoothGatt = null;
    }


}