package magazine.ciweek.com.magazine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.DESCRIPTOR);

    private Button button;
    private Button out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.login);
        out = (Button) findViewById(R.id.out);


        addWXPlatform();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(SHARE_MEDIA.WEIXIN);
            }
        });
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(SHARE_MEDIA.WEIXIN);
            }
        });
    }

    private void addWXPlatform() {

        String appId = "wx214ad894fe5a2049";
        String appSecret = "d4624c36b6795d1d99dcf0547af5443d";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(MainActivity.this, appId,
                appSecret);
        wxHandler.addToSocialSDK();


    }

    private void logout(final SHARE_MEDIA platform) {
        mController.deleteOauth(MainActivity.this, platform, new SocializeListeners.SocializeClientListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                String showText = "解除" + platform.toString() + "平台授权成功";
                if (status != StatusCode.ST_CODE_SUCCESSED) {
                    showText = "解除" + platform.toString() + "平台授权失败[" + status + "]";
                }
                Toast.makeText(MainActivity.this, showText, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(MainActivity.this, platform,
                new SocializeListeners.UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                        Toast.makeText(MainActivity.this, "授权开始",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SocializeException e,
                                        SHARE_MEDIA platform) {
                        Toast.makeText(MainActivity.this, "授权失败",
                                Toast.LENGTH_SHORT).show();
                        Log.e("w", e.toString());
                    }

                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
                        // 获取uid
                        String uid = value.getString("uid");
                        if (!TextUtils.isEmpty(uid)) {
                            // uid不为空，获取用户信息
                            getUserInfo(platform);
                        } else {
                            Toast.makeText(MainActivity.this, "授权失败...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(MainActivity.this, "授权取消",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getUserInfo(SHARE_MEDIA platform) {
        mController.getPlatformInfo(MainActivity.this, platform,
                new SocializeListeners.UMDataListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if (info != null) {
                            Toast.makeText(MainActivity.this, info.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
