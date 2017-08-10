package com.example.semsturgut.snakearmandroidapp;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final int SPEECH_RECOGNITION_CODE = 1;

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter;

    ArrayList<BluetoothDevice> pairedDeviceArrayList;

    ListView listViewPairedDevice;
    Button btn_Speak, btn_Home, btn_Move1, btn_Move2, btn_Beer;
    EditText editID, editDuration;
    SeekBar seekPosition;
    TextView textPosition;

    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    String str2Send;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewPairedDevice = (ListView) findViewById(R.id.pairedlist);
        btn_Speak = (Button) findViewById(R.id.speak_btn);
        btn_Home = (Button) findViewById(R.id.home_btn);
        btn_Move1 = (Button) findViewById(R.id.btn_move1);
        btn_Move2 = (Button) findViewById(R.id.btn_move2);
        btn_Beer = (Button) findViewById(R.id.btn_beer);
        editID = (EditText) findViewById(R.id.edit_ID);
        editDuration = (EditText) findViewById(R.id.edit_Duration);
        seekPosition = (SeekBar) findViewById(R.id.seek_Pos);
        textPosition = (TextView) findViewById(R.id.textPos);

        seekPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                if (myThreadConnected != null) {
                    textPosition.setText(String.valueOf(progress));
                    str2Send = editID.getText().toString() + "," + progress + "," + editDuration.getText().toString() + "," + "#";
                    Log.i("Data", str2Send);
                    myThreadConnected.write(str2Send.getBytes());
                }
            }
        });

        btn_Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myThreadConnected != null) {
                    str2Send = "10" + "," + "10" + "," + "1500" + "," + "#";
                    Log.i("Data", str2Send);
                    myThreadConnected.write(str2Send.getBytes());
                }
            }
        });

        btn_Move1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myThreadConnected != null) {
                    str2Send = "11" + "," + "10" + "," + "1500" + "," + "#";
                    Log.i("Data", str2Send);
                    myThreadConnected.write(str2Send.getBytes());
                }
            }
        });

        btn_Move2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myThreadConnected != null) {
                    str2Send = "12" + "," + "10" + "," + "1500" + "," + "#";
                    Log.i("Data", str2Send);
                    myThreadConnected.write(str2Send.getBytes());
                }
            }
        });

        btn_Beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myThreadConnected != null) {
                    str2Send = "13" + "," + "10" + "," + "1500" + "," + "#";
                    Log.i("Data", str2Send);
                    myThreadConnected.write(str2Send.getBytes());
                }
            }
        });

        btn_Speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Turn ON BlueTooth if it is OFF
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        setup();
    }

    private void setup() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
            }

            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);

            listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BluetoothDevice device =
                            (BluetoothDevice) parent.getItemAtPosition(position);
                    Toast.makeText(MainActivity.this,
                            "Name: " + device.getName() + "\n"
                                    + "Address: " + device.getAddress() + "\n"
                                    + "BondState: " + device.getBondState() + "\n"
                                    + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                                    + "Class: " + device.getClass(),
                            Toast.LENGTH_LONG).show();

                    myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myThreadConnectBTdevice != null) {
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
//                finish();
            }
        }

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    if (text.toLowerCase().contains("up")) {
                        if (myThreadConnected != null) {
                            textPosition.setText(text);
                            str2Send = "6" + "," + "400" + "," + "1000" + "," + "#";
                            Log.i("Data", str2Send);
                            myThreadConnected.write(str2Send.getBytes());
                        }
                    } else if (text.toLowerCase().contains("down")) {
                        if (myThreadConnected != null) {
                            textPosition.setText(text);
                            str2Send = "6" + "," + "600" + "," + "1000" + "," + "#";
                            Log.i("Data", str2Send);
                            myThreadConnected.write(str2Send.getBytes());
                        }
                    } else if (text.toLowerCase().contains("thank")) {
                        textPosition.setText(R.string.ure_welcome);
                    }
                }
                break;
            }
        }
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket) {

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                Log.i("bluetoothSocket", String.valueOf(bluetoothSocket));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.i("bluetoothSocket.connect", eMessage);
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if (success) {
                //connect successful
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, msgconnected, Toast.LENGTH_LONG).show();
                        listViewPairedDevice.setVisibility(View.GONE);
                    }
                });

                startThreadConnected(bluetoothSocket);

            } else {
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /*
    ThreadConnected:
    Background Thread to handle Bluetooth data communication
    after connected
     */
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    final String strReceived = new String(buffer, 0, bytes);
                    final String strByteCnt = String.valueOf(bytes) + " bytes received.\n";

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i("Bytes Received", strReceived + "" + strByteCnt);
                        }
                    });

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i("ConnectionLost", msgConnectionLost);
                        }
                    });
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}