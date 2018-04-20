package com.zqw.fileoperation;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zqw.fileoperation.adapters.OnItemClickListener;
import com.zqw.fileoperation.adapters.PreviewBarAdapter;
import com.zqw.fileoperation.fragments.FolderFragment;
import com.zqw.fileoperation.functions.MyCompress;
import com.zqw.fileoperation.pojos.MyFile;
import com.zqw.fileoperation.tasks.CompressTask;
import com.zqw.fileoperation.tasks.DecompressTask;

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
    private Button bottomMenuCompress = null;
    private Button bottomMenuDecompress = null;
    private int COMPRESS_STATUS_UNDO = 0;
    private int COMPRESS_STATUS_SELECTFILES = 1;
    private int COMPRESS_STATUS_SELECTZIPFILE = 2;


    private int compressStatus = COMPRESS_STATUS_UNDO;

    final public FragmentManager manager = getFragmentManager();
    public String currentAbsolutePath = "/storage/emulated/0";
    public List<String> previewBarItems = null;
    public PreviewBarAdapter adapter = null;
    public RecyclerView previewBar = null;
    public LinearLayout bottomPopupMenuLayout = null;
    public View fileItemView = null;
    //复选框选中的文件
    public List<MyFile> selectedUncompressedFiles = new LinkedList<>();
    public List<MyFile> selectedUndecompressedFiles = new LinkedList<>();

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
        reFresh(true);
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

    public void reFresh(boolean isHint) {
        holdBottomPopMenu();
        FolderFragment folderFragment = (FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout);
        folderFragment.onRefresh();
        if (isHint)
            Toast.makeText(this, "刷新成功!", Toast.LENGTH_SHORT).show();
    }

    public void onItemLongClick(int position) {
        toggleBottomPopupMenu();
    }

    private void holdBottomPopMenu() {
        if (bottomPopupMenuLayout.getHeight() == 0) return;
        compressStatus = COMPRESS_STATUS_UNDO;
        changeTextByStatus(compressStatus);
        FolderFragment folderFragment = ((FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout));
        folderFragment.adapter.setChecked(false);
        folderFragment.adapter.notifyDataSetChanged();
        final ViewGroup.LayoutParams layoutParams = bottomPopupMenuLayout.getLayoutParams();
        final ValueAnimator animator = ValueAnimator.ofInt(120, 0);
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
        selectedUndecompressedFiles.clear();
        selectedUncompressedFiles.clear();
    }

    private void unholdBottomPopupMenu() {
        if (bottomPopupMenuLayout.getHeight() == 120) return;
        compressStatus = COMPRESS_STATUS_UNDO;
        changeTextByStatus(compressStatus);
        FolderFragment folderFragment = ((FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout));
        folderFragment.adapter.setChecked(true);
        folderFragment.adapter.notifyDataSetChanged();
        final ViewGroup.LayoutParams layoutParams = bottomPopupMenuLayout.getLayoutParams();
        final ValueAnimator animator = ValueAnimator.ofInt(0, 120);
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

    private void toggleBottomPopupMenu() {
        if (bottomPopupMenuLayout.getHeight() == 0) unholdBottomPopupMenu();
        else holdBottomPopMenu();
//        compressStatus = COMPRESS_STATUS_UNDO;
//        changeTextByStatus(compressStatus);
//        int begin = 0, end = 0;
//        FolderFragment folderFragment = ((FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout));
//        if (bottomPopupMenuLayout.getHeight() == 0) {
//            end = 120;
//            folderFragment.adapter.setChecked(true);
//            folderFragment.adapter.notifyDataSetChanged();
//
//        } else {
//            begin = 120;
//            folderFragment.adapter.setChecked(false);
//            folderFragment.adapter.notifyDataSetChanged();
//        }
//        final ViewGroup.LayoutParams layoutParams = bottomPopupMenuLayout.getLayoutParams();
//        final ValueAnimator animator = ValueAnimator.ofInt(begin, end);
//        animator.setDuration(600);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int expectedheight = (int) animation.getAnimatedValue();
//                layoutParams.height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, expectedheight, getResources().getDisplayMetrics()));
//                bottomPopupMenuLayout.setLayoutParams(layoutParams);
//            }
//        });
//        animator.start();
    }

    private void changeTextByStatus(int status) {
        if (status == COMPRESS_STATUS_UNDO) {
            bottomMenuCompress.setText("压缩选中文件");
            bottomMenuDecompress.setText("解压选中文件");
        } else if (status == COMPRESS_STATUS_SELECTFILES) {
            bottomMenuCompress.setText("压缩到此处");
            bottomMenuDecompress.setText("解压选中文件");
        } else if (status == COMPRESS_STATUS_SELECTZIPFILE) {
            bottomMenuDecompress.setText("解压到此处");
            bottomMenuCompress.setText("压缩选中文件");
        }
    }

    @Override
    public void onClick(final View view) {
        final FolderFragment folderFragment = ((FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout));
        switch (view.getId()) {
            case R.id.bottom_popup_menu_compress:
                if (compressStatus == COMPRESS_STATUS_UNDO) {
                    selectedUncompressedFiles = new LinkedList<>(folderFragment.getSelectedFiles());
                    String str = "选中文件列表";
                    for (MyFile myFile : folderFragment.getSelectedFiles()) {
                        str += (myFile.getAbsolutePath() + "\n");
                    }
                    Log.d("test9", str);
                    compressStatus = COMPRESS_STATUS_SELECTFILES;
                    changeTextByStatus(compressStatus);
                    break;
                }
                if ((compressStatus == COMPRESS_STATUS_SELECTFILES)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("请输入压缩文件名");
                    final EditText edit = new EditText(this);
                    builder.setView(edit);
                    edit.setText(".zip");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<String> fileNameList = new ArrayList<>();
                            for (MyFile myFile : selectedUncompressedFiles) {
                                fileNameList.add(myFile.getAbsolutePath());
                            }
                            String name = edit.getText().toString();
                            if (name.equals(".zip")) name = "default.zip";
                            if (name.length() <= 4 || name.lastIndexOf(".zip") != name.length() - 4) {
                                name = name + ".zip";
                            }
                            File file = new File(name);
                            if (file.exists()) {
                                Toast.makeText(MainActivity.this, "该文件已存在!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(MainActivity.this, currentAbsolutePath + "/" + name, Toast.LENGTH_SHORT).show();

                            fileNameList.add(currentAbsolutePath + "/" + name);
                            FolderFragment folderFragment = (FolderFragment) manager.findFragmentById(R.id.folder_fragment_layout);
                            new CompressTask(MainActivity.this).execute(fileNameList);
                            toggleBottomPopupMenu();
                            compressStatus = COMPRESS_STATUS_UNDO;
                            changeTextByStatus(compressStatus);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.create();
                    dialog.show();
                    break;
                }

                break;

            case R.id.bottom_popup_menu_decompress:
                if (compressStatus == COMPRESS_STATUS_UNDO) {
                    String str = "解压选中文件列表";
                    for (MyFile myFile : folderFragment.getSelectedFiles()) {
                        str += (myFile.getAbsolutePath() + "\n");
                    }
                    Log.d("test9", str);
                    if (folderFragment.getSelectedFiles().size() != 1 || folderFragment.getSelectedFiles().get(0).getType() != MyFile.TYPE_FILE || !folderFragment.getSelectedFiles().get(0).getAbsolutePath().matches("((\\S+(\\S|\\s)*\\S+)|\\S+)\\.zip")) {
                        Toast.makeText(this, "请选择一个zip类型的压缩文件进行压缩！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    selectedUndecompressedFiles = new ArrayList<>(folderFragment.getSelectedFiles());
                    MyFile myFile = folderFragment.getSelectedFiles().get(0);
                    compressStatus = COMPRESS_STATUS_SELECTZIPFILE;
                    changeTextByStatus(compressStatus);
                    break;
                }
                if (compressStatus == COMPRESS_STATUS_SELECTZIPFILE) {
                    List<String> nameList = new ArrayList<String>();
                    nameList.add(selectedUndecompressedFiles.get(0).getAbsolutePath());
                    nameList.add(currentAbsolutePath + "/");
                    new DecompressTask(MainActivity.this).execute(nameList);
                    toggleBottomPopupMenu();
                    compressStatus = COMPRESS_STATUS_UNDO;
                    changeTextByStatus(compressStatus);

                }

                break;
        }
    }

    //如果底部菜单出来了，先返回底部菜单
    @Override
    public void onBackPressed() {
        if (bottomPopupMenuLayout.getHeight() != 0) {
            holdBottomPopMenu();
            return;
        }
        super.onBackPressed();

    }
}
