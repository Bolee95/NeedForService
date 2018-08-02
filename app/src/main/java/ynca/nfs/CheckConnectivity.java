package ynca.nfs;

/**
 * Created by bolee on 28.5.17..
 */

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;


public class CheckConnectivity extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent arg1) {

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final ConnectivityManager connMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                final android.net.NetworkInfo wifi = connMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                final android.net.NetworkInfo mobile = connMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (!wifi.isConnected() && !mobile.isConnected()) {
                    ProgressDialog dialog = ProgressDialog.show(context, "Warning",
                            "No internet connection...", true);


                    while (!wifi.isConnected() && !mobile.isConnected()) {
                        dialog.dismiss();
                    }
                }
                return null;
            }

        };
    }
}
