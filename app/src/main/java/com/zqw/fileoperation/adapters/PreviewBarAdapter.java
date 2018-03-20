package com.zqw.fileoperation.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zqw.fileoperation.R;

import java.util.List;

/**
 * Created by 51376 on 2018/3/19.
 */

public class PreviewBarAdapter extends RecyclerView.Adapter<PreviewBarAdapter.ViewHolder> {

    public List<String> previewBarItems = null;
    private OnItemClickListener onItemClickListener = null;

    public PreviewBarAdapter(List<String> previewBarItems) {
        this.previewBarItems = previewBarItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preview_bar_item_layout, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String path = previewBarItems.get(position);
        if (position == 0) path = "内部储存";
        else {
            int index = path.lastIndexOf("/");
            if (index > 0 && path.length() - index > 1) {
                path = path.substring(index + 1, path.length());
                if (path.length() > 10) path = path.substring(0, 10) + "...";
            }
        }
        holder.path.setText(path);
    }

    @Override
    public int getItemCount() {
        return previewBarItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView path;

        public ViewHolder(View view) {
            super(view);
            path = (TextView) view.findViewById(R.id.preview_bar_item_path);
        }
    }
}
