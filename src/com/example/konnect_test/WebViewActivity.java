package com.example.konnect_test;

import android.os.Bundle;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.Activity; 
import android.content.Intent;
import android.graphics.Bitmap;

public class WebViewActivity extends Activity {
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   // Allow the title bar to show loading progress.
	    requestWindowFeature(Window.FEATURE_PROGRESS);

	    WebView webview = new WebView(this);
	    setContentView(webview);

	   // Load the page
	    Intent intent = getIntent();
	    if (intent.getData() != null) {
	      webview.loadUrl(intent.getDataString());
	    }
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.setWebChromeClient(new WebChromeClient() {
		     // Show loading progress in activity's title bar.
		      @Override
		      public void onProgressChanged(WebView view, int progress) {
		        setProgress(progress * 100);
		      }
		    });
		  webview.setWebViewClient(new WebViewClient() {
		     // When start to load page, show url in activity's title bar
		      @Override
		      public void onPageStarted(WebView view, String url,
		         Bitmap favicon) {
		        setTitle(url);
		      }
		    });
		  webview.setWebViewClient(new WebViewClient() {
		     

		      @Override
		      public void onPageFinished(WebView view, String url) {
		        CookieSyncManager.getInstance().sync();
		        // Get the cookie from cookie jar.
		        String cookie = CookieManager.getInstance().getCookie(url);
		        if (cookie == null) {
		          return;
		        }
		        // Cookie is a string like NAME=VALUE [; NAME=VALUE]
		        String[] pairs = cookie.split(";");
		        for (int i = 0; i < pairs.length; ++i) {
		          String[] parts = pairs[i].split("=", 2);
		          // If token is found, return it to the calling activity.
		          if (parts.length == 2 &&
		             parts[0].equalsIgnoreCase("oauth_token")) {
		            Intent result = new Intent();
		            result.putExtra("token", parts[1]);
		            setResult(RESULT_OK, result);
		            finish();
		          }
		        }
		      }
		    });
	    
}
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	      case 0:
	        if (resultCode != RESULT_OK || data == null) {
	          return;
	        }
	        // Get the token.
	        String token = data.getStringExtra("token");
	        if (token != null) {
	          /* Use the token to access data */
	        }
	        return;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	  }
	@Override
	  protected void onPause() {
	    super.onPause();
	    CookieSyncManager.getInstance().stopSync();
	  }

	  @Override
	  protected void onResume() {
	    super.onResume();
	    CookieSyncManager.getInstance().startSync();
	  }

	
}

