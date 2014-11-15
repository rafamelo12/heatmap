package com.sportshack.heatmap2;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;


import java.util.Collection;
import java.util.EventListener;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

public class MainActivity extends Activity implements BootstrapNotifier, RangeNotifier {
    static final int REQUEST_BEACON_DATA = 1;
    private static final String TAG = "MainActivity";
    private BeaconManager beaconManager;
    private Region mAllBeaconsRegion;
    private MonitoringActivity mMonitoringActivity;
    private RangingActivity mRangingActivity;
    @SuppressWarnings("unused")
    private RegionBootstrap mRegionBoostrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAllBeaconsRegion = new Region("all beacons", null, null, null);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        mRegionBoostrap = new RegionBootstrap(this, mAllBeaconsRegion);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void didEnterRegion(Region region) {
        if(mMonitoringActivity != null) {
            mMonitoringActivity.didEnterRegion(region);
        }
        try {
            Log.d(TAG, "Entered region. Starting ranging.");
            beaconManager.startRangingBeaconsInRegion(mAllBeaconsRegion);
            beaconManager.setRangeNotifier(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void didExitRegion(Region region) {
        if(mMonitoringActivity != null) {
            mMonitoringActivity.didExitRegion(region);
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if(mRangingActivity != null) {
            mRangingActivity.didRangeBeaconsInRegion(beacons, region);
        }
    }

    public void setMonitoringActivity(MonitoringActivity activity) {
        mMonitoringActivity = activity;
    }

    public void setmRangingActivity(RangingActivity activity) {
        mRangingActivity = activity;
    }
}
