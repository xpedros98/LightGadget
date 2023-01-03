package com.example.lightgadget;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class Dashboard extends AppCompatActivity {
    // GUI objects.
    RelativeLayout relativeLayout;
    Button exitBtn, refreshBtn, sendBtn, eraseBtn;
    TextView feedback, lastTv;
    com.google.android.material.textfield.TextInputEditText to_send;
    ListView devicesList;
    int scrollAmount = 0;

    // Animations.
    Animation refresh_rev;

    // Bluetooth related.
    public MyBTclass bt = new MyBTclass();
    boolean btConnected = false;
    Set<BluetoothDevice> pairedDevices;
    BluetoothAdapter bluetoothAdapter;

    int refresh_counter; // Counter for the refresh function to know the first iteration to show special feedback.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        relativeLayout = findViewById(R.id.layout_Dashboard);

        // GUI definitions.
        refreshBtn = findViewById(R.id.refresh);
        eraseBtn = findViewById(R.id.erase);
        exitBtn = findViewById(R.id.exit_dashboard);
        sendBtn = findViewById(R.id.send);
        feedback = findViewById(R.id.feedback);
        feedback.setMovementMethod(new ScrollingMovementMethod());
        to_send = findViewById(R.id.to_send);
        devicesList = findViewById(R.id.devices_list);

        // Animations
        refresh_rev = AnimationUtils.loadAnimation(this, R.anim.rotate_360);

        // Print the date on the log.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" * * dd/MM/yyyy - HH:mm:ss * * ");
        Date d = Calendar.getInstance().getTime();
        String timestamp = simpleDateFormat.format(d);
        feedback.append(timestamp + "\n");



        // Create the bluetooth adapter if required.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getBaseContext(), "ERROR: Bluetooth adapter is not available.", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) { // Is turned ON.
                Toast.makeText(getBaseContext(), "Bluetooth is already avilable.", Toast.LENGTH_SHORT).show();
            } else { // Is turned OFF.
                // Ask user to activate bluetooth.
                Intent intent_BT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent_BT, 1);
            }
        }

        // Buttons callbacks.
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Trying to send...", Toast.LENGTH_SHORT).show();
                bt.write(Objects.requireNonNull(to_send.getText()).toString(), getApplicationContext());
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPairedDevices();
                refreshBtn.startAnimation(refresh_rev);
            }
        });

        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Print the date on the log.
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" * * dd/MM/yyyy - HH:mm:ss * * ");
                Date d = Calendar.getInstance().getTime();
                String timestamp = simpleDateFormat.format(d);
                feedback.setText(timestamp);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.up_animation, R.anim.null_animation);
                finish();
            }
        });

        refreshPairedDevices();
    }

    // Back button callback.
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.null_animation, R.anim.up_animation);
        finish();
    }

    // Function to refresh the paired devices.
    public void refreshPairedDevices() {
        refresh_counter += 1;
        // Initialize Array adapter that requires a .xml file (new "Layout resource file" in the "layout" directory) with a single TextView.
        ArrayAdapter devicesArray = new ArrayAdapter(Dashboard.this, R.layout.devices_tv);

        // Initialize listView.
        devicesList.setAdapter(devicesArray);
        devicesList.setOnItemClickListener(mDeviceClickListener);

        // Request required permissions.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
        }

        // Get the linked BT devices.
        pairedDevices = bluetoothAdapter.getBondedDevices();

        // Set the linked BT devices to the listView.
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device: pairedDevices) {
                devicesArray.add(" " + device.getName() + "\n  " + device.getAddress());
            }
            logFeedback("Paired devices refreshed.");
        }
    }

    // List item click callback.
    final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            if (lastTv != null) {
                lastTv = (TextView) v; // Update lastTv.
                lastTv.setTypeface(null, Typeface.BOLD);
            }
            // Get MAC adress from device (last 17 characters of the item from the listView).
            String info = ((TextView) v).getText().toString();
            String name = info.substring(0, info.length() - 17);
            String MAC = info.substring(info.length() - 17);
            logFeedback("Selected device: " + name);
            btConnected = bt.newConnect(getApplicationContext(), bluetoothAdapter, MAC);
            if (btConnected) {
                Toast.makeText(getApplicationContext(), "Connected to "+name, Toast.LENGTH_SHORT).show();
                logFeedback("New connection managed successfully.");
                bt.write("0", getApplicationContext());
//                String answer = bt.read(getApplicationContext());
//                while(answer == null) {
//                    answer = bt.read(getApplicationContext());
//                }
//                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "ERROR: not connected to"+name, Toast.LENGTH_SHORT).show();
                logFeedback("ERROR: new connection not managed properly.");
            }
        }
    };

    // Append the feedback to its own tv and scrolls to end.
    public void logFeedback(String msg) {
        feedback.append(">> " + msg + "\n");

        try {
        scrollAmount = feedback.getLayout().getLineTop(feedback.getLineCount()) - feedback.getHeight();
        }
        catch (Exception e) {}

        if (scrollAmount > 0) {
            feedback.scrollTo(0, scrollAmount);
        } else {
            feedback.scrollTo(0, 0);
        }
    }


}
