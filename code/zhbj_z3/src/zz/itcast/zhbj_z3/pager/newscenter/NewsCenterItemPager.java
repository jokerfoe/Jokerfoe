package zz.itcast.zhbj_z3.pager.newscenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import zz.itcast.zhbj_z3.NewsItemDetailActivity;
import zz.itcast.zhbj_z3.R;
import zz.itcast.zhbj_z3.adapter.CZAdapter;
import zz.itcast.zhbj_z3.bean.NewsCenterBean.NewsChildren;
import zz.itcast.zhbj_z3.bean.NewsCenterItemBean;
import zz.itcast.zhbj_z3.bean.NewsCenterItemBean.News;
import zz.itcast.zhbj_z3.bean.NewsCenterItemBean.Topnews;
import zz.itcast.zhbj_z3.pager.BasePager;
import zz.itcast.zhbj_z3.utils.ItcastUrl;
import zz.itcast.zhbj_z3.utils.JsonCacheUtils;
import zz.itcast.zhbj_z3.view.DownFreshListView;
import zz.itcast.zhbj_z3.view.DownFreshListView.IonRefreshFinishListener;
import zz.itcast.zhbj_z3.view.RollViewPager;
import zz.itcast.zhbj_z3.view.RollViewPager.IOnItemClickListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

public class NewsCenterItemPager extends BasePager {

	
	
	protected static final String SP_NAME = "read_news";

	protected static final String READ_NEWS_KEY = "read_news_key";

	private NewsChildren newsChildren;
	
	private DownFreshListView listview;

	@ViewInject(R.id.top_news_viewpager)
	private LinearLayout top_news_viewpager;
	
	@ViewInject(R.id.top_news_title)
	private TextView top_news_title;
	
	@ViewInject(R.id.dots_ll)
	private LinearLayout dots_ll;
	
	public NewsCenterItemPager(Context ctx, NewsChildren newsChildren) {
		super(ctx);
		this.newsChildren = newsChildren;
	}

	private BitmapUtils bitmapUtils;
	
	private SharedPreferences sp;
	@Override
	public View initView() {
		bitmapUtils = new BitmapUtils(ctx);
		listview = (DownFreshListView) View.inflate(ctx, R.layout.list_view, null);
		
		sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		
		// 包含异航效果的ViewPager
		View view = View.inflate(ctx,R.layout.layout_roll_view, null);
		
		ViewUtils.inject(this, view);
		
//		listview.addHeaderView(view); // 轮播图，只是头的一部分
		
		listview.addCustomHeader(view);
		
		// 如果listView没有内容，那么他的headView也不会显示出来。添加一个测试的adapter
//		listview.setAdapter(new ArrayAdapter<String>(ctx, 
//				android.R.layout.simple_list_item_1, // 一个布局的ID，要求布局中有一个TextView 
//				android.R.id.text1,  // 上个参数的布局中，TextView 的ID值
//				new String[]{"aa","bbbb","cccc","dddd","eee","ffff"}));// 要显示的内容数组
		
		listview.setOnRefrshFinishListener(new IonRefreshFinishListener() {
			
			@Override
			/**
			 * 上拉加载更多时的回调 
			 */
			public void onLoadingMore() {
				
				String more = itemBean.data.more;
				if(TextUtils.isEmpty(more)){
					Toast.makeText(ctx, "没有更多内容了。。。", 0).show();
					listview.freshFinish();
					return ;
				}else{
					Toast.makeText(ctx, "加载更多内容。。。", 0).show();
					requestDataFromUrl(more,true);
				}
			}
			
			@Override
			/**
			 * 下拉刷新时的回调 
			 */
			public void onDownRefresh() {
				Toast.makeText(ctx, "开始下拉刷新了。。。", 0).show();
				requestDataFromUrl(newsChildren.url,false);
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
//				Toast.makeText(ctx, "position:"+position, 0).show();
				
				// 当点击listview内容中第一个条目时， position 的值为1 ,对应集合中，是下标为0的内容
				News news = itemBean.data.news.get(position-1);
				// 将当前条止的新闻标记为已读
				// 获得当前新闻的唯一标示，永久性存储起来。同时存在集合中
				String newsId = news.id;
				
				if(readNewsSet.contains(newsId)){ 
					// 有说明之前已经读过了，不做任何处理
					
				}else{
					// 如果没有读过，
					// 存入集合，同时，永久存储
					readNewsSet.add(newsId);
					// #30001#3002
					String oldStr = sp.getString(READ_NEWS_KEY, "");
					sp.edit().putString(READ_NEWS_KEY, oldStr+"#"+newsId).commit();
					// #30001#3002#3003
					
					// 点击条目以后，打开新闻详情，那么就不刷新listView
					listAdapter.notifyDataSetChanged();
				}
				
				// 跳转至详情页面
				String url = news.url;
				Intent intent = new Intent(ctx,NewsItemDetailActivity.class);
				intent.putExtra("web_url", url);
				ctx.startActivity(intent);
				
				
			}
		});
		
		return listview;
	}

	/**
	 * 已经读过的新闻的集合
	 */
	private HashSet<String> readNewsSet;
	
	@Override
	public void initData() {
		// 获得以前的读过的新闻ID的集合
		readNewsSet =new HashSet<String>();
		
		String string = sp.getString(READ_NEWS_KEY, ""); //  #234234#3454#234234
		String[] split = string.split("#");
		for (String str : split) {
			readNewsSet.add(str);
		}
		
//		text.setText(newsChildren.url);
		// 先判断是否有缓存数据
		
		String cache = JsonCacheUtils.getCache(ctx, ItcastUrl.BASE_URL+newsChildren.url);
		if(cache!=null){
			//parseData(cache);
		}
		
		requestDataFromUrl(newsChildren.url,false);
		// 初始化页面
	}

