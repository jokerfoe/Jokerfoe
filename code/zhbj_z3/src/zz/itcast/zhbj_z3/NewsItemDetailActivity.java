package zz.itcast.zhbj_z3;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class NewsItemDetailActivity extends Activity {

	private String url;

	
	@ViewInject(R.id.news_detail_wv)
	private WebView webView;
	
	/**
	 * 加载的进度条
	 */
	@ViewInject(R.id.loading_view)
	private View loading_view;
	
	
	@ViewInject(R.id.btn_left)
	private Button btn_left;
	
	@ViewInject(R.id.imgbtn_left)
	private ImageButton imgbtn_left;
	
	@ViewInject(R.id.txt_title)
	private TextView txt_title;
	
	@ViewInject(R.id.imgbtn_text)
	private ImageButton imgbtn_text;
	
	@ViewInject(R.id.imgbtn_right)
	private ImageButton imgbtn_right;
	
	@ViewInject(R.id.btn_right)
	private ImageButton btn_right;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		url = getIntent().getStringExtra("web_url");
		
		if(TextUtils.isEmpty(url)){
			throw new RuntimeException("你又忘了给我传地址了。。。");
		}
		
		setContentView(R.layout.act_news_detail);
		
		ViewUtils.inject(this);
		
		initTitleBar();
		
		// webView 加载网页内容，加载完后成，隐藏进度条
		webView.loadUrl(url);
		
		webView.setWebViewClient(new WebViewClient(){
			@Override
			/**
			 * 加载新页面，完后时的回调
			 */
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				
				// 隐藏进度条
				loading_view.setVisibility(View.GONE);
			}
		});
		
		
	}
	
	/**
	 * 初始化标题 栏
	 */
	private void initTitleBar() {
		
		btn_left.setVisibility(View.GONE);
		imgbtn_text.setVisibility(View.GONE);
		
		// 菜单图片
		imgbtn_left.setVisibility(View.VISIBLE);
//		imgbtn_left.setBackgroundResource(R.drawable.img_menu);
		imgbtn_left.setImageResource(R.drawable.back);
		imgbtn_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 标题文字 
		txt_title.setVisibility(View.GONE);
		
		imgbtn_right.setVisibility(View.VISIBLE);
//		imgbtn_right.setBackgroundResource(R.drawable.icon_textsize);
		imgbtn_right.setImageResource(R.drawable.icon_textsize);
		
		imgbtn_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				webView.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
			}
		});
		
		btn_right.setVisibility(View.VISIBLE);
//		btn_right.setBackgroundResource(R.drawable.icon_share);
		btn_right.setImageResource(R.drawable.icon_share);
		btn_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(NewsItemDetailActivity.this, "分享功能，稍后推出", 0).show();
			}
		});
	}
	
	
}
