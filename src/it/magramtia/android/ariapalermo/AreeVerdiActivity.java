/**
 * 
 */
package it.magramtia.android.ariapalermo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author mattiadigan
 *
 */
public class AreeVerdiActivity extends ActionBarActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aree_verdi);

		WebView mainWebView = (WebView) findViewById(R.id.webView1);

		WebSettings webSettings = mainWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		mainWebView.setWebViewClient(new MyCustomWebViewClient());
		mainWebView.loadUrl("http://ariapalermo.altervista.org/areeverdi.php");
		mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

	private class MyCustomWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
