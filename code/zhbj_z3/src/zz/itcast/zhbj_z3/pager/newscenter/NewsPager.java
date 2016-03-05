package zz.itcast.zhbj_z3.pager.newscenter;

import java.util.ArrayList;
import java.util.List;

import zz.itcast.zhbj_z3.R;
import zz.itcast.zhbj_z3.bean.NewsCenterBean.NewsCenterItem;
import zz.itcast.zhbj_z3.pager.BasePager;
import zz.itcast.zhbj_z3.view.indicator.TabPageIndicator;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class NewsPager extends BasePager {

	private NewsCenterItem newsCenterItem;
	
	/**
	 * 
	 * @param ctx
	 * @param newsCenterItem  当前页面应该显示的内容，封装的bean 
	 */
	public NewsPager(Context ctx, NewsCenterItem newsCenterItem) {
		super(ctx);
		this.newsCenterItem = newsCenterItem;
	}

	@ViewInject(R.id.indicator)
	private TabPageIndicator indicator;
	
	@ViewInject(R.id.pager)
	private ViewPager viewPager;
	
	@Override
	public View initView() {
		
		View view = View.inflate(ctx, R.layout.frag_news, null);
		
		ViewUtils.inject(this, view);
		
		return view;
	}

	@Override
	public void initData() {

		// 准备viewPager要显示的页面，全部存在itemPagerList 集合中
		itemPagerList = new ArrayList<BasePager>();
		
		for(int i=0;i<newsCenterItem.children.size();i++){
			itemPagerList.add(new NewsCenterItemPager(ctx,newsCenterItem.children.get(i)));
		}
		
		pageAdapter = new MyPagerAdapter();
		
		viewPager.setAdapter(pageAdapter);
		
		// 将页面指针，与viewPager绑定
		indicator.setViewPager(viewPager);
		
		// 初始化viewPager 中第一页的数据
		itemPagerList.get(0).initData();
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if(position == 0){
					// 第一页可以滑动
					activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				}else{
					// 不是第一页，就不让slidingMenu 滑动
					activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				}
				
				// 设置indicator 
				indicator.setCurrentItem(position);
				// 加载相应页面的数据
				// 让对应的页面，执行 initData()
				itemPagerList.get(position).initData();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	private List<BasePager> itemPagerList;
	
	private MyPagerAdapter pageAdapter;
	
	private class MyPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return newsCenterItem.children.size();
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 获得 viewPager 中的一个页面
			View view = itemPagerList.get(position).getView();
			container.addView(view);
			
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
			container.removeView((View)object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		
		@Override
		public CharSequence getPageTitle(int position) {
			return newsCenterItem.children.get(position).title;
		}
	}
	
}
