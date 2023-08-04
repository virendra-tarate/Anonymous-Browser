package com.anonymous.v.anonymousbrowser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements IUnityAdsInitializationListener {
    EditText UrlInput;
    WebView webView;
    ProgressBar progressBar;
    SwipeRefreshLayout mySwipe;
    private long pressTime;
    ImageView info;
    LinearLayout introLayout;
    ImageView quit;
    //unity
    private String unityGameID = "5263755";
    private Boolean testMode = false;
    private String adUnitId = "Rewarded_Android";


    private IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            UnityAds.show(MainActivity.this, adUnitId, new UnityAdsShowOptions(), showListener);

        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
        }
    };


    private IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowStart: " + placementId);
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowClick: " + placementId);
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.v("UnityAdsExample", "onUnityAdsShowComplete: " + placementId);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hide actionbar
        getSupportActionBar().hide();

        //id declarations
        UrlInput = findViewById(R.id.inp_url);
        webView = findViewById(R.id.web_page);
        progressBar = findViewById(R.id.progress_bar);
        info = (ImageView) findViewById(R.id.info_img);
        quit = findViewById(R.id.image);
        introLayout = findViewById(R.id.intro);

        //for webview
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setDisplayZoomControls(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setAllowFileAccessFromFileURLs(true);

        UnityAds.initialize(getApplicationContext(), unityGameID, testMode, this);
        //UnityAds.load(adUnitId, loadListener);




        //about activity
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passing intent to new activity
                UnityAds.load(adUnitId, loadListener);
                Intent info_intent = new Intent(MainActivity.this , info_activity.class);
                startActivity(info_intent);

            }
        });

        //quiting from app
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnityAds.load(adUnitId, loadListener);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            dataClear();
                            finishAffinity();
                        }
                    },5000);
                }

            }
        });




        //when progress is changed
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

        //when go button clicked
        UrlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {
                if(i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(UrlInput.getWindowToken(),0);
                    loadurl(UrlInput.getText().toString());
                    return true;
                }
                return false;
            }
        });

        //on swipe(reload)
        mySwipe = (SwipeRefreshLayout)this.findViewById(R.id.Swiperef);

        mySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mySwipe.setRefreshing(false);
            }
        });

    }

    //loadurl() method to load url from edittext
    void loadurl(String url){

        //hiding intro and displaying webview
        introLayout.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        if (url != null){
            boolean match = Patterns.WEB_URL.matcher(url).matches();
            if (match){
                webView.loadUrl(url);
            }
            else{
                webView.loadUrl("https://www.google.com/search?q="+url);
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Enter Something to Search",Toast.LENGTH_SHORT).show();
        }

    }

    //clear data when an Activity is Destroy
    //when app is suddenly closed


    //after on creat method

    @Override
    public void onBackPressed() {

        if(webView.canGoBack()){
            webView.goBack();
        }
        else{
            if(pressTime + 2000 > System.currentTimeMillis()){

                UnityAds.load(adUnitId, loadListener);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dataClear();
                        finish();
                    }
                },5000);


//                super.onBackPressed();

            }else{
                webView.setVisibility(View.GONE);
                introLayout.setVisibility(View.VISIBLE);
                UrlInput.setText("");
                Toast.makeText(getApplicationContext(), "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            }


            pressTime = System.currentTimeMillis();

        }

    }

    @Override
    public void onInitializationComplete() {
//        UnityAds.load(adUnitId, loadListener);
//        UnityAds.show(MainActivity.this, adUnitId, new UnityAdsShowOptions(), showListener);
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {

    }


    //new class to override method in webviewclient

    class MyWebViewClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            UrlInput.setText(webView.getUrl());
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }
    }

    public void dataClear(){

//        UnityAds.show(MainActivity.this, adUnitId, new UnityAdsShowOptions(), showListener);
//        UnityAds.load(adUnitId, loadListener);
        //clear DOM and HTML Database in Browser
        WebStorage.getInstance().deleteAllData();

        //clearing Cookies
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        //Clearing all data cache from user by Browser before user quite app
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();



        //tost for confirmation
        Toast.makeText(getApplicationContext(), "All Data Clear Successfully", Toast.LENGTH_SHORT).show();

    }

}

