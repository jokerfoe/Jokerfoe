package zz.itcast.zhbj_z3;

import zz.itcast.zhbj_z3.fragment.HomeFragment;
import zz.itcast.zhbj_z3.fragment.MenuFragment;
import android.os.Bundle;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity{

	private SlidingMenu slidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置没有默认标题
		// 设置内容，和菜单布局
		setContentView(R.layout.content);
		setBehindContentView(R.layout.menu_frame);
		
		slidingMenu = getSlidingMenu();
		// 对slidingMenu 进行设置
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		// 添加fragment 
		// 添加菜单
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.menu,new MenuFragment(),"MENU")
		.commit();
		
		// 添加内容
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.content_frame,new HomeFragment(),"HOME")
		.commit();
		
		
	}

	/**
	 * 获得 menuFragment
	 * @return
	 */
	public MenuFragment getMenuFragment(){
		return (MenuFragment) getSupportFragmentManager().findFragmentByTag("MENU");
	}
	
	/**
	 * 获得 menuFragment
	 * @return
	 */
	public HomeFragment getHomeFragment(){
		return (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME");
	}
	
	
	
}
