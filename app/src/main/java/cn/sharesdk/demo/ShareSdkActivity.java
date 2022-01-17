package cn.sharesdk.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.flexbox.FlexboxLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.mob.MobSDK;
import com.mob.OperationCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.WebActivity;
import cn.sharesdk.alipay.friends.Alipay;
import cn.sharesdk.alipay.moments.AlipayMoments;
import cn.sharesdk.dingding.friends.Dingding;
import cn.sharesdk.douyin.Douyin;
import cn.sharesdk.facebook.Facebook;
//import cn.sharesdk.facebookmessenger.FacebookMessenger;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.loopshare.LoopSharePasswordListener;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.instagram.Instagram;
import cn.sharesdk.kakao.talk.KakaoTalk;
import cn.sharesdk.line.Line;
import cn.sharesdk.linkedin.LinkedIn;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.telegram.Telegram;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.vkontakte.VKontakte;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.wework.Wework;
import cn.sharesdk.whatsapp.WhatsApp;

public class ShareSdkActivity extends Activity implements View.OnClickListener {
    String nowSharePlatform;

    String imagePath;
    private ClipboardManager cm;
    private ClipData mClipData;
    private FlexboxLayout flexLayout;
    private final int SHARE_LOCAL_SINGLE_IMAGE = 1001;//本地单图
    private final int SHARE_LOCAL_SINGLE_IMAGE_AND_TEXT = 1005;//本地单图+文字
    private final int SHARE_LOCAL_VIDEO = 1002;
    private final int SHARE_LOCAL_FILE = 1004;
    private final int SHARE_FILE_PROVIDER_IMAGE = 1003;
    private final int SHARE_LOCAL_IMAGES_AND_TEXT = 1006;//本地多图+文字


    String imageUrl = "https://y.gtimg.cn/music/photo_new/T002R300x300M000003bixR51mDMhB.jpg?max_age=2592000";
    String imageUrl2 = "https://shp.qpic.cn/ttkg/0/7dc158efc98089bb047e47ef83f9fafc5981aead/320";


    PlatformActionListener platformActionListener = new PlatformActionListener() {
        public void onError(final Platform arg0, final int arg1, final Throwable arg2) {
//            arg1 = 8 有用户信息登录 ； arg1 = 9 分享 ; arg1 = 1 无用户信息登录
            //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
            Log.d("ShareSdkActivity", arg0.getName() + "  " + arg1 + "失败 " + arg2.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showNormalDialog(arg0.getName() + "  失败\n" + arg2.toString());
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
        TextView sdk_version = findViewById(R.id.sdk_version);
        flexLayout = findViewById(R.id.flex_layout);
        sdk_version.setText("ShareSdkVersion:" + ShareSDK.SDK_VERSION_NAME);
        submitPolicyGrantResult();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

    }

    private class PlatformTask extends AsyncTask<Void, Void, Platform[]> {

        @Override
        protected Platform[] doInBackground(Void... params) {
            return ShareSDK.getPlatformList();
        }

        @Override
        protected void onPostExecute(Platform[] platformList) {
            if (platformList != null) {
//                Toast.makeText(ShareSdkActivity.this, "flexLayout.getChildCount():" + flexLayout.getChildCount(), Toast.LENGTH_SHORT).show();
                if (flexLayout.getChildCount() > platformList.length) {
                    flexLayout.removeViews(7, platformList.length);
                }
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5, 5, 5, 5);//4个参数按顺序分别是左上右下
                for (Platform platform : platformList) {
                    Log.d("ShareSdkActivity1", "platform:" + platform.getName());
                    Button button = new Button(ShareSdkActivity.this);
                    button.setText(platform.getName());
                    button.setLayoutParams(layoutParams);
                    button.setTextAppearance(ShareSdkActivity.this, R.style.ButtonStyle);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showChoise(platform.getName());
                        }
                    });
                    flexLayout.addView(button);
                }
            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PlatformTask.this.execute();
                    }
                }, 300);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yinsixieyi:
                submitPolicyGrantResult();
                break;
            case R.id.share:
                OnekeyShare oks = new OnekeyShare();
                oks.setTitle("标题");
                oks.setTitleUrl("https://mz5210.top");
