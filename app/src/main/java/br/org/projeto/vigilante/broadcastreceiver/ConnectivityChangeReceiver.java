package br.org.projeto.vigilante.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import br.org.projeto.vigilante.business.singletons.SyncHelper_;


public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();

    public ConnectivityChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Connectivity change entered");

        // If has internet connection...
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            SyncHelper_.getInstance_(context).save();
        }
    }
}