	/**
	 * 请求网络，获得数据
	 * @param subUrl
	 * @param isAddmore 是否是追加的操作
	 */
	private void requestDataFromUrl(String subUrl,final boolean isAddmore) {
		// 获得该页面，所需要的数据
		requestUrl(HttpMethod.GET, ItcastUrl.BASE_URL+subUrl, null, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String json = responseInfo.result;
				
				System.out.println("json:"+json);
				
				parseData(json,isAddmore);
				
				JsonCacheUtils.saveCache(ctx, ItcastUrl.BASE_URL+newsChildren.url, json);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				printfErrorLog(error);
			}
		});
	}

	/**
	 * 存储轮播图 文字描述信息的列表
	 */
	private List<String> topNewsDescList;
	/**
	 * 存储轮播图 图片URL 地址的列表
	 */
	private List<String> topNewsImageUrlList;
	/**
	 * 存储轮播图  指示点的列表
	 */
	private List<ImageView> pointList;
	
	/**
	 * 联网返回的数据bean 
	 */
	private NewsCenterItemBean itemBean;
	
	/**
	 * 
	 * @param json
	 * @param isAddmore 是否是追加内容   只处理，追加新闻条目
	 */
	private void parseData(String json, boolean isAddmore) {
	
		
		itemBean = new Gson().fromJson(json, NewsCenterItemBean.class);
		
//		System.out.println("size:::"+itemBean.data.topnews.size());
		
		if(rollViewPager == null){
			rollViewPager = new RollViewPager(ctx);
		}
		
		// 如果不是追加操作，才处理轮播图
		if (!isAddmore) {

			// 导播图的信息列表
			List<Topnews> topnews = itemBean.data.topnews;

			topNewsDescList = new ArrayList<String>();
			topNewsImageUrlList = new ArrayList<String>();

			for (int i = 0; i < topnews.size(); i++) {

				topNewsDescList.add(topnews.get(i).title); // 对应的文字
				topNewsImageUrlList.add(topnews.get(i).topimage); // 对应的图片的地址
			}

			// 初始化导航点
			initPointList();

			// 为轮播图viewpager 设置 文字描述信息
			rollViewPager.setDescList(topNewsDescList, top_news_title);
			// 为轮播图viewpager 设置 图片url
			rollViewPager.setImageUrls(topNewsImageUrlList);

			rollViewPager.setPointList(pointList);

			// 轮播图，开始滚动
			rollViewPager.startRoll();

			rollViewPager.setOnItemClickListener(new IOnItemClickListener() {
				@Override
				public void onItemClick(int position) {

					Toast.makeText(ctx, topNewsDescList.get(position), 1)
							.show();

				}
			});
			// 将viewpager 添加至布局中，预存的位置
			top_news_viewpager.removeAllViews();
			top_news_viewpager.addView(rollViewPager);

		}
		//------------ 上面的轮播图的逻辑
		
		
		
		
		if(isAddmore){ // 如果是追加新闻的操作
			
			newsList.addAll(itemBean.data.news);
			listAdapter.notifyDataSetChanged();
			
		}else{
			// 显示listview 中的内容
			if(listAdapter == null){ 
				newsList = itemBean.data.news;
				listAdapter = new MyListAdapter(newsList);
				listview.setAdapter(listAdapter);
			}else{
				listAdapter.notifyDataSetChanged();
			}
		}
		
		// listView 刷新完成
		listview.freshFinish();
	}

	/**
	 * 存放新闻条目的集合
	 */
	private List<News> newsList;
	
	
	private void initPointList() {
		pointList = new ArrayList<ImageView>();
		
		// 清空加指示点的布局，
		dots_ll.removeAllViews();
		
		for(int i=0;i<topNewsDescList.size();i++){
			ImageView point = new ImageView(ctx);
			if(i == 0){
				point.setBackgroundResource(R.drawable.dot_focus);
			}else{
				point.setBackgroundResource(R.drawable.dot_normal);
			}
			pointList.add(point); // 添加至集合，方便管理
			
			// 添加至布局页面，才能显示出来
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
			params.leftMargin=8;
			params.rightMargin = 8;
			point.setLayoutParams(params);// warp_content 就是 -2
			
			dots_ll.addView(point);
		}
	}

	private MyListAdapter listAdapter;

	private RollViewPager rollViewPager;
	
	private class MyListAdapter extends CZAdapter<NewsCenterItemBean.News>{

		public MyListAdapter(List<News> dataList) {
			super(dataList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view;
			ViewHolder vh;
			
			if(convertView == null){
				view = View.inflate(ctx, R.layout.layout_news_item, null);
				vh = new ViewHolder();
				
				vh.tv_title = (TextView) view.findViewById(R.id.tv_title);
				vh.tv_pub_date = (TextView) view.findViewById(R.id.tv_pub_date);
				vh.image = (ImageView) view.findViewById(R.id.iv_img);
				
				view.setTag(vh);
				
			}else{
				view = convertView;
				vh = (ViewHolder) view.getTag();
			}
			
			// 给条目绑定数据
			News news = dataList.get(position);  // dataList 是在 CZAdapter 构造 函数中进行赋值的。
			
			vh.tv_title.setText(news.title);
			vh.tv_pub_date.setText(news.pubdate);

			bitmapUtils.display(vh.image, news.listimage);
			
			if(readNewsSet.contains(news.id)){ // 读过
				vh.tv_title.setTextColor(Color.RED);
			}else{
				vh.tv_title.setTextColor(Color.BLACK);
			}
			
			return view;
		}
	}
	
	private class ViewHolder{
		TextView tv_title ;
		TextView tv_pub_date;
		ImageView image;
	}
	
	
}
