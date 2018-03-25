package com.zqw.fileoperation.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.zqw.fileoperation.R;
import com.zqw.fileoperation.fragments.FolderFragment;
import com.zqw.fileoperation.pojos.MyFile;

import java.util.List;

/**
 * Created by 51376 on 2018/3/15.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private FolderFragment folderFragment = null;
    private List<MyFile> myFiles;
    private Context context;
    private OnItemClickListener onItemClickListener;
    public FragmentManager manager = null;

    public MyAdapter(List<MyFile> myFiles, FragmentManager fragmentManager, FolderFragment folderFragment, Context context) {
        this.myFiles = myFiles;
        this.manager = fragmentManager;
        this.folderFragment = folderFragment;
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
         CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            fileName = (TextView) view.findViewById(R.id.file_name);
            fileDescribe = (TextView) view.findViewById(R.id.file_describe);
            thumbnail = (ImageView) view.findViewById(R.id.file_thumbnail);
            checkBox = (CheckBox) view.findViewById(R.id.file_item_checkBox);
            checkBox.setVisibility(View.GONE);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
