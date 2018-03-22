package com.zqw.fileoperation.fragments;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zqw.fileoperation.MainActivity;
import com.zqw.fileoperation.adapters.MyAdapter;
import com.zqw.fileoperation.R;
import com.zqw.fileoperation.adapters.OnItemClickListener;
import com.zqw.fileoperation.functions.FileFounder;
import com.zqw.fileoperation.pojos.MyFile;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 51376 on 2018/3/15.
 */
public class FolderFragment extends Fragment {

    private RecyclerView recyclerView = null;
    private List<MyFile> myFiles = new LinkedList<>();
    private FragmentManager manager = null;
    private View view;
    private String absolutePath = "/storage/emulated/0";
    private MyAdapter adapter = null;
    private   MainActivity mainActivity = null;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    //在onAttach内获取活动的实例
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folder_fragment, container, false);
        this.view = view;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        super.onResume();
        mainActivity.currentFragment = this;

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        myFiles = FileFounder.getFilesFromDir(absolutePath, getActivity());
        if (myFiles == null) {
            myFiles = new LinkedList<>();
            //读取不出来的情况
        }
        //装配适配器，在里面实现点击事件的回调
        recyclerViewAdpterAssemble();
        mainActivity.currentAbsolutePath = absolutePath;
    }

    //刷新列表（整体刷新）
    public void onRefresh() {
        myFiles = FileFounder.getFilesFromDir(absolutePath, getActivity());
        if (myFiles == null) {
            myFiles = new LinkedList<>();
            //读取不出来的情况
        }
        // myFiles.
        recyclerViewAdpterAssemble();
    }

    //实现点击，长按事件的回调
    private void recyclerViewAdpterAssemble() {
        adapter = new MyAdapter(myFiles, getActivity().getFragmentManager(), this, getActivity());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //     Log.d("mytest", String.valueOf(holder.getAdapterPosition()));
                MyFile myFile = myFiles.get(position);
                int a = 10;
                //所点击为目录的情况
                if (myFile.getType() == 0) {
                    //Toast.makeText(context, myFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    //读取不出来的情况
                    if (FileFounder.getFilesFromDir(myFile.getAbsolutePath(), getActivity()) == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("目录无法打开!");
                        builder.setMessage("该目录无法打开或者已经损坏");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        builder.show();
                        return;
                    }
                    manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    FolderFragment newfolderfragment = new FolderFragment();
                    //为新的文件夹碎片设置路径
                    newfolderfragment.setAbsolutePath(myFile.getAbsolutePath());
                    FolderFragment oldfolderfragment = (FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout);
                    transaction.remove(oldfolderfragment);
                    transaction.add(R.id.folder_fragment_layout, newfolderfragment);
                    //transaction.replace(R.id.folder_fragment_layout, newfolderfragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
            //长按文件事件
            @Override
            public void onItemLongClick(View view, int position) {
               // Log.d("test2","OK!");
                toggleBottomPopupMenu();
              //  ViewGroup.LayoutParams layoutParams = mainActivity.linearLayout.getLayoutParams();
//                layoutParams.height=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
//            // layoutParams.height = expectedHeight;
//                mainActivity.linearLayout.setLayoutParams(layoutParams);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void toggleBottomPopupMenu() {
        mainActivity.toggleBottomPopupMenu();
//        int begin = 0,end = 0;
//        if(mainActivity.linearLayout.getLayoutParams().height ==0)
//            end = 200;
//        else {
//            begin = 200;
//        }
//        final ViewGroup.LayoutParams layoutParams = mainActivity.linearLayout.getLayoutParams();
//        final ValueAnimator  animator = ValueAnimator.ofInt(begin,end);
//        animator.setDuration(600);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int expectedheight = (int)animation.getAnimatedValue();
//                layoutParams.height=((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, expectedheight, getResources().getDisplayMetrics()));
//                mainActivity.linearLayout.setLayoutParams(layoutParams);
//            }
//        });
//        animator.start();
    }
}

