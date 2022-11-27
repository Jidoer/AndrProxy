package com.car.runer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class ExecuteAsyncTask extends AsyncTask<Void, String, CommandResult> {

    private final String[] cmd;
    private final ExecuteResponseHandler appExecuteResponseHandler;
    private final ShellCommand shellCommand;
    private final long timeout;
    private long startTime;
    public Process process;
    private String output = "";

    public ExecuteAsyncTask(String[] cmd, long timeout, ExecuteResponseHandler appExecuteResponseHandler) {
        this.cmd = cmd;
        this.timeout = timeout;
        this.appExecuteResponseHandler = appExecuteResponseHandler;
        this.shellCommand = new ShellCommand();
    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
        if (appExecuteResponseHandler != null) {
            appExecuteResponseHandler.onStart();
        }
    }

    @Override
    protected CommandResult doInBackground(Void... params) {
        try {
            process = shellCommand.run(cmd);
            if (process == null) {
                return CommandResult.getDummyFailureResponse();
            }
            Log.d("Running publishing updates method");
            checkAndUpdateProcess();
            return CommandResult.getOutputFromProcess(process);
        } catch (TimeoutException e) {
            Log.e("app timed out", e);
            return new CommandResult(false, e.getMessage());
        } catch (Exception e) {
            Log.e("Error running Task", e);
        } finally {
            Util.destroyProcess(process);
        }
        return CommandResult.getDummyFailureResponse();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values != null && values[0] != null && appExecuteResponseHandler != null) {
            appExecuteResponseHandler.onProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(CommandResult commandResult) {
        if (appExecuteResponseHandler != null) {
            output += commandResult.output;
            if (commandResult.success) {
                appExecuteResponseHandler.onSuccess(output);
            } else {
                appExecuteResponseHandler.onFailure(output);
            }
            appExecuteResponseHandler.onFinish();
        }
    }

    private void checkAndUpdateProcess() throws TimeoutException, InterruptedException {
        while (!Util.isProcessCompleted(process)) {
            // checking if process is completed
            if (Util.isProcessCompleted(process)) {
                return;
            }

            // Handling timeout
            if (timeout != Long.MAX_VALUE && System.currentTimeMillis() > startTime + timeout) {
                throw new TimeoutException("timed out");
            }

            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    if (isCancelled()) {
                        return;
                    }

                    output += line+"\n";
                    publishProgress(line);
                    Log.d("Out:" + output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    boolean isProcessCompleted() {
        return Util.isProcessCompleted(process);
    }

}
