package ynca.nfs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by bolee on 28.5.17..
 */

public class ConectionService extends Service {

    ConnectivityManager conMan;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final  Context  con = this;
    Thread thread = new Thread(){
        @Override
        public void run()
        {
            try
            {
                conMan = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                while (conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
                {}

                Toast.makeText(con.getApplicationContext(),"No Internet connection",Toast.LENGTH_LONG).show();

                stopSelf();
                //TODO TEST
                try {
                    super.finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            catch (Exception e){}
        }
    };
     thread.start();
        return Service.START_NOT_STICKY;
    }
}
