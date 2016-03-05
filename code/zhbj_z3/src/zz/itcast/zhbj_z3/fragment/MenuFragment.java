package zz.itcast.zhbj_z3.fragment;

import java.util.List;

import zz.itcast.zhbj_z3.MainActivity;
import zz.itcast.zhbj_z3.R;
import zz.itcast.zhbj_z3.adapter.CZAdapter;
import zz.itcast.zhbj_z3.bean.NewsCenterBean.NewsCenterItem;
import zz.itcast.zhbj_z3.pager.NewsCenterPager;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MenuFragment extends BaseFragment {

	@ViewInject(R.id.lv_menu_news_center)
	private ListView listView;
	
	/**
	 * 二级菜单条目的列表
	 */
	private List<NewsCenterItem> itemList;
	
	private int currSelectPosition;
	
	@Override
	public View initView() {
//		TextView text = new TextView(getActivity());
//		text.setText("菜单页面");
		View view = View.inflate(getActivity(), R.layout.layout_left_menu, null);
		
		ViewUtils.inject(this, view);
		
		return view;
	}

	@Override
	public void initData() {
	}

	/**
	 * 根据 新闻中心的数据，初始化菜单
	 * @param data
	 */
	public void initMenu(List<NewsCenterItem> itemList) {
		this.itemList = itemList;
		currSelectPosition = 0; // 默认选中第一条
		
		if(menuAdapter==null){
			menuAdapter = new MyMenuAdapter(itemList);
			listView.setAdapter(menuAdapter);
			
		}else{
			menuAdapter.notifyDataSetChanged();
		}
		
		// 
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 改变listView条目的显示效果
				currSelectPosition = position;
				menuAdapter.notifyDataSetChanged();
				// 切换新闻中心内容页的内容。  NewsCenterPager 
				
				MainActivity activity = (MainActivity) getActivity();
				HomeFragment homeFragment = activity.getHomeFragment();
//				NewsCenterPager newsCenterPager = homeFragment.getNewsCenterPager();
				NewsCenterPager newsCenterPager = 
						(NewsCenterPager) homeFragment.getPagerByPosition(HomeFragment.INDEX_NEWS_CENTER);
				
				newsCenterPager.switchPage(position);
				
				// 关闭侧滑菜单 
				activity.toggle();
				
			}
		});
		
	}

	private MyMenuAdapter menuAdapter;
	
	private class MyMenuAdapter extends CZAdapter{

		public MyMenuAdapter(List dataList) {
			super(dataList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view ;
			if(convertView == null){
				view = getActivity().getLayoutInflater().inflate(R.layout.layout_item_menu, null);
			}else{
				view = convertView;
			}
			
			ImageView iv_menu_item = (ImageView) view.findViewById(R.id.iv_menu_item);
			TextView tv_menu_item = (TextView) view.findViewById(R.id.tv_menu_item);
			
			// 由于此处条目比较少，我就不考滤convertView 复用的情况了
			
			tv_menu_item.setText(itemList.get(position).title);
			
			if(currSelectPosition == position){ // 当前选中的条目
				view.setBackgroundResource(R.drawable.menu_item_bg_select);
				iv_menu_item.setBackgroundResource(R.drawable.menu_arr_select); // 红色的三角图片
				tv_menu_item.setTextColor(Color.RED);
				
			}else{
				// 默认的显示效果
				// listView 真理一： 如果 getView 方法 中，复用了 convertView 那么，有if 判断 ，必须 写else 
				//  如果没有复用convertView 可以不写else 
				
				view.setBackgroundColor(Color.TRANSPARENT);
				iv_menu_item.setBackgroundResource(R.drawable.menu_arr_normal); // 红色的三角图片
				tv_menu_item.setTextColor(Color.WHITE);
			}
			
			return view;
		}
		
	}
	
//	private class MyMenuAdapter extends BaseAdapter{
//
//		@Override
//		public int getCount() {
//			return itemList.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view ;
//			if(convertView == null){
//				view = getActivity().getLayoutInflater().inflate(R.layout.layout_item_menu, null);
//			}else{
//				view = convertView;
//			}
//			
//			ImageView iv_menu_item = (ImageView) view.findViewById(R.id.iv_menu_item);
//			TextView tv_menu_item = (TextView) view.findViewById(R.id.tv_menu_item);
//			
//			// 由于此处条目比较少，我就不考滤convertView 复用的情况了
//			
//			tv_menu_item.setText(itemList.get(position).title);
//			
//			if(currSelectPosition == position){ // 当前选中的条目
//				view.setBackgroundResource(R.drawable.menu_item_bg_select);
//				iv_menu_item.setBackgroundResource(R.drawable.menu_arr_select); // 红色的三角图片
//				tv_menu_item.setTextColor(Color.RED);
//				
//			}else{
//				// 默认的显示效果
//				// listView 真理一： 如果 getView 方法 中，复用了 convertView 那么，有if 判断 ，必须 写else 
//				//  如果没有复用convertView 可以不写else 
//				
//				view.setBackgroundColor(Color.TRANSPARENT);
//				iv_menu_item.setBackgroundResource(R.drawable.menu_arr_normal); // 红色的三角图片
//				tv_menu_item.setTextColor(Color.WHITE);
//				
//			}
//			
//			return view;
//		}
//		
//	}
	
}
