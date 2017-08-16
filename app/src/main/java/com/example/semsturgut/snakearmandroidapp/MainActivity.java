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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final int SPEECH_RECOGNITION_CODE = 1;

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter;

    ArrayList<BluetoothDevice> pairedDeviceArrayList;

    ListView listViewPairedDevice;
    LinearLayout splashLayout;
    ImageView leftView, rightView, upView, downView, openClaw, closeClaw, menuView, speechView;
    Button btn_Speak, btn_Home, btn_Move1, btn_Move2, btn_Beer, sendButton;
    EditText editID;
    EditText editDuration;
    EditText editZAxis;
    SeekBar seekPosition;
    TextView textPosition;

    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    String str2Send;

    double arm_distances[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 14, 14}, {0, 14.5, 28.5, 28.5, 11}};
    int xyz_position[] = {511, 511, 511};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splashLayout = (LinearLayout) findViewById(R.id.splashLayout);
        listViewPairedDevice = (ListView) findViewById(R.id.pairedlist);
        leftView = (ImageView) findViewById(R.id.leftView);
        rightView = (ImageView) findViewById(R.id.rightView);
        upView = (ImageView) findViewById(R.id.upView);
        downView = (ImageView) findViewById(R.id.downView);
        openClaw = (ImageView) findViewById(R.id.openClaw);
        closeClaw = (ImageView) findViewById(R.id.closeClaw);
        menuView = (ImageView) findViewById(R.id.menuView);
        speechView = (ImageView) findViewById(R.id.speechView);

        editZAxis = (EditText) findViewById(R.id.position_edit_text);
        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (myThreadConnected != null) {
                    str2Send = Arrays.toString(func_Kinematics(Double.parseDouble(editZAxis.getText().toString()))) + "," + "#";
                    str2Send = remove(str2Send,'[');
                    str2Send = remove(str2Send,' ');
                    str2Send = remove(str2Send,']');
                    Log.i("Data", str2Send);
                    myThreadConnected.write(str2Send.getBytes());
                }
            }
        });

        leftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Left", "Pressed!");
            }
        });

        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Right", "Pressed!");
            }
        });

        upView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Up", "Pressed!");
            }
        });

        downView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Down", "Pressed!");
            }
        });

        openClaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Open", "Pressed!");
            }
        });

        closeClaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Close", "Pressed!");
            }
        });

        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Menu", "Pressed!");
            }
        });

        speechView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechToText();
            }
        });

//        btn_Speak = (Button) findViewById(R.id.speak_btn);
//        btn_Home = (Button) findViewById(R.id.home_btn);
//        btn_Move1 = (Button) findViewById(R.id.btn_move1);
//        btn_Move2 = (Button) findViewById(R.id.btn_move2);
//        btn_Beer = (Button) findViewById(R.id.btn_beer);
//        editID = (EditText) findViewById(R.id.edit_ID);
//        editDuration = (EditText) findViewById(R.id.edit_Duration);
//        seekPosition = (SeekBar) findViewById(R.id.seek_Pos);
//        textPosition = (TextView) findViewById(R.id.textPos);

//        seekPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // TODO Auto-generated method stub
//                if (myThreadConnected != null) {
//                    textPosition.setText(String.valueOf(progress));
//                    str2Send = editID.getText().toString() + "," + progress + "," + editDuration.getText().toString() + "," + "#";
//                    Log.i("Data", str2Send);
//                    myThreadConnected.write(str2Send.getBytes());
//                }
//            }
//        });

//        btn_Home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (myThreadConnected != null) {
//                    str2Send = "10" + "," + "10" + "," + "1500" + "," + "#";
//                    Log.i("Data", str2Send);
//                    myThreadConnected.write(str2Send.getBytes());
//                }
//            }
//        });

//        btn_Move1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (myThreadConnected != null) {
//                    str2Send = "11" + "," + "10" + "," + "1500" + "," + "#";
//                    Log.i("Data", str2Send);
//                    myThreadConnected.write(str2Send.getBytes());
//                }
//            }
//        });

