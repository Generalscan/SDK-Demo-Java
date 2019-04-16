package com.generalscan.sdkdemo.Utils;

import android.content.Context;

public interface AsyncTaskCall {
    public Context getContext();

    public CallResult start();

}