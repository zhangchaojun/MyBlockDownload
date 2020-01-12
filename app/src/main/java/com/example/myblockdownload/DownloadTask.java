package com.example.myblockdownload;

import android.os.Environment;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cj on 2020/1/12.
 * 一个文件下载任务
 */
public class DownloadTask {

    private static final String TAG = "cj";
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private FileInfo fileInfo;
    private long blockLength;
    private int blockCounts;
    private long lastBlockLength;
    private final ExecutorService executorService;


    public DownloadTask(FileInfo fileInfo, long blockLength) {
        this.fileInfo = fileInfo;
        this.blockLength = blockLength;

        initDownloadTask();
        executorService = Executors.newFixedThreadPool(10);
        
    }


    /**
     * 根据传入数据初始化一个文件下载任务
     */
    private void initDownloadTask() {
        long contentLength = fileInfo.getContentLength();
        // 若是总文件大小 比我们设定的块还小 ,则修改blocklength为总大小
        blockLength = blockLength > contentLength ? contentLength : blockLength;
        blockCounts = getBlockCounts(contentLength, blockLength);
        lastBlockLength = getLastBlockFileSize(contentLength, blockLength);
        Log.e(TAG, "总分块数=" + blockCounts);

    }

    /**
     * 根据文件总长度，每块文件长度 获取块数
     *
     * @param contentLength 文件总长度
     * @param blockLength   每块文件长度
     * @return int 总块数
     */
    private int getBlockCounts(long contentLength, long blockLength) {
        // 若所传数据均不合法，则默认为一块
        if (contentLength <= 0 || blockLength <= 0 || contentLength <= blockLength) {
            return 1;
        }
        // 是否有余数
        boolean hasRemainder = (contentLength % blockLength) != 0;
        int blockSize = (int) (contentLength / blockLength);
        return hasRemainder ? blockSize + 1 : blockSize;
    }


    private long getLastBlockFileSize(long totalFileSize, long blockFileSize) {
        if (totalFileSize <= 0 || blockFileSize <= 0) {
            return 0;
        }
        long lastBlockFileSize = totalFileSize % blockFileSize;
        return lastBlockFileSize == 0 ? blockFileSize : lastBlockFileSize;
    }


    public void startDownload() {
        for (int i = 0; i < blockCounts; i++) {
            // 对应块号应该的开始位置。
            long startIndex = i * blockLength;
            // 获取对应块号结束位置
            long endIndex = i == blockCounts - 1 ? startIndex + lastBlockLength : startIndex + blockLength - 1;
            Log.e(TAG, "第" + i + "块：" + startIndex + "-" + endIndex);
            DownloadAsyncTask asyncTask = new DownloadAsyncTask(fileInfo.getFileUrl(), FILE_PATH, fileInfo.getName(), startIndex, endIndex);
            asyncTask.executeOnExecutor(executorService);
        }


    }

}
