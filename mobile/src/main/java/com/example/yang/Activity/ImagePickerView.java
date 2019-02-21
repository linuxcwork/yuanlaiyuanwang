package com.example.yang.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.Activity
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/9/16 23:13
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class ImagePickerView extends GridView{
    public ImagePickerView(Context context) {
        super(context);
    }
  /*  //图片选择数量
    int maxImageSize = 9;

    //添加item布局
    private int noImgResource;

    //列选择数量
    private int columnNumber = 3;

    Activity context;
    ImagesAdapter adapter;

    List<String> imageList;//图片选择list


    private static final int TYPE_SHOW_ADD = 0;
    private static final int TYPE_NO_SHOW_ADD = 1;

    private boolean isShowAdd = true;

    int imageGridSize;

    public void setNoImgResource(int noImgResource) {
        this.noImgResource = noImgResource;
    }

    public void setColumnNumber(int columnNumber) {
        if (columnNumber>5){
            columnNumber = 5;
        }
        this.columnNumber = columnNumber;
        this.setNumColumns(columnNumber);
    }

    public void setShowAdd(boolean showAdd) {
        isShowAdd = showAdd;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        adapter.setImageList(imageList);
    }

    public List<String> getImageList() {
        return imageList;
    }

    public ImagePickerView(Context context) {
        this(context,null);
    }

    public ImagePickerView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    *//**
     * 初始化ImagePickerView的一些信息
     * @param context
     * @param attrs
     * @param defStyle
     *//*
    public ImagePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = (Activity) context;
        adapter = new ImagesAdapter();
        this.setAdapter(adapter);
        if (imageList==null){
            imageList = new ArrayList<>();
        }
        this.setNumColumns(columnNumber);
        this.setVerticalSpacing(10);
        this.setHorizontalSpacing(10);
        imageGridSize = (this.context.getWindowManager().getDefaultDisplay().getWidth() - Util.dp2px(context, 2) * 2) / columnNumber;
    }


    *//**
     * 提供给外部调用用来再Activity返回时获取图片信息
     * @param requestCode
     * @param resultCode
     * @param data
     *//*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data!=null&& !TextUtils.isEmpty(data.getStringExtra("photoPath"))){//拍照
            imageList.add(data.getStringExtra("photoPath"));
        }else if (data!=null&&data.getSerializableExtra("images")!=null){//图片选择
            imageList = (List<String>) data.getSerializableExtra("images");
        }else{
            List<ImageItem> list = AndroidImagePicker.getInstance().getSelectedImages();
            for (int i=0;i<list.size();i++){
                imageList.add(list.get(i).path);
            }
        }
        AndroidImagePicker.getInstance().setSelectLimit(maxImageSize-imageList.size());
        adapter.setImageList(imageList);
    }


    class ImagesAdapter extends BaseAdapter {

        List<String> imageList;

        public ImagesAdapter() {
            this.imageList = new ArrayList();
        }

        public void setImageList(List<String> imageList) {
            this.imageList = imageList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (isShowAdd){
                if (imageList == null || imageList.isEmpty()) {
                    return 1;
                }
                if (imageList.size() >= maxImageSize) {
                    return maxImageSize;
                }
                return imageList.size() + 1;
            }
            if (imageList.size() >= maxImageSize) {
                return maxImageSize;
            }
            return imageList.size()+1;
        }

        @Override
        public String getItem(int position) {
            if (isShowAdd){
                if (position==imageList.size()){
                    return null;
                }
                return imageList.get(position-1);
            }
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (isShowAdd){
                return position==imageList.size()?TYPE_SHOW_ADD:TYPE_NO_SHOW_ADD;
            }else{
                return TYPE_NO_SHOW_ADD;
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            int itemViewType = getItemViewType(position);
            if(itemViewType == TYPE_SHOW_ADD){//当前item为添加图片item
                if (noImgResource!=0){//加载用户的添加item布局
                    convertView = LayoutInflater.from(context).inflate(noImgResource, parent, false);
                }else {//默认的添加item布局
                    convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_camera, parent, false);
                }
                convertView.setTag(null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//点击选择图片
                        Intent intent = new Intent(context, ImagesGridActivity.class);//图片选择
                        Activity activity =  context;
                        activity.startActivityForResult(intent,1001);
                    }
                });
            }else{//普通item，加载图片，并对item设置点击进行预览
                final ViewHolder holder;
                if(convertView == null){
                    convertView = LayoutInflater.from(context).inflate(R.layout.image_grid_item, null);
                    holder = new ViewHolder();
                    holder.ivPic = (SimpleDraweeView)convertView.findViewById(R.id.iv_thumb);
                    holder.cbPanel = convertView.findViewById(R.id.thumb_check_panel);
                    convertView.setTag(holder);
                }else{
                    holder = (ViewHolder) convertView.getTag();
                }
                convertView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {//将选择的图片与当前postion传过去。
                        Intent intent = new Intent(context, PreviewDelActivity.class);
                        intent.putExtra("images", (Serializable) imageList);
                        intent.putExtra("position",position);
                        context.startActivityForResult(intent,1002);
                    }
                });
                ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(
                        Uri.parse(String.format("file://%s", imageList.get(position))))
                        .setResizeOptions(new ResizeOptions(imageGridSize, imageGridSize))
                        .setAutoRotateEnabled(true);
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(holder.ivPic.getController())
                        .setImageRequest(requestBuilder.build())
                        .build();
                holder.ivPic.setController(controller);
            }
            return convertView;
        }
    }

    class ViewHolder{
        SimpleDraweeView ivPic;
        View cbPanel;
    }*/

}
/*
public class PreviewDelActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = ImagePreviewActivity.class.getSimpleName();

    TextView mTitleCount;
    TextView mBtnOk;
    private ImageView backBtn;

    List<String> mImageList;

    int mShowItemPosition = 0;


    ViewPager mViewPager;

    TouchImageAdapter mAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_del);

        mImageList = (List<String>) getIntent().getSerializableExtra("images");
        mShowItemPosition = getIntent().getIntExtra("position",0);

        mBtnOk = (TextView) findViewById(R.id.btn_del);
        backBtn = (ImageView) findViewById(R.id.btn_backpress);
        mBtnOk.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        mTitleCount = (TextView) findViewById(R.id.tv_title_count);
        mTitleCount.setText(mShowItemPosition+1+"/" + mImageList.size());// 图片数量和当前图片信息展示

        initView();

        AndroidImagePicker.getInstance().clearSelectedImages();
    }

    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mAdapter = new TouchImageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mShowItemPosition, false);//设置显示当前的图片
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitleCount.setText(position+1+"/" + mImageList.size());//滑动viewPager时更新显示信息
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_del) {//删除按钮点击
            mAdapter.remove(mViewPager.getCurrentItem());//
            mTitleCount.setText(mViewPager.getCurrentItem()+1+"/" + mImageList.size());
            if (mImageList.size()==0){
                Intent intent = new Intent();
                intent.putExtra("images", (Serializable) mImageList);
                setResult(RESULT_OK,intent);
                finish();
            }
        }else if (i==R.id.btn_backpress){//返回
            Intent intent = new Intent();
            intent.putExtra("images", (Serializable) mImageList);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putExtra("images", (Serializable) mImageList);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class TouchImageAdapter extends FragmentStatePagerAdapter {
        public TouchImageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        public void remove(int position){
            mImageList.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            SinglePreviewFragment fragment = new SinglePreviewFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SinglePreviewFragment.KEY_URL, mImageList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }

    }

    @SuppressLint("ValidFragment")
    private class SinglePreviewFragment extends Fragment {
        public static final String KEY_URL = "key_url";
        private PhotoDraweeView photoDraweeView;
        private String url;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();

            url = (String) bundle.getSerializable(KEY_URL);
            Log.i(TAG, "=====current show image path:" + url);

            photoDraweeView = new PhotoDraweeView(getActivity());
            photoDraweeView.setBackgroundColor(0xff000000);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            photoDraweeView.setLayoutParams(params);

            photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    getActivity().finish();
                }
            });
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "file://"+url;
            }

            ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(url))
                    .setResizeOptions(new ResizeOptions(768,1280))
                    .setAutoRotateEnabled(true);

            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setOldController(photoDraweeView.getController());
            controller.setImageRequest(requestBuilder.build());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return photoDraweeView;
        }
         }

}

 */
