package com.zqw.fileoperation.tasks;

import android.os.AsyncTask;

import com.zqw.fileoperation.functions.MyCompress;

import java.util.List;

/**
 * Created by 51376 on 2018/4/20.
 */

public class CompressTask extends AsyncTask<List<String>, Integer, Boolean> {
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Boolean doInBackground(List<String>[] lists) {
       return MyCompress.execCompress(lists[0]);

    }
}
