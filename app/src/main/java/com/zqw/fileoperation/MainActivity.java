package com.zqw.fileoperation;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zqw.fileoperation.adapters.MyAdapter;
import com.zqw.fileoperation.adapters.OnItemClickListener;
import com.zqw.fileoperation.adapters.PreviewBarAdapter;
import com.zqw.fileoperation.fragments.FolderFragment;
import com.zqw.fileoperation.pojos.MyFile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        FragmentManager.OnBackStackChangedListener,
        OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private Button button = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private int lastBackStackCount = 0;
    private boolean hasPermission = false;
    private Button bottomMenuCompress = null;
    private Button bottomMenuDecompress = null;
    private boolean readyCompress = false;

    final public FragmentManager manager = getFragmentManager();
    public String currentAbsolutePath = "/storage/emulated/0";
    public List<String> previewBarItems = null;
    public PreviewBarAdapter adapter = null;
    public RecyclerView previewBar = null;
    public LinearLayout bottomPopupMenuLayout = null;
    public View fileItemView = null;
    //复选框选中的文件
    public List<MyFile> selectedUncompressedFiles = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //通过id加载控件
        previewBar = (RecyclerView) findViewById(R.id.preview_bar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);
        bottomPopupMenuLayout = (LinearLayout) findViewById(R.id.bottom_popup_menu_layout);
        bottomMenuCompress = (Button) findViewById(R.id.bottom_popup_menu_compress);
        bottomMenuDecompress = (Button) findViewById(R.id.bottom_popup_menu_decompress);
        bottomMenuCompress.setOnClickListener(this);
        bottomMenuDecompress.setOnClickListener(this);
        // linearLayout = (LinearLayout)findViewById(R.id.bottom_popup_menu_layout);
        //通过id加载控件
        //是否插入内存卡
        //  Log.d("test", String.valueOf(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)));
        //getPermission();
        //Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
        FolderFragment folderFragment = new FolderFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        manager.addOnBackStackChangedListener(this);
        transaction.add(R.id.folder_fragment_layout, folderFragment);
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

    private void reFresh() {
        toggleBottomPopupMenu();
        FolderFragment folderFragment = (FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout);
        folderFragment.onRefresh();
        Toast.makeText(this, "刷新成功!", Toast.LENGTH_SHORT).show();
    }

    public void onItemLongClick(int position) {
        toggleBottomPopupMenu();
    }

    private void toggleBottomPopupMenu() {
        int begin = 0, end = 0;
        FolderFragment folderFragment = ((FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout));
        if (bottomPopupMenuLayout.getHeight() == 0) {
            end = 120;
            folderFragment.adapter.setChecked(true);
            folderFragment.adapter.notifyDataSetChanged();

        } else {
            begin = 120;
            folderFragment.adapter.setChecked(false);
            folderFragment.adapter.notifyDataSetChanged();
        }
        final ViewGroup.LayoutParams layoutParams = bottomPopupMenuLayout.getLayoutParams();
        final ValueAnimator animator = ValueAnimator.ofInt(begin, end);
        animator.setDuration(600);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int expectedheight = (int) animation.getAnimatedValue();
                layoutParams.height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, expectedheight, getResources().getDisplayMetrics()));
                bottomPopupMenuLayout.setLayoutParams(layoutParams);
            }
        });
        animator.start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int g : grantResults) {
            if (g != PackageManager.PERMISSION_GRANTED) return;
        }
        hasPermission = true;
    }

    public void requestPermission() {

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
        switch (view.getId()) {
            case R.id.bottom_popup_menu_compress:

                FolderFragment folderFragment = ((FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout));
                selectedUncompressedFiles = new LinkedList<>(folderFragment.getSelectedFiles());
                String str = "选中文件列表";
                for (MyFile myFile : folderFragment.getSelectedFiles()) {
                    str += (myFile.getAbsolutePath() + "\n");
                }
                Log.d("test9", str);
                break;

            case R.id.bottom_popup_menu_decompress:
                break;
        }
    }

    //如果底部菜单出来了，先返回底部菜单
    @Override
    public void onBackPressed() {
        if (bottomPopupMenuLayout.getHeight() != 0) {
            toggleBottomPopupMenu();
            return;
        }
        super.onBackPressed();

    }
}
