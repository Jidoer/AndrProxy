package com.car.andrproxy;




import android.app.Service;
import android.content.Intent;
import android.net.VpnService;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import androidx.annotation.Nullable;

import com.car.Globals;
import com.car.NaiveHelper;
import com.car.ProxyHelper;
import com.car.andrproxy.ui.MainActivity;
import com.car.service.ProxyService;


public class AppService extends Service {
    private static final String TAG = AppService.class.getName();

    private static final int READ_WRITE_EXT_STORAGE_PERMISSION_REQUEST = 514;
    private static final int VPN_REQUEST_CODE = 233;
    private static final int SERVER_LIST_CHOOSE_REQUEST_CODE = 1024;
    private static final int EXEMPT_APP_CONFIGURE_REQUEST_CODE = 2077;
    private static final String CONNECTION_TEST_URL = "https://www.google.com";
    private @ProxyService.ProxyState
    int proxyState = ProxyService.STATE_NONE;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service create with pid " + android.os.Process.myPid());
        Globals.Init(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String stringExtra = intent.getStringExtra(getResources().getString(R.string.intent_key_file));
        if (TextUtils.isEmpty(stringExtra)) {
            android.os.Process.killProcess(android.os.Process.myPid());
            return START_STICKY;
        }
        startProxy(stringExtra);
        return START_STICKY;
    }


    private void startProxy(String path) {


        /*
        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "service running with file " + new File(path).getName());
                try {
                    ////Frpclib.run(path);

                    if (proxyState == ProxyService.STATE_NONE || proxyState == ProxyService.STOPPED) {
                        NaiveHelper.WriteTrojanConfig(
                                Globals.getNaiveConfigInstance(),
                                /*Globals.getNaiveConfigPath()* /
                                ""
                        );
                        NaiveHelper.ShowConfig(Globals.getNaiveConfigPath());
                        // start ProxyService
                        Intent i = VpnService.prepare(getApplicationContext());
                        if (i != null) {
                            //startActivityForResult(i, VPN_REQUEST_CODE);
                            Toast.makeText(getApplicationContext(),"Error: i!=null!",Toast.LENGTH_SHORT).show();
                        } else {
                            ProxyHelper.startProxyService(getApplicationContext());
                        }
                    } else if (proxyState == ProxyService.STARTED) {
                        // stop ProxyService
                        ProxyHelper.stopProxyService(getApplicationContext());

                }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "service running error " + e.toString());
                }
            }

        }.start();

        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "service destroy kill pid " + android.os.Process.myPid());
        android.os.Process.killProcess(android.os.Process.myPid());
    }













}
