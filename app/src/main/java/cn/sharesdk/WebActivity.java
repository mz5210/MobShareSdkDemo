package cn.sharesdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.sharesdk.demo.R;
import cn.sharesdk.js.ShareSDKUtils;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView= findViewById(R.id.webview);
        WebViewClient wvClient = new WebViewClient();
        webView.setWebViewClient(wvClient);
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        ShareSDKUtils.prepare(webView,wvClient);
        webView.loadUrl("file:///android_asset/Sample.html");
    }
}
