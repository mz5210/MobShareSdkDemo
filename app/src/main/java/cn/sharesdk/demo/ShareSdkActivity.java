package cn.sharesdk.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.HashMap;
import java.util.List;

import cn.sharesdk.WebActivity;
import cn.sharesdk.alipay.friends.Alipay;
import cn.sharesdk.alipay.moments.AlipayMoments;
import cn.sharesdk.dingding.friends.Dingding;
import cn.sharesdk.douyin.Douyin;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.instagram.Instagram;
import cn.sharesdk.kakao.talk.KakaoTalk;
import cn.sharesdk.line.Line;
import cn.sharesdk.linkedin.LinkedIn;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.wework.Wework;

public class ShareSdkActivity extends AppCompatActivity implements View.OnClickListener {
    String nowSharePlatform;
    String imagePath;
    PlatformActionListener platformActionListener = new PlatformActionListener() {
        public void onError(final Platform arg0, final int arg1, final Throwable arg2) {
//            arg1 = 8 有用户信息登录 ； arg1 = 9 分享 ; arg1 = 1 无用户信息登录
            //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
            Log.d("ShareSdkActivity", arg0.getName() + "  " + arg1 + "失败 " + arg2.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ShareSdkActivity.this, arg0.getName() + "  " + arg1 + "失败 " + arg2.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void onComplete(final Platform arg0, final int arg1, final HashMap arg2) {
            //分享成功的回调
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ShareSdkActivity.this, arg0.getDb().getUserId() + "  " + arg1 + "成功 " + (arg2 == null ? "" : arg2.toString()), Toast.LENGTH_SHORT).show();
                    Log.d("mobDemo", arg0.toString() + "  " + arg1 + "成功 " + (arg2 == null ? "" : arg2.toString()));
                }
            });
        }

        public void onCancel(final Platform arg0, final int arg1) {
            //取消分享的回调

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ShareSdkActivity.this, arg0.getName() + "  " + arg1 + "取消", Toast.LENGTH_SHORT).show();

                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_sdk);

        MobSDK.submitPolicyGrantResult(true, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void aVoid) {
                Toast.makeText(ShareSdkActivity.this, "chengg ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(ShareSdkActivity.this, "throwable " + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.READ_EXTERNAL_STORAGE
                )
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .start();


    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.share:

                OnekeyShare oks = new OnekeyShare();
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle("标题");
                // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
                oks.setTitleUrl("https://mz5210.top");
//                oks.addHiddenPlatform(QQ.NAME);
//                oks.addHiddenPlatform(Wechat.NAME);
                // text是分享文本，所有平台都需要这个字段
                oks.setText("我是分享文本");
                //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
                oks.setImageUrl("https://www.tfkjy.cn/scskx/image/20200611/ae7a53f681e538ea4e132f0d9419ccdc.jpg?download=0");
                // url仅在微信（包括好友和朋友圈）中使用  如果微信是分享图片  那么不要设置URL
                oks.setUrl("https://mz5210.top");
// 设置自定义的外部回调
                oks.setCallback(platformActionListener);
                oks.show(MobSDK.getContext());
                break;
            case R.id.share_qq:
                showChoise(QQ.NAME);
                break;
            case R.id.share_weixinhaoyou:
                showChoise(Wechat.NAME);
                break;
            case R.id.share_weixinpengyouquan:
                showChoise(WechatMoments.NAME);
                break;
            case R.id.share_weixinshoucang:
                showChoise(WechatFavorite.NAME);
                break;
            case R.id.share_qqkongjian:
                showChoise(QZone.NAME);
                break;
            case R.id.share_qiyeweixin:
                showChoise(Wework.NAME);
                break;
            case R.id.share_zhifubaohaoyou:
                showChoise(Alipay.NAME);
                break;
            case R.id.share_zhifubaoshenghuoquan:
                showChoise(AlipayMoments.NAME);
                break;
            case R.id.share_douyin:
                showChoise(Douyin.NAME);
                break;
            case R.id.share_qqdenglu:
                login(QQ.NAME);
                break;
            case R.id.login_line:
                login(Line.NAME);
                break;
            case R.id.kakao_denglu:
                login(KakaoTalk.NAME);
                break;
            case R.id.login_ins:
                login(Instagram.NAME);
                break;
            case R.id.share_google:
                login(GooglePlus.NAME);
                break;
            case R.id.share_weixindenglu:
                login(Wechat.NAME);
                break;
            case R.id.share_weibodenglu:
                login(SinaWeibo.NAME);
                break;
            case R.id.share_douyindenglu:
                login(Douyin.NAME);
                break;
            case R.id.share_js:

                startActivity(new Intent(this, WebActivity.class));

                break;
            case R.id.share_facebook:

                showChoise(Facebook.NAME);

                break;
            case R.id.share_line:

                showChoise(Line.NAME);

                break;
            case R.id.share_ins:

                showChoise(Instagram.NAME);

                break;
            case R.id.share_dingding:

                showChoise(Dingding.NAME);

                break;
            case R.id.share_twitter:

                showChoise(Twitter.NAME);

                break;
            case R.id.share_weibo:

                showChoise(SinaWeibo.NAME);

                break;
            case R.id.share_lingying:

                showChoise(LinkedIn.NAME);

                break;


        }
    }

    private void login(String name) {
        Platform plat = ShareSDK.getPlatform(name);
        ShareSDK.setActivity(this);//抖音登录适配安卓9.0
        plat.setPlatformActionListener(platformActionListener);
        plat.showUser(null);
//        plat.authorize();
    }

    private void showChoise(final String pa) {
        nowSharePlatform = pa;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("选择分享内容");
        //    指定下拉列表的显示数据
        final String[] cities;
        if (pa.equals(Douyin.NAME)) {
            cities = new String[]{"本地视频", "本地图片"};
        } else if (pa.equals(Instagram.NAME)) {
            cities = new String[]{"图片", "视频", "本地图片", "本地视频"};
        } else if (pa.equals(QQ.NAME)) {
            cities = new String[]{"文字", "音乐", "图片", "链接", "多图"};
        } else if (pa.equals(Wework.NAME)) {
            cities = new String[]{"文字", "文件", "图片", "本地视频", "链接"};
        } else if (pa.equals(QZone.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "视频"};
        } else if (pa.equals(Twitter.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "视频"};
        } else if (pa.equals(Wechat.NAME)) {
            cities = new String[]{"本地图片", "文字", "图片", "文件", "链接", "音乐", "视频", "表情", "微信小程序"};
        } else if (pa.equals(WechatFavorite.NAME)) {
            cities = new String[]{"文字", "图片", "文件", "链接", "音乐", "视频"};
        } else if (pa.equals(WechatMoments.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "音乐", "视频"};
        } else if (pa.equals(Alipay.NAME)) {
            cities = new String[]{"文字", "图片", "链接"};
        } else if (pa.equals(AlipayMoments.NAME)) {
            cities = new String[]{"链接"};
        } else if (pa.equals(Facebook.NAME)) {
            cities = new String[]{"链接", "图片"};
        } else if (pa.equals(Dingding.NAME)) {
            cities = new String[]{"文字", "图片", "链接"};
        } else if (pa.equals(LinkedIn.NAME)) {
            cities = new String[]{"文字", "图片", "链接"};
        } else if (pa.equals(Line.NAME)) {
            cities = new String[]{"文字", "图片"};
        } else if (pa.equals(SinaWeibo.NAME)) {
            cities = new String[]{"文字", "图片", "链接"};
        } else {
            cities = new String[]{"链接"};
        }
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cities[which].equals("链接")) {
//                    sharePhotoAndText(pa);

                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("测试分享的文本");
                    shareParams.setTitle("测试分享的标题");
                    shareParams.setUrl("https://mz5210.top");
                    shareParams.setTitleUrl("https://mz5210.top");
                    shareParams.setImageUrl("https://oss.qiashangbao.cn/cloudHealthy/zjl.png?Expires=1747908335&OSSAccessKeyId=LTAI4Fk8UXH9m4aZyisR6i46&Signature=gBpR0Vx7W%2F4fkJPWOjPbt%2BYX5Yw%3D");
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);

                } else if (cities[which].equals("文字")) {
                    Platform.ShareParams sp = new Platform.ShareParams();
                    sp.setTitle("测试分享的标题");
                    sp.setTitleUrl("https://mz5210.top"); // 标题的超链接
                    sp.setText("测试分享的文本");
                    sp.setLinkedinDescription("测试分享的文本");
//                    sp.setImageUrl("http://y.gtimg.cn/music/photo_new/T002R300x300M000003bixR51mDMhB.jpg?max_age=2592000");
//                    sp.setSite("发布分享的网站名称");
//                    sp.setSiteUrl("发布分享网站的地址");
//                    sp.setUrl("https://mz5210.top");
                    sp.setShareType(Platform.SHARE_TEXT);
                    Platform qzone = ShareSDK.getPlatform(pa);
                    qzone.setPlatformActionListener(platformActionListener);
                    qzone.share(sp);
                } else if (cities[which].equals("图片")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setImageUrl("https://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg");
                    shareParams.setText("测试文字");//微博可以分享图片的时候加上文字
                    platform.setPlatformActionListener(platformActionListener);
                    shareParams.setShareType(Platform.SHARE_IMAGE);
                    platform.share(shareParams);


                } else if (cities[which].equals("多图")) {
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("BypassApproval", "false");
                    ShareSDK.setPlatformDevInfo(Wechat.NAME, hashMap);
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
//                    List a = new ArrayList();
//                    a.add("http://y.gtimg.cn/music/photo_new/T002R300x300M000003bixR51mDMhB.jpg?max_age=2592000");
//                    a.add("http://y.gtimg.cn/music/photo_new/T002R300x300M0000004dQi53ybc8o.jpg?max_age=2592000");
//                    a.add("http://y.gtimg.cn/music/photo_new/T002R300x300M000003yQidc3s7P65.jpg?max_age=2592000");
//                    shareParams.setImageUrlList(a);
                    shareParams.setImageArray(new String[]{"http://y.gtimg.cn/music/photo_new/T002R300x300M0000004dQi53ybc8o.jpg?max_age=2592000", "http://y.gtimg.cn/music/photo_new/T002R300x300M000003yQidc3s7P65.jpg?max_age=2592000"});

                    platform.setPlatformActionListener(platformActionListener);
//                    shareParams.setShareType(Platform.SHARE_IMAGE);
                    platform.share(shareParams);
                } else if (cities[which].equals("音乐")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("这是一首歌");
                    shareParams.setTitle("歌名");
//                    shareParams.setImagePath(ResourcesManager.getInstace(MobSDK.getContext()).getImagePath());
                    shareParams.setImageUrl("http://y.gtimg.cn/music/photo_new/T002R500x500M000003yQidc3s7P65.jpg?max_age=2592000");
                    shareParams.setUrl("http://node.kg.qq.com/play?s=--0ay2-IQH5QA-gg&g_f=personal");
                    shareParams.setTitleUrl("http://node.kg.qq.com/play?s=--0ay2-IQH5QA-gg&g_f=personal");
                    shareParams.setMusicUrl("http://bsy.stream.kg.qq.com/shkge-btfs/c5998ff8a8c473aaed08fea81676c7bb31b96f30?ftnrkey=9a4f74d8a30403f53ff2b193e2e9c131acfd0ebc56428105e768432491d8a4234b065763b4dc9ea513e4b5655be85bfde472eb0e5b87842342b53c23f8696ca6&vkey=A6BF81E50A2FB22C4A56CF6C94C6BDA672FCF9FAD897E5116AF40EDCA6238D7F653E44B911916C0A5BB26770D15A667AD5A36A32594066A20B87AA1CE7985C919FCDC4E561966EB1850FEBCDD3FD97FA02DDAD403C43C034&fname=1021_012a89e8c98089bb547e47ef83f984fc748118ac.0.m4a&fromtag=1506&sdtfrom=v1506&ugcid=9346524_1562675034_845");
                    shareParams.setShareType(Platform.SHARE_MUSIC);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                } else if (cities[which].equals("视频")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("分享的视频内容");
                    shareParams.setTitle("分享的视频标题");
                    shareParams.setUrl("http://1302109707.vod2.myqcloud.com/1fed3e20vodcq1302109707/cb9a16515285890803790087814/Jg1zfyEKkIwA.mp4");
//                    shareParams.setTitleUrl("http://tx.stream.kg.qq.com/shkge-btfs/096026e905ed1d8516be5559e778d03f07f11485?ftnrkey=9acd19b930af10f1467922c765bc2450f1ec1c6da2e521a7d637860c1fefe16153c03ab86a30a1dc178c513eb80ccb535693e00a232174eb07cb156dee100ec5&vkey=1CAADE06C6C0EC50DA7FCAAFFF16496768C4719DFE8EE979CD780EA7E2C3E0E37636A99996082A45AA975AE2D01043BB122281CFE591918F0C300FFEB66945CF9E9084BE860B193D42D6E5FF7BA2CA3263AE2292969AEAB5&fname=81_d7b9f9eec98089bb557e47ef83f9fafc598157af.0.mp4&fromtag=1508&sdtfrom=v1508&ugcid=9346524_1532238000_448");
                    shareParams.setImageUrl("http://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                    shareParams.setShareType(Platform.SHARE_VIDEO);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);

                } else if (cities[which].equals("微信小程序")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("分享的视频内容");
                    shareParams.setTitle("分享的视频标题");
                    shareParams.setUrl("http://www.mob.com");
//                    shareParams.setImagePath(ResourcesManager.getInstace(MobSDK.getContext()).getImagePath());
//                    shareParams.setImageData(ResourcesManager.getInstace(MobSDK.getContext()).getImageBmp());
                    shareParams.setImageUrl("http://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                    shareParams.setShareType(Platform.SHARE_WXMINIPROGRAM);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);

                } else if (cities[which].equals("表情")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("分享的视频内容");
                    shareParams.setTitle("分享的视频标题");
                    shareParams.setImageUrl("http://bmob.files.mz5210.top/2020/05/27/5fe6be86400577ab80d7c072a2e62a1e.gif");

                    shareParams.setShareType(Platform.SHARE_EMOJI);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                } else if (cities[which].equals("文件")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivityForResult(intent, Platform.SHARE_FILE);


                } else if (cities[which].equals("本地视频")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("video/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivityForResult(intent, Platform.SHARE_VIDEO);
                } else if (cities[which].equals("本地图片")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivityForResult(intent, Platform.SHARE_IMAGE);
                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Platform.SHARE_IMAGE:
                    Uri uri = data.getData();
                    Platform douyin = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams sp = new Platform.ShareParams();
                    String imagePath = UriUtil.convertUriToPath(this, uri);
                    Log.d("ShareSdkActivity1", imagePath);
                    sp.setImagePath(imagePath);
                    sp.setShareType(Platform.SHARE_IMAGE);
                    sp.setActivity(this);
                    douyin.setPlatformActionListener(platformActionListener);
                    douyin.share(sp);
                    break;
                case Platform.SHARE_VIDEO:
                    Uri douyinVideo = data.getData();
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setFilePath(UriUtil.convertUriToPath(this, douyinVideo));
                    shareParams.setShareType(Platform.SHARE_VIDEO);
                    shareParams.setActivity(this);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                    break;
                case Platform.SHARE_FILE:
                    Uri uri1 = data.getData();
                    Platform platform1 = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams1 = new Platform.ShareParams();
                    shareParams1.setText("分享的文件内容");
                    shareParams1.setTitle("分享的文标题");
                    shareParams1.setImageUrl("http://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");

                    try {
                        shareParams1.setFilePath(PathUtils.getPhotoPathFromContentUri(this, uri1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    shareParams1.setShareType(Platform.SHARE_FILE);
                    platform1.setPlatformActionListener(platformActionListener);
                    platform1.share(shareParams1);
                    break;

            }
        }
    }
}
