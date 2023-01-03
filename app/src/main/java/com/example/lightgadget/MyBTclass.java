package com.example.lightgadget;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MyBTclass extends AppCompatActivity {
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static BluetoothServerSocket bluetoothServerSocket = null;
    static InputStream inputStream = null;
    static OutputStream outputStream = null;
    static BluetoothSocket bluetoothSocket = null;
    int counter; // Counter to check the attempts until being available to connect the socket.
    int counter_10;  // Auxiliary counter to check the attempts until being available to connect the socket.
    String defaultAdress = "94:B9:7E:E4:AB:8A";
    String readMessage = null;

    // Class method to connect the smartphone with the device.
    public boolean connect(Context context, BluetoothAdapter bluetoothAdapter) {
        // RETURN LEGEND: -2 bluetoothAdapter is null ; -1 = bluetoothAdapter is OFF ; 0 = bluetoothSocket not connected ; 1 = bluetoothSocket properly connected.
        // Check if bluetooth is available.
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "This device do not support Bluetooth.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (bluetoothAdapter.isEnabled() && bluetoothSocket == null) { // BluetoothAdapter is ON.
                Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();

                // Create a bluetooth device by its MAC adress.
                BluetoothDevice btDevice = bluetoothAdapter.getRemoteDevice(defaultAdress);
                Toast.makeText(context, "Connecting to device: " + btDevice.getName(), Toast.LENGTH_SHORT).show();

                // Create the socket.
                try {
                    bluetoothSocket = btDevice.createRfcommSocketToServiceRecord(mUUID);
                } catch (IOException e) {
                    Toast.makeText(context, "ERROR: socket creation failed.", Toast.LENGTH_SHORT).show();
                }

                // Create the server socket.
                try {
                    bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BLG", mUUID);
                } catch (IOException e) {
                    Toast.makeText(context, "ERROR: server socket creation failed.", Toast.LENGTH_SHORT).show();
                }

                // Connect the socket.
                socketConnect(bluetoothSocket);
                while (!bluetoothSocket.isConnected()) {
                    Toast.makeText(context, "ERROR: socket connection failed. Attempts: " + counter_10, Toast.LENGTH_SHORT).show();
                    socketConnect(bluetoothSocket);
                }

                if (bluetoothSocket.isConnected()) {
                    String msg = "3/4. Socket connection established.";
                    if (counter > 1) msg = msg + "Attempt: " + counter + "/" + counter_10;

                    // Bluetooth communication need.
                    try {
                        inputStream = bluetoothSocket.getInputStream();
                        outputStream = bluetoothSocket.getOutputStream();
                    } catch (IOException e) {
                        Toast.makeText(context, "ERROR: getting Streams.", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                } else {
                    Toast.makeText(context, "BluetoothSocket not connected properly.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(context, "BluetoothAdapter is OFF.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public boolean disconnect(Context context) {
        Toast.makeText(context, "Disconnecting...", Toast.LENGTH_SHORT).show();

        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Toast.makeText(context, "ERROR: socket not closed.", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            bluetoothServerSocket.close();
        } catch (IOException e) {
            Toast.makeText(context, "ERROR: server socket not closed.", Toast.LENGTH_SHORT).show();
            return false;
        }

        bluetoothSocket = null;
        bluetoothServerSocket = null;

        return true;
    }

    public boolean newConnect(Context context, BluetoothAdapter btAdapter, String MAC) {
        defaultAdress = MAC;
        if (bluetoothSocket == null) return connect(context, btAdapter);
        else {
            boolean disconnected = disconnect(context);
            if (disconnected) return connect(context, btAdapter);
            else return false;
        }
    }

    // Function to connect the socket with several attempts if applicable.
    public void socketConnect(BluetoothSocket bluetoothSocket) {
        counter = 0;
        counter_10 += 10;
        do {
            try {
                bluetoothSocket.connect();
            }
            catch (IOException e) {}
            counter++;
        } while (!bluetoothSocket.isConnected() && counter < 10);
    }

    // Function to send data.
    public void write(String input, Context context) {
        if (bluetoothSocket != null) {
            if (bluetoothSocket.isConnected()) {
                try {
                    outputStream.write(input.getBytes());
                    Toast.makeText(context, "Sent: " + input, Toast.LENGTH_SHORT).show();
                }
                catch (IOException e) {
                    Toast.makeText(context, "ERROR: sending: " + input, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(context, "ERROR: sending: socket is not connected.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "ERROR: sending: socket is not defined.", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to send data.
    public String read(Context context) {
        readMessage = null;
        if (bluetoothSocket != null) {
            if (bluetoothSocket.isConnected()) {
                byte[] buffer = new byte[256];  // buffer store for the stream
                int bytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = inputStream.read(buffer);
                        readMessage = new String(buffer, 0, bytes);
                    } catch (IOException e) {
                        break;
                    }
                }
                Toast.makeText(context, "Read: " + readMessage, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "ERROR: reading: socket is not connected.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "ERROR: reading: socket is not defined.", Toast.LENGTH_SHORT).show();
        }
        return readMessage;
    }
}

