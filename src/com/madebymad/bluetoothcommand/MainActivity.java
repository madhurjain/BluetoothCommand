package com.madebymad.bluetoothcommand;

import java.util.ArrayList;

import com.madebymad.bluetoothcommand.R;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String tag = "MainActivity";
	private static int REQUEST_ENABLE_BT = 101;
    ListView listDevicesFound;
    ArrayList<String> devices;
    Button btnScanDevice;
    Button btnStartListening;
 	BluetoothAdapter bluetoothAdapter;
 	ArrayAdapter<String> btArrayAdapter;
 	ProgressBar progressScanning;
 	IntentFilter btActionFilter; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
        	Toast.makeText(getApplicationContext(), "Bluetooth is not available",Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        devices = new ArrayList<String>();
        btnScanDevice = (Button)findViewById(R.id.btnScan);
        btnStartListening = (Button)findViewById(R.id.btnStartListening);
        progressScanning = (ProgressBar)findViewById(R.id.progressBarScanning);
        
        btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listDevicesFound = (ListView)findViewById(R.id.lvDevicesFound);
        listDevicesFound.setAdapter(btArrayAdapter);
        btnScanDevice.setOnClickListener(btnScanDevice_OnClick);
        btActionFilter = new IntentFilter();
        btActionFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btActionFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(ActionReceiver, btActionFilter);
        
        btnStartListening.setOnClickListener(new Button.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent listeningIntent = new Intent(getBaseContext(), ListeningActivity.class);
				startActivity(listeningIntent);				
			}
		});
        listDevicesFound.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {					
				Intent intent = new Intent(getBaseContext(), CommunicationActivity.class);
				intent.putExtra("address", devices.get(position));
				startActivity(intent);
			}        	
		});
        
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.i(tag, "onPause()");
    	bluetoothAdapter.cancelDiscovery();
    	progressScanning.setVisibility(View.GONE);
		btnScanDevice.setText(R.string.scan_bluetooth);
		btnScanDevice.setEnabled(true);
    	try
    	{
    		unregisterReceiver(ActionReceiver);
    	}
    	catch(Exception e)
    	{}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.i(tag, "onResume()");
    	try
    	{
    		registerReceiver(ActionReceiver, btActionFilter);
    	}
    	catch(Exception e)
    	{}
    }
    
    @Override
    public void onDestroy() {
	   super.onDestroy();
	   Log.i(tag, "onDestroy()");
	   bluetoothAdapter.cancelDiscovery();
	   try
	   {
		   unregisterReceiver(ActionReceiver);
	   }
	   catch(Exception e)
	   {}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    }
    
    private Button.OnClickListener btnScanDevice_OnClick = new Button.OnClickListener(){
    	@Override
		public void onClick(View v) {
    		btnScanDevice.setEnabled(false);
    		btnScanDevice.setText("");
    		progressScanning.setVisibility(View.VISIBLE);
			btArrayAdapter.clear();
			devices.clear();
			bluetoothAdapter.startDiscovery();			
		}    	
    };    
    
    private BroadcastReceiver ActionReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(BluetoothDevice.ACTION_FOUND))
			{
				Log.i(tag, "ActionReceiver");
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				btArrayAdapter.add(device.getName() + "\n" + "Signal Strength : " + rssi  + "dBm" + "\n" + device.getAddress());
				devices.add(device.getAddress());
				btArrayAdapter.notifyDataSetChanged();
			}		
			else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
			{
				progressScanning.setVisibility(View.GONE);
				btnScanDevice.setText(R.string.scan_bluetooth);
				btnScanDevice.setEnabled(true);
			}
		}
	};
}