//        btn_Move2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (myThreadConnected != null) {
//                    str2Send = "12" + "," + "10" + "," + "1500" + "," + "#";
//                    Log.i("Data", str2Send);
//                    myThreadConnected.write(str2Send.getBytes());
//                }
//            }
//        });

//        btn_Beer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (myThreadConnected != null) {
//                    str2Send = "13" + "," + "10" + "," + "1500" + "," + "#";
//                    Log.i("Data", str2Send);
//                    myThreadConnected.write(str2Send.getBytes());
//                }
//            }
//        });

//        btn_Speak.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startSpeechToText();
//            }
//        });


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

    private static String remove(String input, char c) {

        if (input == null || input.length() <= 1)
            return input;
        char[] inputArray = input.toCharArray();
        char[] outputArray = new char[inputArray.length];
        int outputArrayIndex = 0;
        for (int i = 0; i < inputArray.length; i++) {
            char p = inputArray[i];
            if (p != c) {
                outputArray[outputArrayIndex] = p;
                outputArrayIndex++;
            }

        }
        return new String(outputArray, 0, outputArrayIndex);

    }

    public int[] func_Kinematics(double z) {
        double y1a = arm_distances[1][2];
        double y1b = arm_distances[1][3];
        double y1c = arm_distances[1][4];
        double z1a = arm_distances[2][2];
        double z1b = arm_distances[2][3];
        double z1c = arm_distances[2][4];

        double pos1a = xyz_position[0];
        double pos1b = xyz_position[1];
        double pos1c = xyz_position[2];

        double yadif = y1a;
        double ybdif = y1b;
        double ycdif = y1c;
        double zadif = z1a - z;
        double zbdif = z1b - z;
        double zcdif = z1c - z;
        double angle1;
        long newpos1, newpos2;

        if (Math.abs(zbdif) <= 17.5) {
            angle1 = Math.asin((z - z1b) / 17.5) * 180 / Math.PI;
            newpos1 = Math.round(-2.22 * angle1 + 511);
            xyz_position[2] = (int) newpos1;
            arm_distances[2][4] = z;
            arm_distances[1][4] = Math.sqrt(Math.pow(17.5, 2) - Math.pow(z - z1b, 2)) + y1b;
            return xyz_position;
        } else if (Math.abs(zadif) <= 31 && arm_distances[2][4] != 60) {
            newpos1 = 511;
            xyz_position[2] = (int) newpos1;
            angle1 = Math.asin((z - z1a) / 31) * 180 / Math.PI;
            newpos2 = Math.round(-2.22 * angle1 + 511);
            xyz_position[1] = (int) newpos2;
            arm_distances[2][4] = z;
            arm_distances[1][4] = Math.sqrt(Math.pow(31, 2) - Math.pow(z - z1a, 2)) + y1a;
            double z2 = Math.sin(angle1 * Math.PI / 180) * 13.5;
            double z1 = Math.sin(angle1 * Math.PI / 180) * 31;
            double z3 = z - (z1 - z2);
            arm_distances[2][3] = z3;
            return xyz_position;
        } else if (Math.abs(zadif) <= 31 && arm_distances[2][4] == 60) {
            arm_distances[1][4] = 14;
            arm_distances[1][3] = 14;
            arm_distances[2][4] = 11;
            arm_distances[2][3] = 28.5;
            xyz_position[1] = 311;
            xyz_position[2] = 311;
            return xyz_position;
        } else if (zadif < -31) {
            newpos1 = 511;
            xyz_position[2] = (int) newpos1;
            newpos2 = 511;
            xyz_position[1] = (int) newpos2;
            arm_distances[2][4] = 60;
            arm_distances[2][3] = 42.5;
            arm_distances[1][4] = 0;
            arm_distances[1][3] = 0;
            return xyz_position;
        } else if (zadif > 31) {
            newpos1 = 311;
            xyz_position[2] = (int) newpos1;
            newpos2 = 311;
            xyz_position[1] = (int) newpos2;
            arm_distances[2][4] = 11;
            arm_distances[2][3] = 28.5;
            arm_distances[1][4] = 14;
            arm_distances[1][3] = 14;
            return xyz_position;
        }

        return xyz_position;
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
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
//                    if (text.toLowerCase().contains("up")) {
//                        if (myThreadConnected != null) {
//                            textPosition.setText(text);
//                            str2Send = "6" + "," + "400" + "," + "1000" + "," + "#";
//                            Log.i("Data", str2Send);
//                            myThreadConnected.write(str2Send.getBytes());
//                        }
//                    } else if (text.toLowerCase().contains("down")) {
//                        if (myThreadConnected != null) {
//                            textPosition.setText(text);
//                            str2Send = "6" + "," + "600" + "," + "1000" + "," + "#";
//                            Log.i("Data", str2Send);
//                            myThreadConnected.write(str2Send.getBytes());
//                        }
//                    } else if (text.toLowerCase().contains("thank")) {
//                        textPosition.setText(R.string.ure_welcome);
//                    }
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
                        splashLayout.setVisibility(View.GONE);
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


/* Old XML

           <EditText
                android:id="@+id/edit_ID"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_above="@+id/textPos"
                android:layout_alignParentLeft="true"
                android:layout_alignParentStart="true"
                android:ems="10"
                android:hint="@string/servo_id"
                android:inputType="number"
                tools:layout_editor_absoluteY="25dp"
                tools:layout_editor_absoluteX="8dp" />

            <Button
                android:id="@+id/speak_btn"
                android:layout_width="88dp"
                android:layout_height="wrap_content"
                android:layout_above="@+id/edit_ID"
                android:layout_centerHorizontal="true"
                android:text="@string/speech2text"
                android:visibility="visible"
                tools:layout_editor_absoluteY="82dp"
                tools:layout_editor_absoluteX="252dp" />

            <EditText
                android:id="@+id/edit_Duration"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_above="@+id/textPos"
                android:layout_alignParentLeft="true"
                android:layout_alignParentStart="true"
                android:ems="10"
                android:hint="@string/duration"
                android:inputType="number"
                tools:layout_editor_absoluteY="82dp"
                tools:layout_editor_absoluteX="8dp" />

            <Button
                android:id="@+id/home_btn"
                android:layout_width="88dp"
                android:layout_height="wrap_content"
                android:layout_alignParentLeft="true"
                android:layout_alignParentStart="true"
                android:layout_below="@+id/seek_Pos"
                android:text="@string/home_position"
                android:visibility="visible"
                tools:layout_editor_absoluteY="16dp"
                tools:layout_editor_absoluteX="252dp" />

            <SeekBar
                android:id="@+id/seek_Pos"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_centerHorizontal="true"
                android:layout_centerVertical="true"
                android:layout_margin="10dp"
                android:max="1023"
                android:progress="512"
                tools:targetApi="o"
                android:visibility="gone"
                tools:layout_editor_absoluteY="25dp"
                tools:layout_editor_absoluteX="8dp" />

            <TextView
                android:id="@+id/textPos"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_above="@+id/seek_Pos"
                android:layout_centerHorizontal="true"
                android:text="@string/position"
                android:textSize="30sp"
                android:visibility="gone"
                tools:layout_editor_absoluteY="25dp"
                tools:layout_editor_absoluteX="0dp" />
            <Button
                android:id="@+id/btn_move1"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_below="@+id/home_btn"
                android:layout_centerHorizontal="true"
                android:text="@string/movement_1"
                android:visibility="gone"
                tools:layout_editor_absoluteY="25dp"
                tools:layout_editor_absoluteX="8dp" />

            <Button
                android:id="@+id/btn_move2"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_below="@+id/btn_move1"
                android:layout_centerHorizontal="true"
                android:text="@string/movement_2"
                android:visibility="gone"
                tools:layout_editor_absoluteY="25dp"
                tools:layout_editor_absoluteX="8dp" />

            <Button
                android:id="@+id/btn_beer"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_below="@+id/btn_move2"
                android:layout_centerHorizontal="true"
                android:text="@string/beer"
                android:visibility="gone"
                tools:layout_editor_absoluteY="25dp"
                tools:layout_editor_absoluteX="8dp" />
 */