package com.car;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;

import androidx.core.content.ContextCompat;

import com.car.andrproxy.BuildConfig;
import com.car.andrproxy.R;
import com.car.andrproxy.ui.MainActivity;
import com.car.service.ProxyService;


/**
 * Helper class for starting or stopping {@link ProxyService}. Before starting {@link ProxyService},
 * make sure the TrojanConfig is valid (with the help of {@link #isTrojanConfigValid()} and whether
 * user has consented VPN Service (with the help of {@link #isVPNServiceConsented(Context)}.
 * <br/>
 * It's recommended to start launcher activity when the config is invalid or user hasn't consented
 * VPN service.
 */
public abstract class ProxyHelper {
    public static boolean isTrojanConfigValid() {
        NaiveConfig cacheConfig = NaiveHelper.readNaiveConfig(Globals.getNaiveConfigPath());
        if (cacheConfig == null) {
            return false;
        }
        if (BuildConfig.DEBUG) {
            NaiveHelper.ShowConfig(Globals.getNaiveConfigPath());
        }
        return cacheConfig.isValidRunningConfig();
    }

    public static boolean isVPNServiceConsented(Context context) {
        return VpnService.prepare(context.getApplicationContext()) == null;
    }

    public static void startProxyService(Context context) {
        ContextCompat.startForegroundService(context, new Intent(context, ProxyService.class));
    }

    public static void startLauncherActivity(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void stopProxyService(Context context) {
        Intent intent = new Intent(context.getString(R.string.stop_service));
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
