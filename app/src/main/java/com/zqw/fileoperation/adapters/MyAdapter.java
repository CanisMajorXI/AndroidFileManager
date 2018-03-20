package com.zqw.fileoperation.adapters;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.zqw.fileoperation.R;
import com.zqw.fileoperation.fragments.Folderfragment;
import com.zqw.fileoperation.pojos.MyFile;

import java.io.File;
import java.util.List;

/**
 * Created by 51376 on 2018/3/15.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Folderfragment folderfragment = null;
    private List<MyFile> myFiles;
    private Context context;
    private OnItemClickListener onItemClickListener;
    public FragmentManager manager = null;

    public MyAdapter(List<MyFile> myFiles, FragmentManager fragmentManager, Folderfragment folderfragment, Context context) {
        this.myFiles = myFiles;
        this.manager = fragmentManager;
        this.folderfragment = folderfragment;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                onItemClickListener.onItemClick(view, position);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
                onItemClickListener.onItemLongClick(view, position);
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyFile myFile = myFiles.get(position);
        holder.fileName.setText(myFile.getFileName());
        holder.fileDescribe.setText(myFile.getFileDescribe());
        if (myFile.getType() == 0)
            holder.thumbnail.setBackgroundResource(R.drawable.folder2);
        else if (myFile.getType() == 1) {
            holder.thumbnail.setBackgroundResource(R.drawable.file1);
        }
    }

    @Override
    public int getItemCount() {
        return myFiles.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        TextView fileDescribe;
        ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            fileName = (TextView) view.findViewById(R.id.file_name);
            fileDescribe = (TextView) view.findViewById(R.id.file_describe);
            thumbnail = (ImageView) view.findViewById(R.id.file_thumbnail);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
