package com.madebymad.bluetoothcommand;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.madebymad.bluetoothcommand.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class CommunicationActivity extends Activity {
	private static final String TAG = "CommunicationActivity";
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private String deviceAddress;
	private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket btSocket;
    private OutputStream outStream;
    private Button btnSendMessage;  
    private EditText txtMessage;
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Bundle extras = getIntent().getExtras();
        btnSendMessage = (Button)findViewById(R.id.btnSendMessage);
        txtMessage = (EditText)findViewById(R.id.txtChars);
        if (extras != null) {
            deviceAddress = extras.getString("address");
        }
        if(deviceAddress == null)
        {
        	return;
        }        
        //Toast.makeText(getApplicationContext(), deviceAddress, Toast.LENGTH_SHORT).show();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        Toast.makeText(getApplicationContext(), device.getName() + "(" + deviceAddress + ")", Toast.LENGTH_SHORT).show();
        try {
        	btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
        	btSocket.connect();
        	outStream = btSocket.getOutputStream();
        }
        catch(IOException e) {
        	Log.d(TAG, e.getMessage());
        	Toast.makeText(getApplicationContext(), "Unable to connect!", Toast.LENGTH_SHORT).show();
        }
        
        btnSendMessage.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SendMessage();
			}
		});                
    }   
    
    private void SendMessage() {
    	if(outStream == null) {
    		return;
    	}
    		
		String message = txtMessage.getText().toString();
		byte[] msgBuffer = message.getBytes();
		try {			
			outStream.write(msgBuffer);
			Log.i(TAG, "Message written");
		} catch (IOException e) {
			Log.e(TAG, "Exception during write.", e);
		}	
    }
    

}
