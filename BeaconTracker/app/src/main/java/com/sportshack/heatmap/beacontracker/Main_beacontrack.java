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

    private static TextView tv1;
    private static TextView tv2;
    private static TextView tv3;
    private static TextView tv4;

    private Coordinate b1Coord = new Coordinate(0, 0);
    private Coordinate b2Coord = new Coordinate(1, 0);
    private Coordinate b3Coord = new Coordinate(0, 1);
    private Coordinate user = new Coordinate();

    private float d1 = 0;
    private float d2 = 0;
    private float d3 = 0;

    private Menu _menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_beacontrack);

        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView2);
        tv4 = (TextView) findViewById(R.id.textView2);

        if(_beacons == null)
            _beacons = new ArrayList<IBeacon>();

        _ibp = IBeaconProtocol.getInstance(this);
        _ibp.setListener(this);

        _beacons.clear();
        //_beaconsAdapter.notifyDataSetChanged();
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
        }else if (state == IBeaconProtocol.SEARCH_END_EMPTY || state == IBeaconProtocol.SEARCH_END_SUCCESS){
            updateResults();
            scanBeacons();
        }
    }

    public void updateResults(){
        tv1 = (TextView) findViewById(R.id.textView1);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);

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
