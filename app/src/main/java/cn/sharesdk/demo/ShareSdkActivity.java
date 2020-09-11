package cn.sharesdk.demo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.mob.tools.utils.BitmapHelper;
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
import cn.sharesdk.facebookmessenger.FacebookMessenger;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.loopshare.LoopSharePasswordListener;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.instagram.Instagram;
import cn.sharesdk.kakao.talk.KakaoTalk;
import cn.sharesdk.line.Line;
import cn.sharesdk.linkedin.LinkedIn;
import cn.sharesdk.meipai.ShareActivity;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.wework.Wework;
import cn.sharesdk.whatsapp.WhatsApp;

public class ShareSdkActivity extends AppCompatActivity implements View.OnClickListener {
    String nowSharePlatform;
    String imagePath;
    private ClipboardManager cm;
    private ClipData mClipData;
    PlatformActionListener platformActionListener = new PlatformActionListener() {
        public void onError(final Platform arg0, final int arg1, final Throwable arg2) {
//            arg1 = 8 有用户信息登录 ； arg1 = 9 分享 ; arg1 = 1 无用户信息登录
            //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
            Log.d("ShareSdkActivity", arg0.getName() + "  " + arg1 + "失败 " + arg2.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showNormalDialog(arg0.getName() + "  失败\n" + arg2.getMessage());
//                    Toast.makeText(ShareSdkActivity.this, arg0.getName() + "  " + arg1 + "失败 " + arg2.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void onComplete(final Platform arg0, final int arg1, final HashMap arg2) {
            //分享成功的回调

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(ShareSdkActivity.this, arg0.getDb().getExpiresTime() + "  " + arg0.getDb().getUserId() + "  " + arg1 + "成功 " + (arg2 == null ? "" : arg2.toString()), Toast.LENGTH_SHORT).show();
                    showNormalDialog(arg0.getName() + "  " + (arg1 == 1 ? "无用户信息登录" : (arg1 == 8 ? "有用户信息登录" : (arg1 == 9 ? "分享" : ""))) + "成功\n" + arg0.getDb().exportData() + "\n" + (arg2 == null ? "" : arg2.toString()));

                    Log.d("mobDemo", arg0.getDb().exportData() + "  " + "  " + arg1 + "成功 " + (arg2 == null ? "" : arg2.toString()));
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
                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform, Platform.ShareParams shareParams) {
                        shareParams.setShareType(Platform.SHARE_TEXT);
                    }
                });
                //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
                oks.setImageUrl("https://www.tfkjy.cn/scskx/image/20200611/ae7a53f681e538ea4e132f0d9419ccdc.jpg?download=0");
                // url仅在微信（包括好友和朋友圈）中使用  如果微信是分享图片  那么不要设置URL
                oks.setUrl("https://mz5210.top");

// 设置自定义的外部回调
                oks.setCallback(platformActionListener);
                oks.show(MobSDK.getContext());
                break;
            case R.id.share_kouling:
                testQuickPassWord();
                break;
            case R.id.share_huanyuankouling:
                parasQuickPassWord();
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
            case R.id.share_FacebookMessenger:
                showChoise(FacebookMessenger.NAME);
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

            case R.id.share_google:
                showChoise(GooglePlus.NAME);
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
            case R.id.share_kakaotalk:

                showChoise(KakaoTalk.NAME);

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
            case R.id.share_whatapp:

                showChoise(WhatsApp.NAME);

