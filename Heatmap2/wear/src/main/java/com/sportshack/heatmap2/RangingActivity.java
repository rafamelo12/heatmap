package com.sportshack.heatmap2;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class RangingActivity extends Activity {
    protected static final String TAG = "RangingActivity";
    protected static final String DISTANCE = "Distance";

    private int distance = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MainActivity)this.getParent()).setmRangingActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((MainActivity)this.getParent()).setmRangingActivity(null);
    }

    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if(beacons.size() > 0) {
            int notficationId = 001;
            for (Beacon beacon:
                 beacons) {
                Intent intent = new Intent(this, RangingActivity.class);
                intent.putExtra(DISTANCE, distance);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle("Distance to beacon")
                                .setContentText("Beacon " + beacon.toString() + " is about " + beacon.getDistance() + " meters away, with Rssi: " + beacon.getRssi())
                                .setContentIntent(pendingIntent);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                notificationManager.notify(notficationId, builder.build());
                notficationId++;
            }
        }
    }
}
