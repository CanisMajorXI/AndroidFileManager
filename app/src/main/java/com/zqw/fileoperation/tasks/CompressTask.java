package com.zqw.fileoperation.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.zqw.fileoperation.MainActivity;
import com.zqw.fileoperation.MyApplication;
import com.zqw.fileoperation.R;
import com.zqw.fileoperation.fragments.FolderFragment;
import com.zqw.fileoperation.functions.MyCompress;

import java.util.List;

/**
 * Created by 51376 on 2018/4/20.
 */

public class CompressTask extends AsyncTask<List<String>, String, Boolean> {

    private ProgressDialog progressDialog = null;
    private MainActivity mainActivity = null;

    public CompressTask(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setTitle("压缩中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                MyCompress.setCancelled(true);
                Toast.makeText(mainActivity, "你取消了压缩", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.dismiss();
        if (result) {
            Toast.makeText(mainActivity, "压缩成功!", Toast.LENGTH_SHORT).show();
            mainActivity.reFresh(false);

        } else {
            Toast.makeText(mainActivity, "压缩失败!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onProgressUpdate(String... files) {
        String file = files[0];
        progressDialog.setMessage("压缩:\n" + file);
    }

    @Override
    protected Boolean doInBackground(List<String>[] lists) {
        // return true;
        return MyCompress.execCompress(lists[0], this);
    }

    public void publishCompressProgress(String file) {
        publishProgress(file);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Notification.Builder builder = new Notification.Builder(MyApplication.getContext()).
                setContentTitle(title).setSmallIcon(R.mipmap.ic_launcher).
                setLargeIcon(BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.ic_launcher));
        if (progress > 0) {
            builder.setContentText("压缩进度" + progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}
