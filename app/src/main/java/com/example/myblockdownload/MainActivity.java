package com.example.myblockdownload;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "cj";
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Button bt_donw;
    String url = "http://xmp.down.sandai.net/xmp/XMPSetup_5.4.0.6151-dl.exe";

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        bt_donw = (Button) findViewById(R.id.bt_donw);

        bt_donw.setOnClickListener(this);
        verifyStoragePermissions(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_donw:

                //模拟下载数据
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName("迅雷.exe");
                fileInfo.setContentLength(42180856L);
                fileInfo.setFileUrl("http://xmp.down.sandai.net/xmp/XMPSetup_5.4.0.6151-dl.exe");


                DownloadTask downloadTask = new DownloadTask(fileInfo,128*1024L);
                downloadTask.startDownload();

//                DownloadAsyncTask asyncTask = new DownloadAsyncTask(url, FILE_PATH, "迅雷.exe", 0L, 2047L);
//                asyncTask.execute();


                break;
        }
    }


    //然后通过一个函数来申请
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
