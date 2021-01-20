package com.generalscan.sdkdemo.Utils;

import android.content.Context;

import com.generalscan.scannersdk.core.basic.interfaces.ILogger;

import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import kotlin.jvm.Synchronized;

public class DeviceLogger implements ILogger {

    private Context context;
    public DeviceLogger(Context context)
    {
        this.context = context;
    }
    @Override
    public void logDataTransaction(@NotNull String s) {
        writeLog(s);
    }

    @Override
    public void logError(@NotNull String title, @NotNull String message) {
        writeLogHeader(title);
        writeLog(message);
    }

    @Override
    public void logError(@NotNull String title, @NotNull Throwable throwable) {
        writeLogHeader(title);
        writeLog(throwable.getMessage());
    }

    @Override
    public void logInfo(@NotNull String title, @NotNull String message) {
        writeLogHeader(title);
        writeLog(message);
    }
    private void writeLogHeader(String title) {
        writeLog("===============$title=================");
        writeLog("Android Version:${Build.VERSION.RELEASE}");
    }
    @Synchronized
    private void writeLog(String log) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String curDate = dateFormat.format(date);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String curTime = dateFormat.format(date);
            FileWriter write = new FileWriter(context.getExternalFilesDir("").getAbsolutePath() + "/log.txt");
            write.append("[" + curTime + "]:" + log + "\r\n");
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
