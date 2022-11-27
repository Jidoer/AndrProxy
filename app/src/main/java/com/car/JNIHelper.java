package com.car;

import android.app.VoiceInteractor;
import android.content.Context;
import android.widget.Toast;

import com.car.runer.ExecuteAsyncTask;
import com.car.runer.ExecuteResponseHandler;
import com.car.runer.ShellCommand;
import com.car.runer.CommandResult;
import com.car.runer.Util;

import java.io.File;

public class JNIHelper {

    public static ExecuteAsyncTask ExecuteAsyncTask_;

    public static void RunNaive(Context appContext, String config) {
        String soPath = appContext.getApplicationInfo().nativeLibraryDir + "/libnaive.so";
        File naive = new File(soPath);
        if(!naive.canExecute()){
            if(!naive.setExecutable(true)) {
                //applog.setText("ERROR: can't Execute"+soPath);
                LogHelper.e("RunNaive","!naive.setExecutable(true)");
                Toast.makeText(appContext,"Run NaiveProxy error: can't setExecutable !",Toast.LENGTH_SHORT).show();

                return;
            }
        }

        String[] CMD = {soPath,config};

        ExecuteAsyncTask_ = new ExecuteAsyncTask(CMD, Long.MAX_VALUE, new ExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                LogHelper.i("Naive_onSuccess:",message);

            }

            @Override
            public void onProgress(String message) {
                //Toast.makeText(appContext,"onProgress:"+message,Toast.LENGTH_SHORT).show();
                LogHelper.i("Naive_onProgress:",message);
            }

            @Override
            public void onFailure(String message) {
                LogHelper.i("Naive_onFailure:",message);

            }

            @Override
            public void onStart() {
                LogHelper.i("Naive","Start()");

            }

            @Override
            public void onFinish() {
                LogHelper.i("Naive","Finish()");

            }
        });
        ExecuteAsyncTask_.execute();

    }

    public static void stop() {
        //trojan.Trojan.stopClient();
        Util.killAsync(ExecuteAsyncTask_);
    }
    public static String GetNaiveVersion(Context appContext){
        String soPath = appContext.getApplicationInfo().nativeLibraryDir + "/libnaive.so";
        File naive = new File(soPath);
        if(!naive.canExecute()){
            if(!naive.setExecutable(true)) {
                //applog.setText("ERROR: can't Execute"+soPath);
                LogHelper.e("RunNaive","!naive.setExecutable(true)");
                Toast.makeText(appContext,"Run NaiveProxy error: can't setExecutable !",Toast.LENGTH_SHORT).show();

                return "null";
            }
        }
        ShellCommand command = new ShellCommand();
        String[] CMD = {soPath,"-version"};
        CommandResult res = command.runWaitFor(CMD);
        return res.output;
    }
}
