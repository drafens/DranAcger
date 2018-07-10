package com.drafens.dranacger.Tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApplicationUpdate{
    private final static String downloadPath = Environment.getExternalStorageDirectory().getPath() + "/drafens/update/";
    private int nowVersionCode;
    private int newVersionCode;
    private String newVersionName;
    private String nowVersionName;
    private int getApplicationVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        int version = 0;
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    private int getLatestVersion(){
        int version = 0;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://raw.githubusercontent.com/drafens/DranAcger/master/app/release/output.json").build();
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            JSONObject jsonObject = new JSONArray(string).getJSONObject(0);
            JSONObject object = jsonObject.getJSONObject("apkInfo");
            version = Integer.parseInt(object.getString("versionCode"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    private void downloadApplication(Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://raw.githubusercontent.com/drafens/DranAcger/master/app/release/app-release.apk").build();
        client.newCall(request).enqueue(callback);
    }

    private void installApplication(Context context){
        Uri uri;
        File file = new File(downloadPath, "DranAcger.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, "com.drafens.dranacger.fileProvider", file);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public int getResult(Context context){
        int newVersion = getLatestVersion();
        int nowVersion = getApplicationVersion(context);
        Log.d("TAG", newVersion + " "+nowVersion);
        if (newVersion==nowVersion){
            //无更新
            return 0;
        }else if (newVersion>nowVersion){
            //更新
            return 1;
        }else {
            //版本号异常
            //Toast.makeText(context,"哇，你在使用比最新发布版本还新的软件",Toast.LENGTH_LONG).show();
            return -1;
        }
    }

    public void getUpdate(final Context context){
        downloadApplication(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //Toast.makeText(context,"更新失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                InputStream inputStream;
                byte[] buff = new byte[2048];
                int len;
                FileOutputStream fileOutputStream;
                try{
                    inputStream = response.body().byteStream();
                    //long total = response.body().contentLength();
                    //File file = new File(downloadPath,"DranAcger");
                    File files = new File(downloadPath);
                    if(!files.exists()) {
                        files.mkdirs();
                    }
                    File file = new File(downloadPath+"DranAcger.apk");
                    if (!file.exists()){
                        file.createNewFile();
                    }
                    fileOutputStream = new FileOutputStream(file);
                    //long sum = 0;
                    while ((len = inputStream.read(buff)) != -1) {
                        fileOutputStream.write(buff, 0, len);
                        //sum += len;
                        //int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                    }
                    fileOutputStream.flush();
                    inputStream.close();
                    fileOutputStream.close();
                    //success
                    installApplication(context);
                }catch (Exception e){
                    e.printStackTrace();
                    //error
                    //Toast.makeText(context,"更新失败",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
