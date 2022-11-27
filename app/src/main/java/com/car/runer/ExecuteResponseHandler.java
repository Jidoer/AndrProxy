package com.car.runer;

public interface ExecuteResponseHandler extends ResponseHandler {

    /**
     * on Success
     * @param message complete output of the FFmpeg command
     */
    public void onSuccess(String message);

    /**
     * on Progress
     * @param message current output of FFmpeg command
     */
    public void onProgress(String message);

    /**
     * on Failure
     * @param message complete output of the FFmpeg command
     */
    public void onFailure(String message);

}
