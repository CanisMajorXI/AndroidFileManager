package com.zqw.fileoperation;

import android.support.v7.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import com.zqw.fileoperation.FileEntity;
import com.zqw.fileoperation.FileEntity.Type;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 文件列表 界面
 * @author Administrator
 *
 */
public class FileListActivity extends Activity implements OnClickListener{

    private ListView mListView;
    private Button btnComfirm;
    private MyFileAdapter mAdapter;
    private Context mContext;
    private File currentFile;
    String sdRootPath;

    private ArrayList<FileEntity> mList;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        if(mAdapter ==null){
                            mAdapter = new MyFileAdapter(mContext, mList);
                            mListView.setAdapter(mAdapter);
                        }else{
                            mAdapter.notifyDataSetChanged();
                        }

                        break;
                    case 2:

                        break;

                    default:
                        break;
                }
            }
        };

        mContext = this;
        mList = new ArrayList<>();
        sdRootPath =Environment.getExternalStorageDirectory().getAbsolutePath();
        currentFile = new File(sdRootPath);
        System.out.println(sdRootPath);
        initView();
        getData(sdRootPath);


    }

    @Override
    public void onBackPressed() {
//		super.onBackPressed();
        System.out.println("onBackPressed...");
        if(sdRootPath.equals(currentFile.getAbsolutePath())){
            System.out.println("已经到了根目录...");
            return ;
        }

        String parentPath = currentFile.getParent();
        currentFile = new File(parentPath);
        getData(parentPath);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listView1);
        btnComfirm = (Button) findViewById(R.id.button1);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final FileEntity entity = mList.get(position);
                if(entity.getFileType() == Type.FLODER){
                    currentFile = new File(entity.getFilePath());
                    getData(entity.getFilePath());
                }else if(entity.getFileType() == Type.FILE){

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(mContext, entity.getFilePath()+" "+entity.getFileName(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
    }

    private void getData(final String path) {
        new Thread(){
            @Override
            public void run() {
                super.run();

                findAllFiles(path);
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                setResult(100);
                finish();
                break;

            default:
                break;
        }

    }

    /**
     * 查找path地址下所有文件
     * @param path
     */
    public void findAllFiles(String path) {
        mList.clear();

        if(path ==null ||path.equals("")){
            return;
        }
        File fatherFile = new File(path);
        File[] files = fatherFile.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                FileEntity entity = new FileEntity();
                boolean isDirectory = files[i].isDirectory();
                if(isDirectory ==true){
                    entity.setFileType(Type.FLODER);
//					entity.setFileName(files[i].getPath());
                }else{
                    entity.setFileType(Type.FILE);
                }
                entity.setFileName(files[i].getName().toString());
                entity.setFilePath(files[i].getAbsolutePath());
                entity.setFileSize(files[i].length()+"");
                mList.add(entity);
            }
        }
        mHandler.sendEmptyMessage(1);

    }


    class MyFileAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<FileEntity> mAList;
        private LayoutInflater mInflater;



        public MyFileAdapter(Context mContext, ArrayList<FileEntity> mList) {
            super();
            this.mContext = mContext;
            this.mAList = mList;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mAList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mAList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if(mAList.get(position).getFileType() == Type.FLODER){
                return 0;
            }else{
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//			System.out.println("position-->"+position+"  ---convertView--"+convertView);
            ViewHolder holder = null;
            int type = getItemViewType(position);
            FileEntity entity = mAList.get(position);

            if(convertView == null){
                holder = new ViewHolder();
                switch (type) {
                    case 0://folder
                        convertView = mInflater.inflate(R.layout.item_listview, parent, false);
                        holder.iv = (ImageView) convertView.findViewById(R.id.item_imageview);
                        holder.tv = (TextView) convertView.findViewById(R.id.item_textview);
                        break;
                    case 1://file
                        convertView = mInflater.inflate(R.layout.item_listview, parent, false);
                        holder.iv = (ImageView) convertView.findViewById(R.id.item_imageview);
                        holder.tv = (TextView) convertView.findViewById(R.id.item_textview);

                        break;

                    default:
                        break;

                }
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            switch (type) {
                case 0:
                    holder.iv.setImageResource(R.drawable.folder1);
                    holder.tv.setText(entity.getFileName());
                    break;
                case 1:
                    holder.iv.setImageResource(R.drawable.folder1);
                    holder.tv.setText(entity.getFileName());

                    break;

                default:
                    break;
            }


            return convertView;
        }

    }

    class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}