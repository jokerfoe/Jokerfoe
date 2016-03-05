package zz.itcast.zhbj_z3.pager;

import java.util.ArrayList;
import java.util.List;

import zz.itcast.zhbj_z3.MainActivity;
import zz.itcast.zhbj_z3.R;
import zz.itcast.zhbj_z3.bean.NewsCenterBean;
import zz.itcast.zhbj_z3.fragment.MenuFragment;
import zz.itcast.zhbj_z3.pager.newscenter.ArrPicPager;
import zz.itcast.zhbj_z3.pager.newscenter.EachOtherPager;
import zz.itcast.zhbj_z3.pager.newscenter.NewsPager;
import zz.itcast.zhbj_z3.pager.newscenter.TopNewsPager;
import zz.itcast.zhbj_z3.utils.ItcastUrl;
import zz.itcast.zhbj_z3.utils.JsonCacheUtils;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 新闻中心页面
 * @author Administrator
 *
 */
public class NewsCenterPager extends BasePager {

	public NewsCenterPager(Context ctx) {
		super(ctx);
	}

	@ViewInject(R.id.news_center_fl)
	private FrameLayout news_center_fl;
	
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
	
	/**
	 * 初始化标题 栏
	 */
	private void initTitleBar() {
		
		btn_left.setVisibility(View.GONE);
		imgbtn_text.setVisibility(View.GONE);
		imgbtn_right.setVisibility(View.GONE);
		btn_right.setVisibility(View.GONE);
		
		// 菜单图片
		imgbtn_left.setVisibility(View.VISIBLE);
//		imgbtn_left.setBackgroundResource(R.drawable.img_menu);
		imgbtn_left.setImageResource(R.drawable.img_menu);
		imgbtn_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 切换侧滑菜单的开关状态
				MainActivity activity = (MainActivity) ctx;
				activity.toggle();// 作用等价于   slidingMenu.toggle();
			}
		});
		// 标题文字 
		txt_title.setVisibility(View.VISIBLE);
	}
	
	
	@Override
	public View initView() {
//		TextView text = new TextView(ctx);
//		text.setText("新闻中心");
		
		View view = View.inflate(ctx, R.layout.news_center_frame, null);
		ViewUtils.inject(this, view);
		
		initTitleBar();
		
		return view;
	}



	/*
	 * 开发当中，联网的原则，
	 * 尽量减少联网次数，
	 * 每次联网，尽量多传递一些数据
	 */
	
	@Override
	/*
	 * 该方法 ，会在 点击 homeFragment 底顶RadioGroup 中第2个radioButton时，执行。
	 */
	public void initData() {
		// 先读取缓存数据，如果有，就显示缓存，数据， // 
		String jsonResult = JsonCacheUtils.getCache(ctx, ItcastUrl.NEWS_CENTER_CATEGORIES);
		if(jsonResult!=null){
			parseJson(jsonResult);
		}
		// 不管有没有缓存信息，都要进行联网，如果成功返回，更新缓存
		
		// 联网，获得服务器的信息
		requestUrl(HttpMethod.GET, ItcastUrl.NEWS_CENTER_CATEGORIES, null, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String json = responseInfo.result;
				System.out.println(json);
				// 解析结果
				parseJson(json);
				// 更新缓存
				JsonCacheUtils.saveCache(ctx, ItcastUrl.NEWS_CENTER_CATEGORIES, json);
			}
			@Override
			public void onFailure(HttpException error, String msg) {
				
			}
		});
	}
	
	/**
	 * 解析json串
	 * @param jsonResult
	 */
	private void parseJson(String jsonResult) {
		
		Gson gson  = new Gson();
		newCenterBean = gson.fromJson(jsonResult, NewsCenterBean.class);
		
		System.out.println(newCenterBean.data.get(0).children.get(2).url);
		
		// 获得数据以后，可以干的事：
		// 1\ 初始化滑动菜单
		MainActivity activity = (MainActivity) ctx;
		MenuFragment menuFragment = activity.getMenuFragment();
		menuFragment.initMenu(newCenterBean.data);
		
		// 2\ 新闻栏目的内容也有了
		if(subPagerList == null){
			subPagerList = new ArrayList<BasePager>();

			subPagerList.add(new NewsPager(ctx,newCenterBean.data.get(0)));
			subPagerList.add(new TopNewsPager(ctx,newCenterBean.data.get(1)));
			subPagerList.add(new ArrPicPager(ctx,newCenterBean.data.get(2)));
			subPagerList.add(new EachOtherPager(ctx,newCenterBean.data.get(3)));			
		}
		
		
		// 设置初始显示
		switchPage(0);
	}

	private List<BasePager> subPagerList;

	private NewsCenterBean newCenterBean;
	
	/**
	 * 切换内容页面
	 * position
	 * 0	新闻页面
	 * 1	专题
	 * 2 	组图
	 * 3 	互动
	 * @param position
	 */
	public void switchPage(int position) {
		// 使用不同的页面，替换   news_center_fl 的内容
		news_center_fl.removeAllViews(); // 清空内容
		news_center_fl.addView(subPagerList.get(position).getView());
		// 设置对应页面的内容
		subPagerList.get(position).initData(); // 执行动  NewsPager  中 的 initData();
		
		// 切换标题
		txt_title.setText(newCenterBean.data.get(position).title);
		
		switch (position) {
		case 2: // 组图模块
			// 在标题栏显示切换列表类型的图标
			btn_right.setVisibility(View.VISIBLE);
			btn_right.setImageResource(R.drawable.icon_pic_list_type);
			
			btn_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 让组图页面，切换显示风络,在listView 与gridView之间切换
					ArrPicPager arrPicPager = (ArrPicPager) subPagerList.get(2);
					arrPicPager.switchShowStyle(btn_right);
				}
			});
			
			break;
		}
	}

}
