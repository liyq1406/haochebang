package com.haoche51.checker.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.haoche51.checker.GlobalData;

import java.io.File;

/**
 * Created by mac on 15/11/19.
 */
public class DownloadUtil {

  public static final String MRECORDDIR_NAME = "HCCheckerDownload";//保存目录
  public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + MRECORDDIR_NAME + File.separator;

  public static long mDownloadId = 0;
  public static DownloadManager mDownloadManager = null;
  public static BroadcastReceiver mReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
//      if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
//        // 下载完成
//      } else
      if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
        long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
        mDownloadManager.remove(ids);
        ToastUtil.showText("已经取消下载");
      }
    }
  };

  public static void downloadUseDownloadManager(Context context, String downloadUrl, String title) {
    String fileName = "";
    mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    Uri downloadUri = Uri.parse(downloadUrl);

    DownloadManager.Request request = new DownloadManager.Request(downloadUri);

    // 下载存储路径
    // 文件将存放在外部存储的确实download文件内，如果无此文件夹，创建之，如果有，下面将返回false。
    // 不同的手机不同Android版本的SD卡的挂载点可能会不一样，因此通过系统方式获取。
    File mDir = new File(DOWNLOAD_PATH);
    if (!mDir.exists()) {
      mDir.mkdirs();
    }

    if ("车检报告".equals(title)) {
      fileName = getFileName(downloadUrl, 0);
    } else if ("车鉴定报告".equals(title)) {
      fileName = getFileName(downloadUrl, 1);
    }

    request.setDestinationInExternalPublicDir(MRECORDDIR_NAME, fileName);

    // 下载文件类型
    String extension = MimeTypeMap.getFileExtensionFromUrl(downloadUrl);
    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    request.setMimeType(mimeType);
    // 设置通知栏UI是否可见
    request.allowScanningByMediaScanner();
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    // 下载标题
    request.setTitle(fileName);
    request.setVisibleInDownloadsUi(true);
    mDownloadId = mDownloadManager.enqueue(request);

  }


  /**
   * @param downloadUrl
   * @param flag        0:车检报告  1：车鉴定报告
   * @return
   */
  public static String getFileName(String downloadUrl, Integer flag) {
    String fileName = "";
    Uri downloadUri = Uri.parse(downloadUrl);
    String id = downloadUri.getQueryParameter("id");
    String uid = downloadUri.getQueryParameter("uid");
    String vinCode = downloadUri.getQueryParameter("vin_code");
    HCLogUtil.e("id-->" + id + "||uid-->" + uid);

    switch (flag) {
      case 0:
        fileName = "checker_" + id + "_" + uid + ".pdf";
        break;
      case 1:
        uid = String.valueOf(GlobalData.userDataHelper.getChecker().getId());
        fileName = "cjd_" + vinCode + "_" + uid + ".pdf";
        break;
    }
    return fileName;
  }



}
