package com.zqw.fileoperation.adapters;

import android.view.View;
import android.widget.CompoundButton;

import com.zqw.fileoperation.pojos.MyFile;

/**
 * Created by 51376 on 2018/4/19.
 */

public interface OnFileItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);

    void onCheckedChange(CompoundButton buttonView, boolean isChecked, MyFile myFile);
}
