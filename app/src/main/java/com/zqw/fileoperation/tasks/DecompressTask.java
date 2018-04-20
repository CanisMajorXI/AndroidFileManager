package com.zqw.fileoperation.tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.zqw.fileoperation.MainActivity;
import com.zqw.fileoperation.functions.MyCompress;

import java.util.List;

/**
 * Created by 51376 on 2018/4/20.
 */

public class DecompressTask extends AsyncTask<List<String>, String, Boolean> {
    private ProgressDialog progressDialog = null;
    private MainActivity mainActivity = null;

    public DecompressTask(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setTitle("解压中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                MyCompress.setCancelled(true);
                Toast.makeText(mainActivity, "你取消了解压", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mainActivity, "解压成功!", Toast.LENGTH_SHORT).show();
            mainActivity.reFresh(false);

        } else {
            Toast.makeText(mainActivity, "解压失败!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(String... files) {
        String file = files[0];
        progressDialog.setMessage("提取:\n" + file);
    }

    @Override
    protected Boolean doInBackground(List<String>[] lists) {
        return MyCompress.execDecompress(lists[0], this);
    }

    public void publishDecompressProgress(String file) {
        publishProgress(file);
    }

}
