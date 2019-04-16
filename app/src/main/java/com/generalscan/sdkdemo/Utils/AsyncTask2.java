package com.generalscan.sdkdemo.Utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by alexli on 11/20/13.
 */
public class AsyncTask2 extends
        android.os.AsyncTask<String, Integer, CallResult> {
    private ProgressDialog progressDialog;
    private AsyncTaskCall call;
    private AsyncTaskCallback callBack;
    private String processingMessage = "Loading";
    private Context context;

    public AsyncTask2(AsyncTaskCall call, AsyncTaskCallback cb, int procesMessageResId) {
        super();
        initTask(call, cb, cb.getContext().getText(procesMessageResId).toString());
    }

    public AsyncTask2(AsyncTaskCall call, AsyncTaskCallback cb, String procesMessage) {
        super();
        initTask(call, cb, procesMessage);
    }

    private void initTask(AsyncTaskCall call, AsyncTaskCallback cb, String procesMessage) {
        this.callBack = cb;
        this.call = call;
        this.context = cb.getContext();
        this.processingMessage = procesMessage;
        if(this.processingMessage!=null)
        {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(this.processingMessage);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
    }

    @Override
    protected CallResult doInBackground(String... params) {
        CallResult callResult =  new CallResult();
        try {
            callResult =  call.start();
        } catch (Exception e) {
            callResult.setSuccess(false);
            callResult.setException(e);
            callResult.setErrorMessage(e.getMessage());
            //Utils.sysLog(e);
        }
        return callResult;
        //return Boolean.TRUE; // Return your real result here
    }

    @Override
    protected void onPreExecute() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(CallResult result) {
        try {
            callBack.hasFinished(result);
        } catch (Exception e) {
            e.printStackTrace();
            dismissDialog();
            throw e;
        }
        dismissDialog();
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}