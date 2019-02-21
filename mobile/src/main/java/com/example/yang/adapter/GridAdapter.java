package com.example.yang.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.example.yang.InterfaceClass.sendDataToAvtivityInterface;
import com.example.yang.myapplication.R;
import com.example.yang.util.ViewHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import static java.lang.Thread.sleep;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.adapter
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/1/6 19:13
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class GridAdapter extends RecyclerView.Adapter<ViewHolder>{
    private LayoutInflater inflater;
    ArrayList pathtemp = new ArrayList();

    //已选择图片数量
    private int mcountview;
    private Cursor cursor;

    private Context mcontext;
    private int getpictureamount = 0;
    private ViewHolder viewHolder;
    private List<Map<String, Object>> listItemsAdpt = new ArrayList<>();
    private sendDataToAvtivityInterface senddatatoavtivityinterface;

    boolean isDragging = false;//判断scroll是否是用户主动拖拽
    boolean isScrolling = false;//判断scroll是否处于滑动中

    /**************************************************************************
     * Name     ： GridAdapter
     * descript ： 适配器构造器
     * return   ： 无
     ***************************************************************************/
    public GridAdapter(Context context,int count) {
        mcontext = context;
        inflater = LayoutInflater.from(context);
        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null);
        mcountview = 0;
        this.getpictureamount = count;
    }

    public void sendData(sendDataToAvtivityInterface sendDataToAvtivityInterface){
        this.senddatatoavtivityinterface = sendDataToAvtivityInterface;
    }


    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String name = null;
                System.out.println(cursor.getCount());
                ArrayList names = new ArrayList();
                //ArrayList descs = new ArrayList();
                ArrayList fileNames = new ArrayList();
                for (int i = 0; cursor.moveToNext(); i++) {
                    //获取图片的名称
                    name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    //获取图片的生成日期
                    byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    //获取图片的详细信息
                    //String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
                    names.add(name);
                    //descs.add(desc);
                    fileNames.add(new String(data, 0, data.length - 1));
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    //map.put("desc", desc);
                    map.put("state", false);
                    listItemsAdpt.add(map);
                    //System.out.println("GridAdapter update " + "cursor:"+cursor.getCount() + "  " +"listItemsAdpt size:"+listItemsAdpt.size());

                }
            }
        }).start();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("onCreateViewHolder");
        View convertView = inflater.inflate(R.layout.album_girdview, parent, false);
        viewHolder = new ViewHolder(convertView);

        viewHolder.mImg = (ImageView) convertView.findViewById(R.id.picture_album);
        viewHolder.mImg.setOnClickListener(mOnClickListener);
        viewHolder.checkBox = convertView.findViewById(R.id.select_checkbox);
        viewHolder.checkBox.setOnCheckedChangeListener(compoundButton);
        return viewHolder;
    }

    private CompoundButton.OnCheckedChangeListener compoundButton=new CompoundButton.OnCheckedChangeListener(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = (int) buttonView.getTag();
            if (listItemsAdpt.get(position).get("state").equals(true)) {
                mcountview = mcountview - 1;
                listItemsAdpt.get(position).replace("state", false);
                //holder.checkBox.setChecked(false);
                pathtemp.remove(listItemsAdpt.get(position).get("name"));
                senddatatoavtivityinterface.dataCon(mcountview, pathtemp, listItemsAdpt);
            } else if (mcountview < getpictureamount) {
                mcountview = mcountview + 1;
                listItemsAdpt.get(position).replace("state", true);
                pathtemp.add(listItemsAdpt.get(position).get("name"));
                senddatatoavtivityinterface.dataCon(mcountview, pathtemp, listItemsAdpt);
            } else {
                buttonView.setChecked(false);
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            //int position = (int) v.getTag();
             switch (v.getId()){
                 case R.id.picture_album:
                     /*if (listItemsAdpt.get(position).get("state").equals(true)) {
                         //holder.checkBox.setChecked(false);
                         listItemsAdpt.get(position).replace("state", false);
                         mcountview = mcountview -1;
                         senddatatoavtivityinterface.dataCon(mcountview, pathtemp, listItemsAdpt);
                         pathtemp.remove(listItemsAdpt.get(position).get("name"));
                     } else if (mcountview < getpictureamount) {
                         //holder.checkBox.setChecked(true);
                         listItemsAdpt.get(position).replace("state", true);
                         mcountview = mcountview + 1;
                         senddatatoavtivityinterface.dataCon(mcountview, pathtemp, listItemsAdpt);
                         pathtemp.add(listItemsAdpt.get(position).get("name"));
                     }*/
                     break;
                     default:
                         System.out.println("GridAdapter onClick no id ");
            }
        }
    };

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (listItemsAdpt.size() > 0) {
            //System.out.println("GridAdapter onBindViewHolder " + "position" + position + " listItems:" + listItemsAdpt.get(position).get("name") + " selectstate:" + listItemsAdpt.get(position).get("state").equals(true));
            Glide
                    .with(mcontext)
                    .load(listItemsAdpt.get(position).get("name").toString())
                    .override(200, 400)
                    .centerCrop()
                    .into(holder.mImg);
            holder.checkBox.setChecked(listItemsAdpt.get(position).get("state").equals(true));

        }
        //holder.mImg.setTag(position);
        holder.checkBox.setTag(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getItemCount() {
        //System.out.println(cursor.getCount());
        return cursor.getCount();
    }

    public void SetScrollState(int state){
        switch (state){
            case 0:
                isDragging = true;
                isScrolling = true;
                break;
            case 1:
                isDragging = false;
                isScrolling = true;
                break;
            case 2:
                isDragging = false;
                isScrolling = false;
                break;
                default:
                    System.out.println("SetScrollState");
        }
    }


    /**************************************************************************
     * Name ：cropBitmap
     * descript ：裁剪图片
     * return ： bitmap
     ***************************************************************************/
    /*private Bitmap cropBitmap(Bitmap bitmap) {

        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        //cropWidth /= 2;
        int cropHeight = cropWidth;//(int) //(cropWidth / 1.2);
        return Bitmap.createBitmap(bitmap, 0, 0, cropWidth, cropHeight, null, false); //w / 3

    }*/


    /*private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 480f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 5;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }*/

    /*private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }*/

    /**
     * 滑动自动加载监听器
     */
    /*class AutoLoadScrollListener extends RecyclerView.OnScrollListener {

        private ImageLoader imageLoader;
        private final boolean pauseOnScroll;
        private final boolean pauseOnFling;

        public AutoLoadScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
            super();
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
            this.imageLoader = imageLoader;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            //由于GridLayoutManager是LinearLayoutManager子类，所以也适用
            if (getLayoutManager() instanceof LinearLayoutManager) {
                int lastVisibleItem = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = AutoLoadRecyclerView.this.getAdapter().getItemCount();

                //有回调接口，且不是加载状态，且计算后剩下2个item，且处于向下滑动，则自动加载
                if (loadMoreListener != null && !isLoadingMore && lastVisibleItem >= totalItemCount -
                        2 && dy > 0) {
                    loadMoreListener.loadMore();
                    isLoadingMore = true;
                }
            }
        }

        //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；由于用户的操作，屏幕产生惯性滑动时为2
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            //根据newState状态做处理
            if (imageLoader != null) {
                switch (newState) {
                    case 0:
                        imageLoader.resume();
                        break;

                    case 1:
                        if (pauseOnScroll) {
                            imageLoader.pause();
                        } else {
                            imageLoader.resume();
                        }
                        break;

                    case 2:
                        if (pauseOnFling) {
                            imageLoader.pause();
                        } else {
                            imageLoader.resume();
                        }
                        break;
                }
            }
        }
    }*/

}
