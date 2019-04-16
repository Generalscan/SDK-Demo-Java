package com.generalscan.sdkdemo.Utils;

import android.content.Context;

public interface AsyncTaskCallback {
    public Context getContext();
    public void hasFinished(CallResult result);
}