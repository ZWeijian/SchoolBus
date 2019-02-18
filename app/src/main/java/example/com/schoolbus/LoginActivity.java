package example.com.schoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.internal.Util;

public class LoginActivity extends AppCompatActivity {
    Tencent mTencent ;
    Button QQlogin ;
Button QQLogout;
Button QQshare;
Button QQZoneShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //QQ第三方登录
        mTencent = Tencent.createInstance("101547191", getApplicationContext());//将123123123改为自己的AppID
        QQlogin = (Button) findViewById(R.id.qq_login);
QQLogout=(Button)findViewById(R.id.qq_logout) ;
QQshare=(Button)findViewById(R.id.qq_share);
QQZoneShare=(Button)findViewById(R.id.qq_zone_share);
        JSONObject jsonObject = null;
        boolean isValid = mTencent.checkSessionValid("101547191");
        if(!isValid) {
            Toast.makeText(LoginActivity.this, "token过期，请调用登录接口拉起手Q授权登录", Toast.LENGTH_SHORT).show();
            Log.d("zhu","!isValid");

        } else {
            Log.d("zhu","sValid");
           // jsonObject = mTencent.loadSession("101547191");
           // mTencent.initSessionCache(jsonObject);
        }

        QQlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get_simple_userinfo
                if (!mTencent.isSessionValid()) {
                    IUiListener listener = new BaseUiListener() {

                        protected void doComplete(JSONObject values) {
                           Log.d("zhu","doComplete");
                        }
                    };
                   // mTencent.login(this, "all", listener);
                    mTencent.login(LoginActivity.this, "all", listener);
                    Log.d("zhu", "login");
                }
            }
        });
QQLogout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mTencent.logout(getApplicationContext());
    }
});
QQshare.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

            final Bundle params = new Bundle();

            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "要分享的摘要");
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "https://blog.csdn.net/weixin_41504476");//这条分享消息被好友点击后的跳转URL。
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "https://pic2.zhimg.com/80/v2-20cf742eac1b2380b0301d94dd17ad61_hd.jpg");
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "测试应用");
          //  params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
            mTencent.shareToQQ(LoginActivity.this, params, new BaseUiListener());
        }

});
QQZoneShare.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

            try {
                final Bundle params = new Bundle();
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                        QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "测试标题");
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "测试简介");
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
                        "https://me.csdn.net/weixin_41504476");
                ArrayList<String> imageUrls = new ArrayList<String>();
                imageUrls.add("https://pic2.zhimg.com/80/v2-20cf742eac1b2380b0301d94dd17ad61_hd.jpg");
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
                params.putInt(QzoneShare.SHARE_TO_QQ_EXT_INT,
                        QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);

                mTencent.shareToQzone(LoginActivity.this, params,
                        new BaseUiListener());
            } catch (Exception e) {
            }
        }

});
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("zhu", "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,new BaseUiListener());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }





    private class BaseUiListener implements IUiListener {


//这个类需要实现三个方法 onComplete（）：登录成功需要做的操作写在这里
// onError onCancel 方法具体内容自己搜索

        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            Log.d("zhu","onComplete");
            /*
             * 下面隐藏的是用户登录成功后 登录用户数据的获取的方法
             * 共分为两种  一种是简单的信息的获取,另一种是通过UserInfo类获取用户较为详细的信息
             *有需要看看
             * */
           try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.v("----TAG--", "-------------"+response.toString());
               String openidString = ((JSONObject) response).getString("openid");
                mTencent.setOpenId(openidString);

                mTencent.setAccessToken(((JSONObject) response).getString("access_token"),((JSONObject) response).getString("expires_in"));


                Log.d("zhu", "openidString-------------"+openidString);
                //access_token= ((JSONObject) response).getString("access_token");              //expires_in = ((JSONObject) response).getString("expires_in");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /**到此已经获得OpneID以及其他你想获得的内容了
             QQ登录成功了，我们还想获取一些QQ的基本信息，比如昵称，头像什么的，这个时候怎么办？
             sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
             如何得到这个UserInfo类呢？  */

            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);

            //    info.getUserInfo(new BaseUIListener(this,"get_simple_userinfo"));
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    //用户信息获取到了

                    try {

                        Toast.makeText(getApplicationContext(), ((JSONObject) o).getString("nickname")+((JSONObject) o).getString("gender"), Toast.LENGTH_SHORT).show();
                        Log.d("zhu",o.toString());
                        StringBuilder builder=new StringBuilder();
                        builder.append("nickname:"+((JSONObject) o).getString("nickname")+"\n");
                        builder.append("gender:"+((JSONObject) o).getString("gender")+"\n");
                        builder.append("year:"+((JSONObject) o).getString("year")+"\n");

                        builder.append("is_yellow_vip:"+((JSONObject) o).getString("is_yellow_vip")+"\n");
                        builder.append("yellow_vip_level:"+((JSONObject) o).getString("yellow_vip_level")+"\n");
                        builder.append("is_yellow_year_vip:"+((JSONObject) o).getString("is_yellow_year_vip")+"\n");

String s=builder.toString();
                        Intent intent1 = new Intent(LoginActivity.this,TraceActivity.class);
                        intent1.putExtra("information",s);
                        startActivity(intent1);
                        finish();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    Log.d("zhu","onError");
                }

                @Override
                public void onCancel() {
                    Log.d("zhu","onCancel");
                }
            });


        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "onCancel", Toast.LENGTH_SHORT).show();
            Log.d("zhu","onCancel");
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
            Log.d("zhu","onError");
        }
    }
}
