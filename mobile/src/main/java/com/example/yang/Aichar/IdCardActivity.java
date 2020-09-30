package com.example.yang.Aichar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.UserRegister;
import com.example.yang.Activity.AlbumSelectChat;
import com.example.yang.util.AuthService;
import com.example.yang.util.RotateTransformation;

import java.util.ArrayList;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.Aichar
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/1/5 10:42
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class IdCardActivity extends Activity implements View.OnClickListener {
    private ImageButton idcardfront;
    private ImageButton idcardback;
    private Button next;
    private Button jump;
    private ImageView back;
    private IdCardIdtfcation idCardIdtfcation;
    private Bundle bundle = null;
    private int from = 0;
    private int froncode = 0;
    private int backcode = 1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String path= (String) msg.obj;
            switch (msg.what){
                case 0:
                    Glide
                            .with(IdCardActivity.this)
                            .load(path)
                            .transform(new RotateTransformation(IdCardActivity.this,90f))
                            .thumbnail(2f)
                            .into(idcardfront);
                    break;
                case 1:
                    Glide
                            .with(IdCardActivity.this)
                            .load(path)
                            .transform(new RotateTransformation(IdCardActivity.this,90f))
                            .thumbnail(1f)
                            .into(idcardback);
                    break;
                    default:
            }
            return false;
        }
	});
	
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);
        AuthService authService = new AuthService();
        authService.getAuth("O2ygXFpcAkiOh7iuwsxZ2pE0","b1Sqee3dBB5hMMEtmiI8HHcZQPDN8nGi ");

        init_activity();

        Intent incomming = getIntent();
        bundle = incomming.getExtras();
        checkGalleryPermission();
    }

    private void init_activity(){
        back = findViewById(R.id.id_card_back);
        jump = findViewById(R.id.id_card_jump);
        idcardfront = findViewById(R.id.id_card_front_button);
        idcardback = findViewById(R.id.id_card_back_button);
        next = findViewById(R.id.id_next);

        back.setOnClickListener(this);
        jump.setOnClickListener(this);
        idcardfront.setOnClickListener(this);
        idcardback.setOnClickListener(this);
        next.setOnClickListener(this);

        idCardIdtfcation = new IdCardIdtfcation(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_card_back:
                Intent frontactivity = new Intent(IdCardActivity.this,UserRegister.class);
                startActivity(frontactivity);
                finish();
                break;
            case R.id.id_card_jump:
                Intent mainactivity = new Intent(IdCardActivity.this,MainActivity.class);
                if(from == 1){
                    mainactivity.putExtras(bundle);
                }
                startActivity(mainactivity);
                finish();
                break;
            case R.id.id_card_front_button:
                Intent fronintent = new Intent(this,AlbumSelectChat.class);
                /* 取得相片后返回本画面 */
                fronintent.putExtra("activity","idcard");
                startActivityForResult(fronintent,froncode);
                break;
            case R.id.id_card_back_button:
                Intent backintent = new Intent(this,AlbumSelectChat.class);
                /* 取得相片后返回本画面 */
                backintent.putExtra("activity","idcard");
                startActivityForResult(backintent,backcode);
                break;
            case R.id.id_next:

                break;
            default:
                System.out.println("no this button");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String s = null;
        Message message = new Message();
        System.out.println("requestCode:"+requestCode+",resultCode:"+resultCode);
        if(resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            if(bundle != null) {
                ArrayList mdata = bundle.getCharSequenceArrayList("picture");
                System.out.println(mdata);
                if(mdata != null) {
                    s = mdata.get(0).toString();
                    message.obj = s;
                } else {
                    System.out.println("mdata is null");
                    return;
                }
            } else {
                System.out.println("bundle is null");
                return;
            }
            System.out.println(s);
            switch (requestCode){
                case 0:
                    message.what = 0;
                    idCardIdtfcation.identificationAction(s,this,"front");
                    break;
                case 1:
                    message.what = 1;
                    idCardIdtfcation.identificationAction(s,this,"back");
                    break;
                    default:
                       System.out.println("no match requestcode");
            }
            handler.sendMessage(message);

        }
    }

    private boolean checkGalleryPermission() {
        int ret = ActivityCompat.checkSelfPermission(IdCardActivity.this, Manifest.permission
                .READ_EXTERNAL_STORAGE);
        if (ret != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(IdCardActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
            return false;
        }
        return true;
    }
}
