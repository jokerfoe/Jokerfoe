package zz.itcast.zhbj_z3.fragment;

import java.util.ArrayList;
import java.util.List;

import zz.itcast.zhbj_z3.R;
import zz.itcast.zhbj_z3.pager.BasePager;
import zz.itcast.zhbj_z3.pager.HomePager;
import zz.itcast.zhbj_z3.pager.NewsCenterPager;
import zz.itcast.zhbj_z3.pager.PolicyPager;
import zz.itcast.zhbj_z3.pager.SettingPager;
import zz.itcast.zhbj_z3.pager.SmartServicePager;
import zz.itcast.zhbj_z3.view.MyViewPager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeFragment extends BaseFragment implements OnCheckedChangeListener {

	@ViewInject(R.id.layout_content)
	private MyViewPager viewPager;
	
	@ViewInject(R.id.main_radio)
	private RadioGroup main_radio;
	
	@Override
	public View initView() {
		View view = View.inflate(ctx, R.layout.frag_home, null);
		
		/**
		 * 第一个参数是  有注解的类的对象。
		 * 第二个参数是，包含指定ID的view
		 */
		ViewUtils.inject(this, view);
		
		return view;
	}

	@Override
	public void initData() {
		
		main_radio.check(R.id.rb_function); // 选中第0个radioButton
		main_radio.setOnCheckedChangeListener(this);
		
		// 初始化5个页面
		pagerList = new ArrayList<BasePager>();
		
		pagerList.add(new HomePager(getActivity()));
		pagerList.add(new NewsCenterPager(getActivity()));
		pagerList.add(new SmartServicePager(getActivity()));
		pagerList.add(new PolicyPager(getActivity()));
		pagerList.add(new SettingPager(getActivity()));
		
		
		adapter = new MyPagerAdapter();
		viewPager.setAdapter(adapter);
		
	}

	/**
	 * viewPager 要显示的页面集合
	 */
	private List<BasePager> pagerList;
	
	@Override
	/**
	 * 响应radioGroup 选择改变的事件
	 * @param group
	 * @param checkedId
	 */
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int selectPosition = 0;
		switch (checkedId) {
		case R.id.rb_function: // 首页
			selectPosition = 0;
			break;
		case R.id.rb_news_center: // 新闻中心
			selectPosition = 1;
			break;
		case R.id.rb_smart_service:// 智慧服务
			selectPosition = 2;
			break;
		case R.id.rb_gov_affairs:// 政府指南
			selectPosition = 3;
			break;
		case R.id.rb_setting: // 设置
			selectPosition = 4;
			break;
		}
		
		// 让viewPager 切换至对应的页面
		viewPager.setCurrentItem(selectPosition);
		// 加载对应页面中的内容
		// 当 selectPosition = 1 ，执行 NewsCenterPager 类中的init(); 方法 
		pagerList.get(selectPosition).initData(); 
	}
	
	private MyPagerAdapter adapter;
	
	/**
	 * 系统的viewPager 在此处有二个问题，
	 *  第一：系统的viewPager 会有预加载功能，会加载，当前，和左右，条一个页面。在此处，会造成，内存紧张，甚至溢出。
	 * 	第二，和slidingMenu 或者，页中的其他内容，触摸事件冲突
	 *
	 */
	
	private class MyPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return pagerList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			
			System.out.println("instantiateItem position::"+position);
			
			View view = pagerList.get(position).getView();
			container.addView(view);
			
			return view;
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
			container.removeView((View)object);
			
			System.out.println("destroyItem position::"+position);
		}
		
	}
	
	public NewsCenterPager getNewsCenterPager(){
		return (NewsCenterPager) pagerList.get(1);
	}
	
	public static final int INDEX_HOME = 0;
	public static final int INDEX_NEWS_CENTER = 1;
	public static final int INDEX_SMART_SERVICE = 2;
	public static final int INDEX_POLICY = 3;
	public static final int INDEX_SETTING = 4;
	
	/**
	 * 
	public static final int INDEX_HOME = 0;
	public static final int INDEX_NEWS_CENTER = 1;
	public static final int INDEX_SMART_SERVICE = 2;
	public static final int INDEX_POLICY = 3;
	public static final int INDEX_SETTING = 4;
	 * @return
	 */
	public BasePager getPagerByPosition(int position){
		
		return pagerList.get(position);
		
	}
	

}
