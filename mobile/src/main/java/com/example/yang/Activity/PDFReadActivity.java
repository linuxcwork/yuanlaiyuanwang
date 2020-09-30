package com.example.yang.Activity;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yang.myapplication.R;

import java.io.File;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;

/**
 * @Description: PDF阅读，可加载assets/SD卡/URL(在线预览)；
 * 通过URL地址下载PDF文档到本地，然后再浏览;
 * @Author: yzq
 * @Date: 2019/8/17 17:10
 * @Version: 1.0.0
 */
public class PDFReadActivity extends Activity implements View.OnClickListener{

    private LinearLayout rlRoot;
    private PDFViewPager remotePDFViewPager;
    private PDFPagerAdapter pdfPagerAdapter;
    private static final String ROOT_PATH = "/mnt/sdcard/pdf";
    private String url = "";//"http://cdn.mozilla.net/pdfjs/tracemonkey.pdf" 测试URL
    private String fileSize;
    //private DownloadRequest downloadRequest;
    private RelativeLayout rlDownloadContainer;
    private TextView tvDlFileName;
    private TextView tvDlProgress;
    private ProgressBar pbDownload;
    private Button btnDownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_reader_pdf);
        url = "http://cdn.mozilla.net/pdfjs/tracemonkey.pdf";
        //Intent intent = getIntent();
       // Bundle bundle = intent.getExtras();
       // String title = bundle.getString("title");

        //文件大小，这里如果自己知道文档的大小，可以传入
        fileSize = Formatter.formatFileSize(this, getIntent().getIntExtra("fileSize", 0));
        ImageView returnpre = findViewById(R.id.thoughtimagereturn);
        Button discuss = findViewById(R.id.thoughtreport);
        returnpre.setOnClickListener(this);
        discuss.setOnClickListener(this);
        init();
        //downloadPdf(url, title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.thoughtimagereturn:
                Intent returnthought = new Intent(this,LandOfThought.class);
                startActivity(returnthought);
                break;
            case R.id.thoughtreport:
                Intent discuss = new Intent(this, RichEditorText.class);
                startActivity(discuss);
                break;
        }
    }

    private void init() {
        rlRoot = findViewById(R.id.rl_pdf_view);
        remotePDFViewPager = findViewById(R.id.pdf_view);
    }


    /**
     * @param path 文件本地路径
     * 该PDF库只支持5.0及以上的手机，5.0以下的需要第三方软件打开
     */
    private void setData(String path) {
        //该PDF库只支持api大于等于21，否则用本地第三方软件打开
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (pdfPagerAdapter == null) {
                pdfPagerAdapter = new PDFPagerAdapter(this, path);
                remotePDFViewPager.setAdapter(pdfPagerAdapter);
            } else {
                pdfPagerAdapter.notifyDataSetChanged();
            }
            remotePDFViewPager.addOnPageChangeListener(onPageChangeListener);
            setPageNum(remotePDFViewPager.getCurrentItem());
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(path);
            intent.setDataAndType(uri, "application/pdf");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {

            }
        }
    }

    private void setPageNum(int position) {
        //设置页面的指示，当前页为position + 1和总页数pdfPagerAdapter.getCount()

    }


    private void downloadPdf(String url, String fileName) {
        File file = new File(ROOT_PATH + "/" + fileName);
        if (file.exists()) {
            setData(file.getPath());
            return;
        }
        showDownloadView(fileName);
    }

    private void showDownloadView(String fileName) {
        tvDlFileName.setText(fileName);
        rlDownloadContainer.setVisibility(View.VISIBLE);
        //download String为字符“下载”,自行定义
        /*btnDownload.setText(String.format("%s(%s)", getString(R.string.download), fileSize));
        btnDownload.setOnClickListener(v -> {
            if (downloadRequest == null) {
                downloadRequest = NoHttp.createDownloadRequest(url, ROOT_PATH, fileName, true, false);
                CallServer.getInstance().download(0, downloadRequest, downloadListener);
            }
        });*/
    }

    /**
     * 页面变化的监听器
     */
    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setPageNum(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 下载进度监听
     */
    /*private final DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadError(int what, Exception exception) {
            //下载错误
        }

        @Override
        public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
            if (btnDownload != null) {
                btnDownload.setVisibility(View.GONE);
                tvDlProgress.setVisibility(View.VISIBLE);
                pbDownload.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onProgress(int what, int progress, long fileCount, long speed) {
            pbDownload.setProgress(progress);
            tvDlProgress.setText(String.format("%s(%s/%s)", getResources().getString(R.string.downloading), Formatter.formatFileSize(PDFReaderActivity.this, fileCount), fileSize));
        }

        @Override
        public void onFinish(int what, String filePath) {
            setData(filePath);
            if (remotePDFViewPager.getVisibility() != View.VISIBLE) {
                remotePDFViewPager.setVisibility(View.VISIBLE);
            }
            rlDownloadContainer.setVisibility(View.GONE);
        }

        @Override
        public void onCancel(int what) {

        }
    };*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //需要手动关闭资源，防止内存泄漏
        if (pdfPagerAdapter != null) {
            pdfPagerAdapter.close();
        }
        /*if (downloadRequest != null) {
            downloadRequest.cancel();
        }*/
    }
}

