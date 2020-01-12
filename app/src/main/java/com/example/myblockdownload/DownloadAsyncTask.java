package com.example.myblockdownload;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


/**
 * Created by cj on 2020/1/11.
 * 一块文件下载执行
 */
public class DownloadAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "cj";
    private String FILE_PATH;
    private String FILE_NAME;
    private String downUrl;
    private long startIndex;
    private long endIndex;
    private File cacheFile;


    public DownloadAsyncTask(String downUrl, String FILE_PATH, String FILE_NAME, long startIndex, long endIndex) {
        this.downUrl = downUrl;
        this.FILE_PATH = FILE_PATH;
        this.FILE_NAME = FILE_NAME;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        initCacheFile();
    }

    private void initCacheFile() {
        cacheFile = new File(FILE_PATH, FILE_NAME);
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
        Log.e(TAG, "onPostExecute:下载结果=" + s);

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        RandomAccessFile raf = null;
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(downUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
            connection.connect();
            int responseCode = connection.getResponseCode();

            Log.e(TAG, "onClick: " + responseCode);
            String responseMessage = connection.getResponseMessage();
            Log.e(TAG, "doInBackground: " + responseMessage);
            int contentLength = connection.getContentLength();
            Log.e(TAG, "doInBackground: " + (contentLength));

            if (responseCode == HttpURLConnection.HTTP_PARTIAL || responseCode == HttpURLConnection.HTTP_OK) {
                //得到响应流
                inputStream = connection.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                raf = new RandomAccessFile(cacheFile, "rwd");
                raf.seek(startIndex);
                while ((len = inputStream.read(bytes)) != -1) {
//                    Log.e(TAG, "length=" + len + "   :" + Arrays.toString(bytes));
                    raf.write(bytes, 0, len);
                }
                return true;
            } else {
                Log.e(TAG, "下载失败 原因："+"code!=200 || 206");
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "下载失败 原因：" + e.getMessage());
            return false;
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

    }
}
