package com.zqw.fileoperation;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zqw.fileoperation.adapters.OnItemClickListener;
import com.zqw.fileoperation.adapters.PreviewBarAdapter;
import com.zqw.fileoperation.fragments.Folderfragment;

import com.zqw.fileoperation.pojos.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        FragmentManager.OnBackStackChangedListener,
        OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{
    private Button button = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private int lastBackStackCount = 0;

    final public FragmentManager manager = getFragmentManager();
    public String currentAbsolutePath = "/storage/emulated/0";
    public List<String> previewBarItems = null;
    public PreviewBarAdapter adapter;
    public RecyclerView previewBar = null;
    public LinearLayout linearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //通过id加载控件
        previewBar = (RecyclerView) findViewById(R.id.preview_bar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);
        linearLayout = (LinearLayout)findViewById(R.id.bottom_popup_menu_layout);
        //通过id加载控件

        //是否插入内存卡
        //  Log.d("test", String.valueOf(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)));
        getPermission();
        Folderfragment folderfragment = new Folderfragment();
        FragmentTransaction transaction = manager.beginTransaction();
        manager.addOnBackStackChangedListener(this);
        transaction.add(R.id.folder_fragment_layout, folderfragment);
        transaction.commit();
        swipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        previewBar.setLayoutManager(linearLayoutManager);
        previewBarItems = new ArrayList<String>() {{
            add("/storage/emulated/0");
        }};
        adapter = new PreviewBarAdapter(previewBarItems);
        adapter.setOnItemClickListener(this);
        previewBar.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        reFresh();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackStackChanged() {
        int backStackEntryCount = manager.getBackStackEntryCount();
        if (backStackEntryCount < lastBackStackCount) {
            previewBarItems.remove(previewBarItems.size() - 1);
            adapter.notifyItemRemoved(previewBarItems.size());
        } else {
            previewBarItems.add(currentAbsolutePath);
            adapter.notifyItemInserted(previewBarItems.size() - 1);
        }
        lastBackStackCount = backStackEntryCount;
    }

    @Override
    public void onItemClick(View view, int position) {
        int times = previewBarItems.size() - 1 - position;
        for (int i = 0; i < times; i++)
            manager.popBackStack();
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private void reFresh() {
        Folderfragment folderFragment = (Folderfragment) manager.findFragmentById(R.id.folder_fragment_layout);
        folderFragment.onRefresh();
        Toast.makeText(this, "刷新成功!", Toast.LENGTH_SHORT).show();
    }

    //权限检查
    private void getPermission() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    124);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
