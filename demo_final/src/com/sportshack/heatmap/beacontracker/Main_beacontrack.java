package com.sportshack.heatmap.beacontracker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;

public class Main_beacontrack extends Activity implements IBeaconListener{

    private static final int REQUEST_BLUETOOTH_ENABLE = 1;

    private static ArrayList<IBeacon> _beacons;
    //private ArrayAdapter<IBeacon> _beaconsAdapter;
    private static IBeaconProtocol _ibp;

    private static TextView td1;
    private static TextView td2;
    private static TextView td3;
    
    private static TextView trssi1;
    private static TextView trssi2;
    private static TextView trssi3;
    
    private static TextView txp1;
    private static TextView txp2;
    private static TextView txp3;
    
    private static TextView position;

    private Coordinate b1Coord = new Coordinate(0, 0);
    private Coordinate b2Coord = new Coordinate(4, 0);
    private Coordinate b3Coord = new Coordinate(0, 3);
    private Coordinate user = new Coordinate();

    private float d1 = 0;
    private float d2 = 0;
    private float d3 = 0;

    private Menu _menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main_beacontrack);

        td1 = (TextView) findViewById(R.id.td1);
        td2 = (TextView) findViewById(R.id.td2);
        td3 = (TextView) findViewById(R.id.td3);
        
        
        trssi1 = (TextView) findViewById(R.id.trssi1);
        trssi2 = (TextView) findViewById(R.id.trssi2);
        trssi3 = (TextView) findViewById(R.id.trssi3);
        
        txp1 = (TextView) findViewById(R.id.txp1);
        txp2 = (TextView) findViewById(R.id.txp2);
        txp3 = (TextView) findViewById(R.id.txp3);

        position = (TextView) findViewById(R.id.position);
        if(_beacons == null)
            _beacons = new ArrayList<IBeacon>();

        _ibp = IBeaconProtocol.getInstance(this);
        _ibp.setListener(this);

        _beacons.clear();
        scanBeacons();
    }

    @Override
    protected void onStop() {
        _ibp.stopScan();
        super.onStop();
    }

    private void scanBeacons(){
        // Check Bluetooth every time
        Log.i(Utils.LOG_TAG, "Scanning");

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

    // The following methods implement the IBeaconListener interface

    @Override
    public void beaconFound(IBeacon ibeacon) {
        if(ibeacon.getMajor() == 125 && ibeacon.getMinor() == 15881){
            ibeacon.setPosition(b1Coord);
            _beacons.add(ibeacon);

        }
        else if(ibeacon.getMajor() == 125 && ibeacon.getMinor() == 19059){
            ibeacon.setPosition(b2Coord);
            _beacons.add(ibeacon);
        }
        else if(ibeacon.getMajor() == 125 && ibeacon.getMinor() == 17304){
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
        }else if (state == IBeaconProtocol.SEARCH_END_EMPTY || state == IBeaconProtocol.SEARCH_END_SUCCESS){
            updateResults();
            scanBeacons();

        }
    }

    public void updateResults(){
        td1 = (TextView) findViewById(R.id.td1);
        td2 = (TextView) findViewById(R.id.td2);
        td3 = (TextView) findViewById(R.id.td3);
        
        
        trssi1 = (TextView) findViewById(R.id.trssi1);
        trssi2 = (TextView) findViewById(R.id.trssi2);
        trssi3 = (TextView) findViewById(R.id.trssi3);
        
        txp1 = (TextView) findViewById(R.id.txp1);
        txp2 = (TextView) findViewById(R.id.txp2);
        txp3 = (TextView) findViewById(R.id.txp3);

        position = (TextView) findViewById(R.id.position);
        for(int i = 0; i < _beacons.size(); i++){
            if(_beacons.get(i).getMajor() == 125 && _beacons.get(i).getMinor() == 15881){
                td1.setText("Distance: " + String.valueOf(_beacons.get(i).getProximity()));
                txp1.setText("TxPower: " + String.valueOf(_beacons.get(i).getPowerValue()));
                trssi1.setText("RSSI: " + String.valueOf(_beacons.get(i).get_rssi()));

                d1 = _beacons.get(i).getProximity();
            }
            else if(_beacons.get(i).getMajor() == 125 && _beacons.get(i).getMinor() == 19059){
                td2.setText("Distance: " + String.valueOf(_beacons.get(i).getProximity()));
                txp2.setText("TxPower: " + String.valueOf(_beacons.get(i).getPowerValue()));
                trssi2.setText("RSSI: " + String.valueOf(_beacons.get(i).get_rssi()));

                d2 = _beacons.get(i).getProximity();
            }
            else if(_beacons.get(i).getMajor() == 125 && _beacons.get(i).getMinor() == 17304){
                td3.setText("Distance: " + String.valueOf(_beacons.get(i).getProximity()));
                txp3.setText("TxPower: " + String.valueOf(_beacons.get(i).getPowerValue()));
                trssi3.setText("RSSI: " + String.valueOf(_beacons.get(i).get_rssi()));

                d3 = _beacons.get(i).getProximity();
            }
        }
        user.trilaterate(d1, d2, d3, b1Coord, b2Coord, b3Coord);
        position.setText("(" + String.valueOf(user.getX()) + ", " + String.valueOf(user.getY()) + ", " + String.valueOf(user.getZ()) + ")");
        
        RequestTask rt = new RequestTask();
		rt.setxCoord(String.valueOf(user.getX()/4));
		rt.setyCoord(String.valueOf(user.getY()/3));
		rt.execute();	
    }
}
