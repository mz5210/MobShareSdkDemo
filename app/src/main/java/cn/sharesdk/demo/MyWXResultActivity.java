package cn.sharesdk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

public class MyWXResultActivity extends WechatHandlerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_w_x_result);
    }

    @Override
    public void onGetMessageFromWXReq(WXMediaMessage msg) {
        super.onGetMessageFromWXReq(msg);
        Log.d("onGetMessageFromWXReq", msg.toString());
    }

    @Override
    public void onShowMessageFromWXReq(WXMediaMessage msg) {
        super.onShowMessageFromWXReq(msg);
        Log.d("onShowMessageFromWXReq ", msg.toString());

    }
}