//                oks.addHiddenPlatform(QQ.NAME);
//                oks.addHiddenPlatform(Wechat.NAME);
                oks.setText("我是分享文本");
                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform, Platform.ShareParams shareParams) {
                        shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    }
                });
                oks.setImageUrl(imageUrl);
                oks.setUrl("https://mz5210.top");
                oks.setCallback(platformActionListener);
                oks.show(MobSDK.getContext());
                break;
            case R.id.yijinashareImg:
                OnekeyShare oks1 = new OnekeyShare();
                oks1.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform, Platform.ShareParams shareParams) {
                        shareParams.setShareType(Platform.SHARE_IMAGE);
                    }
                });
                oks1.setImageUrl(imageUrl);
                oks1.setCallback(platformActionListener);
                oks1.show(MobSDK.getContext());
                break;
            case R.id.yijinasharebendidantu:
                OnekeyShare oks2 = new OnekeyShare();
                oks2.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform, Platform.ShareParams shareParams) {
                        shareParams.setShareType(Platform.SHARE_IMAGE);
                    }
                });
                oks2.setImagePath(imageUrl);
                oks2.setCallback(platformActionListener);
                oks2.show(MobSDK.getContext());
                break;

            case R.id.share_js:
                startActivity(new Intent(this, WebActivity.class));
                break;
        }
    }

    private void submitPolicyGrantResult() {
        MobSDK.submitPolicyGrantResult(true, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void aVoid) {
                showNormalDialog("隐私协议成功");
                new PlatformTask().execute();
            }

            @Override
            public void onFailure(Throwable throwable) {
                showNormalDialog("隐私协议失败 throwable " + throwable.getMessage());
            }
        });
    }

    private void parasQuickPassWord() {
        ShareSDK.readPassWord(false, new LoopSharePasswordListener() {
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
            cities = new String[]{"安装否", "本地视频", "本地单图", "授权", "登录"};
        } else if (pa.equals(Instagram.NAME)) {
            cities = new String[]{"安装否", "URL单图", "视频", "本地单图", "本地视频", "授权", "登录"};
        } else if (pa.equals(QQ.NAME)) {
            cities = new String[]{"安装否", "音乐", "本地单图", "URL单图", "链接", "url多图", "授权", "登录"};
        } else if (pa.equals(Wework.NAME)) {
            cities = new String[]{"安装否", "文字", "文件", "URL单图", "本地视频", "链接", "授权", "登录"};
        } else if (pa.equals(QZone.NAME)) {
            cities = new String[]{"安装否", "文字", "URL单图", "链接", "视频"};
        } else if (pa.equals(Twitter.NAME)) {
            cities = new String[]{"安装否", "文字", "本地单图", "URL单图", "url单图+文字", "链接", "视频", "授权", "登录"};
        } else if (pa.equals(Wechat.NAME)) {
            cities = new String[]{"安装否", "本地单图", "文字", "文件", "bitmap图片", "URL单图", "FileProvider图片", "链接", "音乐", "视频", "表情", "分享微信小程序", "打开微信小程序", "授权", "登录"};
        } else if (pa.equals(WechatFavorite.NAME)) {
            cities = new String[]{"安装否", "文字", "URL单图", "文件", "链接", "音乐", "视频"};
        } else if (pa.equals(WechatMoments.NAME)) {
            cities = new String[]{"安装否", "文字", "URL单图", "链接", "音乐", "视频"};
        } else if (pa.equals(Alipay.NAME)) {
            cities = new String[]{"安装否", "文字", "URL单图", "本地单图", "bitmap图片", "链接", "授权", "登录"};
        } else if (pa.equals(KakaoTalk.NAME)) {
            cities = new String[]{"安装否", "文字", "本地单图", "URL单图", "链接", "授权", "登录"};
        } else if (pa.equals(GooglePlus.NAME)) {
            cities = new String[]{"安装否", "授权", "登录"};
        } else if (pa.equals(AlipayMoments.NAME)) {
            cities = new String[]{"安装否", "链接"};
        } else if (pa.equals(Facebook.NAME)) {
            cities = new String[]{"安装否", "链接", "链接带文字", "URL单图", "本地单图", "bitmap图片", "授权", "登录"};
        } else if (pa.equals(Dingding.NAME)) {
            cities = new String[]{"安装否", "文字", "URL单图", "链接", "授权", "登录"};
        } else if (pa.equals(Line.NAME)) {
            cities = new String[]{"安装否", "文字", "URL单图", "授权", "登录"};
        } else if (pa.equals(SinaWeibo.NAME)) {
            cities = new String[]{"安装否", "文字", "url单图+文字", "url多图+文字", "本地单图+文字", "本地多图+文字", "链接", "url多图", "授权", "登录"};
        } else if (pa.equals(WhatsApp.NAME)) {
            cities = new String[]{"安装否", "本地单图", "文字", "URL单图", "URL图片+文字", "本地单图+文字", "url单图+文字", "授权", "登录"};
//        } else if (pa.equals(FacebookMessenger.NAME)) {
//            cities = new String[]{"安装否", "URL单图", "本地单图", "链接"};
        } else if (pa.equals(Telegram.NAME)) {
            cities = new String[]{"安装否", "URL单图", "本地单图", "文字"};
        } else if (pa.equals(LinkedIn.NAME)) {
            cities = new String[]{"安装否", "URL单图", "链接", "文字"};
        } else if (pa.equals(VKontakte.NAME)) {
            cities = new String[]{"安装否", "链接", "文字"};
        } else {
            cities = new String[]{"安装否", "链接"};
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
                    } else if (!nowSharePlatform.equals(QQ.NAME)) {
//                        shareParams.setUrl("https://www.mob.com");
                        shareParams.setUrl("http://bmob.files.mz5210.top/2020/05/25/533c76b1408716488024712639360deb.html");
                    }

                    shareParams.setText("测试分享的文本");
                    shareParams.setTitle("测试分享的标题");


                    shareParams.setTitleUrl("https://www.mob.com");
                    shareParams.setImageUrl(imageUrl);
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                } else if (cities[which].equals("文字")) {
                    Platform qzone = ShareSDK.getPlatform(pa);

                    Platform.ShareParams sp = new Platform.ShareParams();
                    sp.setTitle("测试分享的标题");
//                    sp.setTitleUrl("https://mz5210.top"); // 标题的超链接
                    sp.setText("测试分享的文本");
//                    sp.setLinkedinDescription("测试分享的文本");
//                    sp.setImageUrl(imageUrl);
//                    sp.setSite("发布分享的网站名称");
//                    sp.setSiteUrl("发布分享网站的地址");
//                    sp.setUrl("https://mz5210.top");
                    sp.setComment("https://mz5210.top");
                    if (KakaoTalk.NAME.equals(pa)) {
                        sp.setShareType(Platform.KAKAO_TEXT_TEMPLATE);
                    } else {
                        sp.setShareType(Platform.SHARE_TEXT);
                    }
                    qzone.setPlatformActionListener(platformActionListener);
                    qzone.share(sp);
                } else {
                    if (cities[which].equals("url单图+文字")) {
                        Platform.ShareParams sp = new Platform.ShareParams();
                        sp.setText("测试分享的文本");
                        sp.setImageUrl(imageUrl);
                        sp.setShareType(Platform.SHARE_IMAGE);
                        Platform qzone = ShareSDK.getPlatform(pa);
                        qzone.setPlatformActionListener(platformActionListener);
                        qzone.share(sp);
                    } else if (cities[which].equals("url多图+文字")) {
                        Platform.ShareParams sp = new Platform.ShareParams();
                        sp.setText("测试分享的文本");
                        String[] imagePathArray = {
                                imageUrl,
                                imageUrl2
                        };
                        sp.setImageArray(imagePathArray);
                        sp.setShareType(Platform.SHARE_IMAGE);
                        Platform qzone = ShareSDK.getPlatform(pa);
                        qzone.setPlatformActionListener(platformActionListener);
                        qzone.share(sp);
                    } else if (cities[which].equals("本地多图+文字")) {
                        //                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        //                    intent.setType("image/*");
                        //                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        ////                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        //                    startActivityForResult(intent, SHARE_LOCAL_IMAGES_AND_TEXT);

                        PictureSelector.create(ShareSdkActivity.this)
                                .openGallery(PictureMimeType.ofImage())
                                .imageEngine(GlideEngine.createGlideEngine())
                                .selectionMode(PictureConfig.MULTIPLE)
                                .compress(true)//是否压缩
                                .compressFocusAlpha(true)//压缩后是否保持图片的透明通道
                                .minimumCompressSize(1500)// 小于多少kb的图片不压缩
                                .compressQuality(80)//图片压缩后输出质量
                                .maxSelectNum(3)//最大选择数量,默认9张
                                .forResult(new OnResultCallbackListener<LocalMedia>() {
                                    @Override
                                    public void onResult(List<LocalMedia> result) {
                                        String[] imagePathArray = new String[result.size()];
                                        for (int i = 0; i < result.size(); i++) {
                                            imagePathArray[i] = result.get(i).getRealPath();
                                        }
                                        // 结果回调
                                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                                        Platform.ShareParams shareParams = new Platform.ShareParams();

                                        shareParams.setImageArray(imagePathArray);
                                        shareParams.setText("测试文字");

                                        shareParams.setShareType(Platform.SHARE_IMAGE);
                                        //                                    shareParams.setActivity(this);
                                        platform.setPlatformActionListener(platformActionListener);
                                        platform.share(shareParams);
                                    }

                                    @Override
                                    public void onCancel() {
                                        // 取消
                                    }
                                });

                    } else if (cities[which].equals("链接带文字")) {
                        Platform.ShareParams sp = new Platform.ShareParams();
                        sp.setQuote("测试分享的文本");
                        sp.setUrl("https://developers.facebook.com");
                        sp.setShareType(Platform.SHARE_WEBPAGE);
                        Platform qzone = ShareSDK.getPlatform(pa);
                        qzone.setPlatformActionListener(platformActionListener);
                        qzone.share(sp);
                    } else if (cities[which].equals("URL单图")) {
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams = new Platform.ShareParams();
                        shareParams.setImageUrl(imageUrl);
                        platform.setPlatformActionListener(platformActionListener);
                        shareParams.setShareType(Platform.SHARE_IMAGE);
                        platform.share(shareParams);


                    } else if (cities[which].equals("URL图片+文字")) {
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams = new Platform.ShareParams();

                        if (nowSharePlatform.equals(Facebook.NAME)) {
                            //                        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
                            //                        shareParams.setImageData(bitmap);
                            shareParams.setHashtag("测试文字");//微博可以分享图片的时候加上文字
                        } else {
                            shareParams.setText("测试文字");
                        }
                        shareParams.setImageUrl(imageUrl);
                        platform.setPlatformActionListener(platformActionListener);
                        shareParams.setShareType(Platform.SHARE_IMAGE);
                        platform.share(shareParams);


                    } else if (cities[which].equals("FileProvider图片")) {

                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivityForResult(intent, SHARE_FILE_PROVIDER_IMAGE);

                    } else if (cities[which].equals("bitmap图片")) {
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams = new Platform.ShareParams();
                        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
                        shareParams.setImageData(bitmap);
                        shareParams.setText("测试文字");//微博可以分享图片的时候加上文字
                        platform.setPlatformActionListener(platformActionListener);
                        shareParams.setShareType(Platform.SHARE_IMAGE);
                        platform.share(shareParams);


                    } else if (cities[which].equals("url多图")) {
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("BypassApproval", "false");
                        ShareSDK.setPlatformDevInfo(Wechat.NAME, hashMap);
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams = new Platform.ShareParams();
                        //                    List a = new ArrayList();
                        //                    a.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000003bixR51mDMhB.jpg?max_age=2592000");
                        //                    a.add("https://y.gtimg.cn/music/photo_new/T002R300x300M0000004dQi53ybc8o.jpg?max_age=2592000");
                        //                    a.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000003yQidc3s7P65.jpg?max_age=2592000");
                        //                    shareParams.setImageUrlList(a);
                        shareParams.setImageArray(new String[]{"https://y.gtimg.cn/music/photo_new/T002R300x300M0000004dQi53ybc8o.jpg?max_age=2592000", "https://y.gtimg.cn/music/photo_new/T002R300x300M000003yQidc3s7P65.jpg?max_age=2592000"});
                        platform.setPlatformActionListener(platformActionListener);
                        //                    shareParams.setShareType(Platform.SHARE_IMAGE);
                        platform.share(shareParams);
                    } else if (cities[which].equals("音乐")) {
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);

                        Platform.ShareParams shareParams = new Platform.ShareParams();
                        shareParams.setText("这是一首歌");
                        shareParams.setTitle("歌名");
                        //                    shareParams.setImagePath(ResourcesManager.getInstace(MobSDK.getContext()).getImagePath());
                        shareParams.setImageUrl("https://y.gtimg.cn/music/photo_new/T002R500x500M000003yQidc3s7P65.jpg?max_age=2592000");
                        shareParams.setUrl("https://node.kg.qq.com/play?s=--0ay2-IQH5QA-gg&g_f=personal");
                        shareParams.setTitleUrl("https://node.kg.qq.com/play?s=--0ay2-IQH5QA-gg&g_f=personal");
                        shareParams.setMusicUrl("https://uploader.shimo.im/f/1v3pE0SzlEO6VN3H.mp3");
                        shareParams.setShareType(Platform.SHARE_MUSIC);
                        platform.setPlatformActionListener(platformActionListener);
                        platform.share(shareParams);
                    } else if (cities[which].equals("视频")) {
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams = new Platform.ShareParams();
                        shareParams.setText("分享的视频内容");
                        shareParams.setTitle("分享的视频标题");
                        if (QZone.NAME.equals(nowSharePlatform)) {
                            shareParams.setTitleUrl("https://node.kg.qq.com/play?s=--0ay2-IQH5QA-gg&g_f=personal");
                        }

                        shareParams.setUrl("https://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
                        shareParams.setImageUrl("https://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                        shareParams.setShareType(Platform.SHARE_VIDEO);
                        platform.setPlatformActionListener(platformActionListener);
                        platform.share(shareParams);

                    } else if (cities[which].equals("分享微信小程序")) {
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams = new Platform.ShareParams();
                        shareParams.setText("外卖大红包");
                        shareParams.setTitle("Test Share");
                        shareParams.setUrl("https://www.mob.com");
                        shareParams.setWxUserName("gh_e4db9ed61fd4");
                        shareParams.setWxPath("pages/index/index");
                        //0-正式，1-开发，2-体验
                        shareParams.setWxMiniProgramType(0);
                        shareParams.setImageUrl("https://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                        shareParams.setShareType(Platform.SHARE_WXMINIPROGRAM);
                        platform.setPlatformActionListener(platformActionListener);
                        platform.share(shareParams);

                    } else if (cities[which].equals("打开微信小程序")) {
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams = new Platform.ShareParams();
                        shareParams.setText("分享的视频内容");
                        shareParams.setTitle("分享的视频标题");
                        shareParams.setUrl("https://www.mob.com");
                        shareParams.setWxUserName("gh_52568203455c");
                        shareParams.setWxPath("pages/index/index");
                        //0-正式，1-开发，2-体验
                        shareParams.setWxMiniProgramType(0);
                        shareParams.setImageUrl("https://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                        shareParams.setShareType(Platform.OPEN_WXMINIPROGRAM);
                        platform.setPlatformActionListener(platformActionListener);
                        platform.share(shareParams);

                    } else if (cities[which].equals("表情")) {
                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams = new Platform.ShareParams();
                        shareParams.setText("分享的视频内容");
                        shareParams.setTitle("分享的视频标题");
                        shareParams.setImageUrl("https://download.sdk.mob.com/2020/11/12/19/1605179619971317.32.gif");

                        shareParams.setShareType(Platform.SHARE_EMOJI);
                        platform.setPlatformActionListener(platformActionListener);
                        platform.share(shareParams);
                    } else if (cities[which].equals("文件")) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivityForResult(intent, SHARE_LOCAL_FILE);


                    } else if (cities[which].equals("本地视频")) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("video/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivityForResult(intent, SHARE_LOCAL_VIDEO);
                    } else if (cities[which].equals("本地单图")) {
                        //                    Intent intent = new Intent(Intent.ACTION_PICK);
                        //                    intent.setType("image/*");
                        //                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        //                    startActivityForResult(intent, SHARE_LOCAL_SINGLE_IMAGE);
                        PictureSelector.create(ShareSdkActivity.this)
                                .openGallery(PictureMimeType.ofImage())
                                .imageEngine(GlideEngine.createGlideEngine())
                                .compress(true)//是否压缩
                                .compressFocusAlpha(true)//压缩后是否保持图片的透明通道
                                .minimumCompressSize(1500)// 小于多少kb的图片不压缩
                                .compressQuality(80)//图片压缩后输出质量
                                .selectionMode(PictureConfig.SINGLE)
                                .forResult(new OnResultCallbackListener<LocalMedia>() {
                                    @Override
                                    public void onResult(List<LocalMedia> result) {
                                        // 结果回调
                                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                                        Platform.ShareParams shareParams = new Platform.ShareParams();
                                        ShareSDK.setActivity(ShareSdkActivity.this);//抖音登录适配安卓9.0
                                        shareParams.setImagePath(result.get(0).getRealPath());
                                        shareParams.setShareType(Platform.SHARE_IMAGE);
                                        platform.setPlatformActionListener(platformActionListener);
                                        platform.share(shareParams);
                                    }

                                    @Override
                                    public void onCancel() {
                                        // 取消
                                    }
                                });
                    } else if (cities[which].equals("本地单图+文字")) {
                        //                    Intent intent = new Intent(Intent.ACTION_PICK);
                        //                    intent.setType("image/*");
                        //                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        //                    startActivityForResult(intent, SHARE_LOCAL_SINGLE_IMAGE_AND_TEXT);
                        PictureSelector.create(ShareSdkActivity.this)
                                .openGallery(PictureMimeType.ofImage())
                                .imageEngine(GlideEngine.createGlideEngine())
                                .selectionMode(PictureConfig.SINGLE)
                                .compress(true)//是否压缩
                                .compressFocusAlpha(true)//压缩后是否保持图片的透明通道
                                .minimumCompressSize(1500)// 小于多少kb的图片不压缩
                                .compressQuality(80)//图片压缩后输出质量
                                .forResult(new OnResultCallbackListener<LocalMedia>() {
                                    @Override
                                    public void onResult(List<LocalMedia> result) {
                                        //                                    String[] imagePathArray = new String[result.size()];
                                        //                                    for (int i = 0; i < result.size(); i++) {
                                        //                                        imagePathArray[i] = result.get(i).getRealPath();
                                        //                                    }
                                        // 结果回调
                                        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
                                        Platform.ShareParams shareParams = new Platform.ShareParams();

                                        shareParams.setImagePath(result.get(0).getRealPath());
                                        shareParams.setText("测试文字");

                                        shareParams.setShareType(Platform.SHARE_IMAGE);
                                        //                                    shareParams.setActivity(this);
                                        platform.setPlatformActionListener(platformActionListener);
                                        platform.share(shareParams);
                                    }

                                    @Override
                                    public void onCancel() {
                                        // 取消
                                    }
                                });
                    } else if (cities[which].equals("授权")) {
                        Platform plat = ShareSDK.getPlatform(nowSharePlatform);
                        ShareSDK.setActivity(ShareSdkActivity.this);//抖音登录适配安卓9.0
                        plat.setPlatformActionListener(platformActionListener);
                        //                    plat.removeAccount(true);

                        plat.authorize();
                    } else if (cities[which].equals("安装否")) {
                        Platform plat = ShareSDK.getPlatform(nowSharePlatform);
                        Toast.makeText(ShareSdkActivity.this, plat.getName() + "    " + plat.isClientValid(), Toast.LENGTH_SHORT).show();
                    } else if (cities[which].equals("登录")) {
                        Platform plat = ShareSDK.getPlatform(nowSharePlatform);
                        ShareSDK.setActivity(ShareSdkActivity.this);//抖音登录适配安卓9.0
                        plat.setPlatformActionListener(platformActionListener);
                        //                    plat.removeAccount(true);
                        plat.showUser(null);
                    }
                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Platform platform = ShareSDK.getPlatform(nowSharePlatform);
        Platform.ShareParams shareParams = new Platform.ShareParams();

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SHARE_LOCAL_IMAGES_AND_TEXT://本地多图+文字
                    Uri uri1 = data.getClipData().getItemAt(0).getUri();
//                    Log.d("ShareSdkActivity", "data.getData():" + uri1);
                    imagePath = UriUtil.convertUriToPath(this, uri1);
//                    Log.d("ShareSdkActivity1", imagePath);
                    String[] imagePathArray = {UriUtil.convertUriToPath(this, uri1)};
                    shareParams.setImageArray(imagePathArray);
                    shareParams.setText("测试文字");

                    shareParams.setShareType(Platform.SHARE_IMAGE);
                    shareParams.setActivity(this);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                    break;
                case SHARE_LOCAL_SINGLE_IMAGE://本地单图
                    ShareSDK.setActivity(this);//抖音登录适配安卓9.0
                    Uri uri = data.getData();
                    imagePath = UriUtil.convertUriToPath(this, uri);
                    shareParams.setImagePath(imagePath);
                    shareParams.setShareType(Platform.SHARE_IMAGE);
                    shareParams.setActivity(this);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                    break;
                case SHARE_LOCAL_SINGLE_IMAGE_AND_TEXT://本地单图+文字
                    ShareSDK.setActivity(this);//抖音登录适配安卓9.0
                    Uri uri2 = data.getData();
                    imagePath = UriUtil.convertUriToPath(this, uri2);
                    Platform douyin2 = ShareSDK.getPlatform(nowSharePlatform);
                    Platform.ShareParams sp2 = new Platform.ShareParams();
                    sp2.setImageFileProviderPath(imagePath);
                    sp2.setText("测试文字");


//                    File file = new File(imagePath);
//                    String contentPath = getFileUri(this, file);
//                    sp.setImageFileProviderPath(contentPath);

                    sp2.setShareType(Platform.SHARE_IMAGE);
                    sp2.setActivity(this);
                    douyin2.setPlatformActionListener(platformActionListener);
                    douyin2.share(sp2);
                    break;
                case SHARE_LOCAL_VIDEO:
                    ShareSDK.setActivity(this);//抖音登录适配安卓9.0
                    Uri videoUri = data.getData();

                    shareParams.setText("分享的文件内容");
                    shareParams.setTitle("分享的文标题");
                    shareParams.setImageUrl("https://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                    String videoPath = UriUtil.convertUriToPath(this, videoUri);
                    Log.d("ShareSdkActivity1", videoPath);
                    shareParams.setFilePath(videoPath);
                    shareParams.setShareType(Platform.SHARE_VIDEO);
                    shareParams.setActivity(this);
                    platform.setPlatformActionListener(platformActionListener);
                    platform.share(shareParams);
                    break;
                case SHARE_LOCAL_FILE:

                    try {
                        Uri fileUri = data.getData();
                        Platform platform1 = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams1 = new Platform.ShareParams();
//                        shareParams1.setText("分享的文件内容");
//                        shareParams1.setTitle("分享的文标题");
//                        shareParams1.setImageUrl("https://shp.qpic.cn/ttkg/0/c4baf9eec98089bb047e47ef83f9fafc5981aaac/640?j=PiajxSqBRaEIf0bHhsJQ0QVoFSjos8ibuwib8icMibSGWGru7aj84uAW826V84GUk58dtVGrIjzPEcNIuZJAHJGfHTK6ibpQrN3vf0OGCejp7Xh78UWoLibQragicmI5Xh5UxorhLNlVoBZtMUM");
                        String filePath = UriUtil.convertUriToPath(this, fileUri);
                        Log.d("ShareSdkActivity1", filePath);
                        shareParams1.setFilePath(filePath);
//                        shareParams1.setImageFileProviderPath(filePath);
                        shareParams1.setShareType(Platform.SHARE_FILE);
                        platform1.setPlatformActionListener(platformActionListener);
                        platform1.share(shareParams1);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }


                    break;


                case SHARE_FILE_PROVIDER_IMAGE:
                    try {
                        Uri fileUri = data.getData();
                        Platform platform1 = ShareSDK.getPlatform(nowSharePlatform);
                        Platform.ShareParams shareParams1 = new Platform.ShareParams();
                        String filePath = UriUtil.convertUriToPath(this, fileUri);
                        Log.d("ShareSdkActivity1", filePath);
                        shareParams1.setImageFileProviderPath(filePath);
                        shareParams1.setShareType(Platform.SHARE_IMAGE);
                        platform1.setPlatformActionListener(platformActionListener);
                        platform1.share(shareParams1);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;

            }
        }
    }


    public String getFileUri(Context context, File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        Uri contentUri = FileProvider.getUriForFile(context,
                "cn.sharesdk.demo.fileProvider",  // 要与`AndroidManifest.xml`里配置的`authorities`一致，假设你的应用包名为com.example.app
                file);

        // 授权给微信访问路径
        context.grantUriPermission("com.tencent.mm",  // 这里填微信包名
                contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return contentUri.toString();   // contentUri.toString() 即是以"content://"开头的用于共享的路径
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实路径
        //Android系统提供了MediaScanner，MediaProvider，MediaStore等接口，并且提供了一套数据库
        //表格，通过Content Provider的方式提供给用户。当手机开机或者有SD卡插拔等事件发生时，系统
        //将会自动扫描SD卡和手机内存上的媒体文件，如audio，video，图片等，将相应的信息放到定义好
        //的数据库表格中。在这个程序中，我们不需要关心如何去扫描手机中的文件，只要了解如何查询和使
        //用这些信息就可以了。MediaStore中定义了一系列的数据表格，通过ContentResolver提供的查询
        //接口，我们可以得到各种需要的信息。
        //EXTERNAL_CONTENT_URI 为查询外置内存卡的，INTERNAL_CONTENT_URI为内置内存卡。
        //MediaStore.Audio获取音频信息的类
        //MediaStore.Images获取图片信息
        //MediaStore.Video获取视频信息
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
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

    /**
     * 设置返回键不关闭应用,回到桌面
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //启动一个意图,回到桌面

            Intent backHome = new Intent(Intent.ACTION_MAIN);
            backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            backHome.addCategory(Intent.CATEGORY_HOME);
            startActivity(backHome);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
