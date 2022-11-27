package com.kangc;


import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class ServiceTool {

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        Log.e("OnlineService：", className);
        for (int i = 0; i < serviceList.size(); i++) {
            Log.e("serviceName：", serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().contains(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

}



