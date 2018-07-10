package com.drafens.dranacger.Tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

public class ApplicationUpdate {
    private final static String downloadPate = "";
    public String getApplicationVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String version = "";
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName+"#"+packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public void getLatestVerson(){

    }

    private void downloadApplication(){

    }

    private void installApplication(Context context){
        File file = new File(downloadPate);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
