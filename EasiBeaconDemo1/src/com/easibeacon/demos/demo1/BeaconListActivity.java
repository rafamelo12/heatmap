package com.easibeacon.demos.demo1;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

// This activity implements IBeaconListener to receive events about iBeacon discovery
public class BeaconListActivity extends Activity implements IBeaconListener{
	
	private static final int REQUEST_BLUETOOTH_ENABLE = 1;	

	private static ArrayList<IBeacon> _beacons;
	//private ArrayAdapter<IBeacon> _beaconsAdapter;
	private static IBeaconProtocol _ibp;
	
	private static TextView tv1;
	private static TextView tv2;
	private static TextView tv3;
	private static TextView tv4;
	
	private Coordinate b1Coord = new Coordinate(0, 0);
	private Coordinate b2Coord = new Coordinate((float)0.1, 0);
	private Coordinate b3Coord = new Coordinate(0, (float)0.1);
	private Coordinate user = new Coordinate();
	
	private float d1 = 0;
	private float d2 = 0;
	private float d3 = 0;
	
	private Menu _menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		
		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv4 = (TextView) findViewById(R.id.textView4);
		
		if(_beacons == null)
			_beacons = new ArrayList<IBeacon>();
		
		_ibp = IBeaconProtocol.getInstance(this);
		_ibp.setListener(this);
		
		
	}
	
	@Override
	protected void onStop() { 
		_ibp.stopScan();
		super.onStop();
	}
	
	private void scanBeacons(){
		// Check Bluetooth every time
		Log.i(Utils.LOG_TAG,"Scanning");

		if(!IBeaconProtocol.configureBluetoothAdapter(this)){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE );
		}else{
			if(_ibp.isScanning())
				_ibp.stopScan();
			_ibp.reset();
			_ibp.startScan();		
		}		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_BLUETOOTH_ENABLE){
			if(resultCode == Activity.RESULT_OK){
				scanBeacons();
			}
		}
	}	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beacon_list, menu);
		_menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			_beacons.clear();
			//_beaconsAdapter.notifyDataSetChanged();
			scanBeacons();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("InflateParams")
	private void startRefreshAnimation(){
		MenuItem item = _menu.findItem(R.id.action_refresh);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ImageView iv = (ImageView)inflater.inflate(R.layout.refresh_button, null);
		Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
		rotation.setRepeatCount(Animation.INFINITE);
		iv.startAnimation(rotation);
		item.setActionView(iv);		
	}
	
	private void stopRefreshAnimation(){
		MenuItem item = _menu.findItem(R.id.action_refresh);
        if(item.getActionView()!=null){
            // Remove the animation.
            item.getActionView().clearAnimation();
            item.setActionView(null);
        }
	}
	
	
	// The following methods implement the IBeaconListener interface
	
	@Override
	public void beaconFound(IBeacon ibeacon) {
		if(ibeacon.getMajor() == 7 && ibeacon.getMinor() == 44659){
			ibeacon.setPosition(b1Coord);
			_beacons.add(ibeacon);
		}
		else if(ibeacon.getMajor() == 125 && ibeacon.getMinor() == 2631){
			ibeacon.setPosition(b2Coord);
			_beacons.add(ibeacon);
		}
		else if(ibeacon.getMajor() == 7 && ibeacon.getMinor() == 45002){
			ibeacon.setPosition(b3Coord);
			_beacons.add(ibeacon);
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
			}
		});
	}
	
	@Override
	public void enterRegion(IBeacon ibeacon) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void exitRegion(IBeacon ibeacon) {
	}
	
	@Override
	public void operationError(int status) {
		Log.i(Utils.LOG_TAG, "Bluetooth error: " + status);		
	}
	
	@Override
	public void searchState(int state) {
		if(state == IBeaconProtocol.SEARCH_STARTED){
			startRefreshAnimation();
		}else if (state == IBeaconProtocol.SEARCH_END_EMPTY || state == IBeaconProtocol.SEARCH_END_SUCCESS){
			stopRefreshAnimation();
			updateResults();
			scanBeacons();
		}
	}
	
	public void updateResults(){
		
		for(int i = 0; i < _beacons.size(); i++){
			if(_beacons.get(i).getMajor() == 7 && _beacons.get(i).getMinor() == 44659){
				tv1.setText(String.valueOf(_beacons.get(i).getProximity()));
				d1 = _beacons.get(i).getProximity();
			}
			else if(_beacons.get(i).getMajor() == 125 && _beacons.get(i).getMinor() == 2631){
				tv2.setText(String.valueOf(_beacons.get(i).getProximity()));
				d2 = _beacons.get(i).getProximity();
			}
			else if(_beacons.get(i).getMajor() == 7 && _beacons.get(i).getMinor() == 45002){
				tv3.setText(String.valueOf(_beacons.get(i).getProximity()));
				d3 = _beacons.get(i).getProximity();
			}
		}
		
		user.trilaterate(d1, d2, d3, b1Coord, b2Coord, b3Coord);
		tv4.setText("(" + String.valueOf(user.getX()) + ", " + String.valueOf(user.getY()) + ", " + String.valueOf(user.getY()) + ")");
		
	}
	
}
