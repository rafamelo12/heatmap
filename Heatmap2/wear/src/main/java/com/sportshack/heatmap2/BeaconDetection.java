package com.sportshack.heatmap2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class BeaconDetection {
    private static final String TAG = "MonitorActivity";
    private static final String NO_BLUETOOTH_ID = "No bluetooth";
    private static final String FOUND_DEVICE = "Found device";
    private static final String EXIT_REGION = "Exit region";
    private static final String CHANGE_STATE = "Changed state";

    private int noBluetoothID = 1;
    private int foundDevice = 2;
    private int exitRegion = 3;
    private int changeState = 4;

    private BeaconManager beaconManager;
    verifyBluetooth();

        private void verifyBluetooth() {
            try {
                if(!BeaconManager.getInstanceForApplication(this).checkAvailability()){
                    int notf01 = 001;

                    Intent intent = new Intent(this, RangingActivity.class);
                    intent.putExtra(NO_BLUETOOTH_ID, noBluetoothID);

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(this)
                                    .setContentTitle("Bluetooth not enabled.")
                                    .setContentText("Please enable bluetooth in the settings and restart application.")
                                    .setContentIntent(pendingIntent);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                    notificationManager.notify(notf01, builder.build());
                }

            } catch (RuntimeException e) {
                int notf02 = 002;

                Intent intent = new Intent(this, RangingActivity.class);
                intent.putExtra(NO_BLUETOOTH_ID, noBluetoothID);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle("Bluetooth LE not available.")
                                .setContentText("Sorry, this device does not support Bluetooth LE..")
                                .setContentIntent(pendingIntent);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                notificationManager.notify(notf02, builder.build());

            }
        }

    public void didEnterRegion(Region region) {
        int notf03 = 003;

        Intent intent = new Intent(this, RangingActivity.class);
        intent.putExtra(FOUND_DEVICE, foundDevice);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Found a beacon!")
                        .setContentText("I just saw a beacon named " + region.getUniqueId() + " for the first time!")
                        .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notf03, builder.build());
    }

    public void didExitRegion(Region region) {
        int notf04 = 004;

        Intent intent = new Intent(this, RangingActivity.class);
        intent.putExtra(EXIT_REGION, exitRegion);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Beacon is gone!")
                        .setContentText("I no longer see a beacon named " + region.getUniqueId())
                        .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notf04, builder.build());
    }

    public void didDetermineStateForRegion(int State, Region region) {
        int notf05 = 005;

        Intent intent = new Intent(this, RangingActivity.class);
        intent.putExtra(CHANGE_STATE, changeState);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("State changed.")
                        .setContentText("I just switched from seeing/not seeing " + region.getUniqueId())
                        .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notf05, builder.build());
    }

}