                break;
        }
    }

    private void parasQuickPassWord() {
        ShareSDK.readPassWord(true, new LoopSharePasswordListener() {
            //复制口令中的信息会在var1返回
            @Override
            public void onResult(Object var1) {
                Log.d("ShareSDK", " onResult " + var1);
                Toast.makeText(ShareSdkActivity.this, " onResult " + var1, Toast.LENGTH_SHORT).show();
                //获取信息效果
                // onResult {key2=value2, key4=value4, key1=value1, key3=value3}
            }

            @Override
            public void onError(Throwable var1) {
                Log.d("ShareSDK", " onError " + var1);
                Toast.makeText(ShareSdkActivity.this, " onError " + var1, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void testQuickPassWord() {

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");

        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("params", map);

        String paramasStr = "想你所想，高端品牌适合高端的你";
        ShareSDK.preparePassWord(paramsMap, paramasStr, new LoopSharePasswordListener() {
            //生成口令信息会在var1返回
            @Override
            public void onResult(Object var1) {
                Log.d("ShareSDK", "onResult " + var1);
                //获取剪贴板管理器：
                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
// 创建普通字符型ClipData
                mClipData = ClipData.newPlainText("Label", var1.toString());
// 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(ShareSdkActivity.this, var1.toString() + "   已复制到剪贴板", Toast.LENGTH_SHORT).show();
                //生成口令效果
                //onResult 想你所想，高端品牌适合高端的你#UFJJbi#
            }

            @Override
            public void onError(Throwable var1) {
                Log.d("ShareSDK", "onError " + var1);
                Toast.makeText(ShareSdkActivity.this, "onError " + var1, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showChoise(final String pa) {
        nowSharePlatform = pa;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("选择操作");


        //    指定下拉列表的显示数据
        final String[] cities;
        if (pa.equals(Douyin.NAME)) {
            cities = new String[]{"本地视频", "本地图片", "授权", "登录"};
        } else if (pa.equals(Instagram.NAME)) {
            cities = new String[]{"图片", "视频", "本地图片", "本地视频", "授权", "登录"};
        } else if (pa.equals(QQ.NAME)) {
            cities = new String[]{"文字", "音乐", "图片", "链接", "多图", "授权", "登录"};
        } else if (pa.equals(Wework.NAME)) {
            cities = new String[]{"文字", "文件", "图片", "本地视频", "链接", "授权", "登录"};
        } else if (pa.equals(QZone.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "视频"};
        } else if (pa.equals(Twitter.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "视频", "授权", "登录"};
        } else if (pa.equals(Wechat.NAME)) {
            cities = new String[]{"本地图片", "文字", "bitmap图片", "图片", "文件", "链接", "音乐", "视频", "表情", "分享微信小程序", "打开微信小程序", "授权", "登录"};
        } else if (pa.equals(WechatFavorite.NAME)) {
            cities = new String[]{"文字", "图片", "文件", "链接", "音乐", "视频"};
        } else if (pa.equals(WechatMoments.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "音乐", "视频"};
        } else if (pa.equals(Alipay.NAME)) {
            cities = new String[]{"文字", "图片", "本地图片", "bitmap图片", "链接", "授权", "登录"};
        } else if (pa.equals(KakaoTalk.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "授权", "登录"};
        } else if (pa.equals(GooglePlus.NAME)) {
            cities = new String[]{"授权", "登录"};
        } else if (pa.equals(AlipayMoments.NAME)) {
            cities = new String[]{"链接"};
        } else if (pa.equals(Facebook.NAME)) {
            cities = new String[]{"链接", "图片", "本地图片", "bitmap图片", "授权", "登录"};
        } else if (pa.equals(Dingding.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "授权", "登录"};
        } else if (pa.equals(LinkedIn.NAME)) {
            cities = new String[]{"文字", "图片", "链接"};
        } else if (pa.equals(Line.NAME)) {
            cities = new String[]{"文字", "图片", "授权", "登录"};
        } else if (pa.equals(SinaWeibo.NAME)) {
            cities = new String[]{"文字", "图片", "链接", "多图", "授权", "登录"};
        } else if (pa.equals(WhatsApp.NAME)) {
            cities = new String[]{"文字", "图片", "图文", "授权", "登录"};
        } else if (pa.equals(FacebookMessenger.NAME)) {
            cities = new String[]{"图片", "本地图片", "链接"};
        } else {
            cities = new String[]{"链接"};
        }
        //    设置一个下拉的列表选择项
        builder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (cities[which].equals("链接")) {

                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    if (nowSharePlatform.equals(Facebook.NAME)) {
                        shareParams.setUrl("https://developers.facebook.com");
                    } else {
                        shareParams.setUrl("http://www.mob.com");
                    }
                    shareParams.setText("测试分享的文本");
                    shareParams.setTitle("测试分享的标题");
                    shareParams.setTitleUrl("https://testinnews.nowrupiah.com:8443/h5/signin.html?s=dPiCPq");
                    shareParams.setImageUrl("http://y.gtimg.cn/music/photo_new/T002R300x300M000003bixR51mDMhB.jpg?max_age=2592000");
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                } else if (cities[which].equals("文字")) {
                    Platform.ShareParams sp = new Platform.ShareParams();
                    sp.setTitle("测试分享的标题");
//                    sp.setTitleUrl("https://mz5210.top"); // 标题的超链接
                    sp.setText("测试分享的文本");
//                    sp.setLinkedinDescription("测试分享的文本");
//                    sp.setImageUrl("http://y.gtimg.cn/music/photo_new/T002R300x300M000003bixR51mDMhB.jpg?max_age=2592000");
//                    sp.setSite("发布分享的网站名称");
//                    sp.setSiteUrl("发布分享网站的地址");
//                    sp.setUrl("https://mz5210.top");
                    sp.setComment("https://mz5210.top");
                    sp.setShareType(Platform.SHARE_TEXT);
                    Platform qzone = ShareSDK.getPlatform(pa);
                    qzone.setPlatformActionListener(platformActionListener);
                    qzone.share(sp);
                } else if (cities[which].equals("图文")) {
                    Platform.ShareParams sp = new Platform.ShareParams();
                    sp.setText("测试分享的文本");
                    sp.setImageUrl("https://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg");
                    sp.setShareType(Platform.SHARE_WEBPAGE);
                    Platform qzone = ShareSDK.getPlatform(pa);
                    qzone.setPlatformActionListener(platformActionListener);
                    qzone.share(sp);
                } else if (cities[which].equals("图片")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setImageUrl("https://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg");
                    if (!nowSharePlatform.equals(QQ.NAME)) {
                        shareParams.setText("测试文字");//微博可以分享图片的时候加上文字
                    }
                    platform.setPlatformActionListener(platformActionListener);
                    shareParams.setShareType(Platform.SHARE_IMAGE);
                    platform.share(shareParams);


                } else if (cities[which].equals("bitmap图片")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
                    shareParams.setImageData(bitmap);
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
                    if (SinaWeibo.NAME.equals(nowSharePlatform)) {
                        shareParams.setText("测试文字");
                    }
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
                    shareParams.setMusicUrl("https://uploader.shimo.im/f/1v3pE0SzlEO6VN3H.mp3");
                    shareParams.setShareType(Platform.SHARE_MUSIC);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                } else if (cities[which].equals("视频")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("分享的视频内容");
                    shareParams.setTitle("分享的视频标题");
                    shareParams.setUrl("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
                    shareParams.setImageUrl("http://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                    shareParams.setShareType(Platform.SHARE_VIDEO);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);

                } else if (cities[which].equals("分享微信小程序")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("分享的视频内容");
                    shareParams.setTitle("分享的视频标题");
                    shareParams.setUrl("http://www.mob.com");
                    shareParams.setWxUserName("gh_52568203455c");
                    shareParams.setWxPath("pages/index/index");
                    //0-正式，1-开发，2-体验MobCommons-2020.0902.2024.jar
                    shareParams.setWxMiniProgramType(0);
                    shareParams.setImageUrl("http://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                    shareParams.setShareType(Platform.SHARE_WXMINIPROGRAM);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);

                } else if (cities[which].equals("打开微信小程序")) {
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("分享的视频内容");
                    shareParams.setTitle("分享的视频标题");
                    shareParams.setUrl("http://www.mob.com");
                    shareParams.setWxUserName("gh_52568203455c");
                    shareParams.setWxPath("pages/index/index");
                    //0-正式，1-开发，2-体验
                    shareParams.setWxMiniProgramType(0);
                    shareParams.setImageUrl("http://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                    shareParams.setShareType(Platform.OPEN_WXMINIPROGRAM);
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

//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//
//                                    String s = BitmapHelper.downloadBitmap(ShareSdkActivity.this, "http://img.youbesun.com/12,01b53fceb825");
//                                    String s1 = BitmapHelper.downloadBitmap(ShareSdkActivity.this, "http://img.youbesun.com/13,016b76a5d752");
//                                    Log.d("ShareSdkActivity", s + "    " + s1);
//                                } catch (Throwable throwable) {
//                                    throwable.printStackTrace();
//                                }
//                            }
//                        }).start();

                } else if (cities[which].equals("授权")) {
                    Platform plat = ShareSDK.getPlatform(nowSharePlatform);
                    ShareSDK.setActivity(ShareSdkActivity.this);//抖音登录适配安卓9.0
                    plat.setPlatformActionListener(platformActionListener);
                    plat.removeAccount(true);
                    plat.authorize();
                } else if (cities[which].equals("登录")) {
                    Platform plat = ShareSDK.getPlatform(nowSharePlatform);
                    ShareSDK.setActivity(ShareSdkActivity.this);//抖音登录适配安卓9.0
                    plat.setPlatformActionListener(platformActionListener);
                    plat.removeAccount(true);
                    plat.showUser(null);
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
                    ShareSDK.setActivity(this);//抖音登录适配安卓9.0
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
                    ShareSDK.setActivity(this);//抖音登录适配安卓9.0
                    Uri douyinVideo = data.getData();
                    Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams shareParams = new Platform.ShareParams();
                    shareParams.setText("分享的文件内容");
                    shareParams.setTitle("分享的文标题");
                    shareParams.setImageUrl("http://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                    shareParams.setFilePath(UriUtil.convertUriToPath(this, douyinVideo));
                    shareParams.setShareType(Platform.SHARE_VIDEO);
                    shareParams.setActivity(this);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                    break;
                case Platform.SHARE_FILE:

                    try {
                        Uri uri1 = data.getData();
                        Platform platform1 = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams1 = new Platform.ShareParams();
                        shareParams1.setText("分享的文件内容");
                        shareParams1.setTitle("分享的文标题");
                        shareParams1.setImageUrl("http://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");

                        shareParams1.setFilePath(PathUtils.getPhotoPathFromContentUri(this, uri1));
                        shareParams1.setShareType(Platform.SHARE_FILE);
                        platform1.setPlatformActionListener(platformActionListener);
                        platform1.share(shareParams1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;

            }
        }
    }

    private void showNormalDialog(String msg) {

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle("结果");
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });

        // 显示
        normalDialog.show();
    }
}
