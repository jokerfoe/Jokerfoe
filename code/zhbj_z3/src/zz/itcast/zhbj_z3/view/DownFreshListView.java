package zz.itcast.zhbj_z3.view;

import zz.itcast.zhbj_z3.R;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class DownFreshListView extends ListView {

	/**
	 * listview 的总的头布局
	 */
	private LinearLayout allHeader;

	/**
	 * 在代码中 用 new 创建对象时，调用
	 * @param context
	 */
	public DownFreshListView(Context context) {
		super(context);
		initView();
	}
	
	/**
	 * 在布局文件中声明，将布局文件转换为view对象时，由系统自动调用
	 * @param context
	 * @param attrs
	 */
	public DownFreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	/**
	 * 下拉刷新的头
	 */
	@ViewInject(R.id.refresh_header_view)
	private LinearLayout refresh_header_view;
	
	/**
	 * 环形进度条
	 */
	@ViewInject(R.id.refresh_header_progressbar)
	private ProgressBar refresh_header_progressbar;
	
	/**
	 * 刷新头中的红色箭头
	 */
	@ViewInject(R.id.refresh_header_imageview)
	private ImageView refresh_header_imageview;
	
	/**
	 * 刷新头中，描述文字
	 */
	@ViewInject(R.id.refresh_header_text)
	private TextView refresh_header_text;
	/**
	 * 刷新头中，时间，描述文字
	 */
	@ViewInject(R.id.refresh_header_time)
	private TextView refresh_header_time;

	/**
	 * 开始刷新时，箭头的执行动画
	 */
	private RotateAnimation startFreshAnim;

	/**
	 * 箭头恢复 初始状态
	 */
	private RotateAnimation restoreAnim;

	/**
	 * 刷新头的高度
	 */
	private int refreshHeight;

	/**
	 * 脚view的高度
	 */
	private int footerHeight;

	private void initView() {
		
		allHeader = (LinearLayout) View.inflate(getContext(), R.layout.refresh_header, null);
		ViewUtils.inject(this, allHeader);
		// 添加listView 头
		this.addHeaderView(allHeader);
		initAnimation();
		
//		refresh_header_view.getHeight(); // 此处，这个方法不能用，为啥，希望你懂的。
		
		// 手工测量view 的大小
		refresh_header_view.measure(0, 0); // 测量，refresh_header_view 的大小
		
		refreshHeight = refresh_header_view.getMeasuredHeight(); // 获得该view的测量值 高度。
		refresh_header_view.setPadding(0, -refreshHeight, 0, 0);
		
		// 设置滑动监听
		this.setOnScrollListener(scrollListener);
		
		refresh_footer = View.inflate(getContext(), R.layout.refresh_footer, null);
		this.addFooterView(refresh_footer);
		
		refresh_footer.measure(0, 0);// 手工测量大小
		footerHeight = refresh_footer.getMeasuredHeight();
	}
	/**
	 * listView可看到的第一个条目的位置
	 */
	private int mFirstVisibleItem;
	
	/**
	 * 判断是否是正在加载更多
	 */
	private boolean isAddMoreing = false;
	
	private OnScrollListener scrollListener = new OnScrollListener() {
		@Override
		// 当listView 滑动状态，发生改变时，调用
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
//			OnScrollListener.SCROLL_STATE_IDLE// 空闲
//			OnScrollListener.SCROLL_STATE_TOUCH_SCROLL // 拖动的状态
//			OnScrollListener.SCROLL_STATE_FLING //飞速滑动的状态
			
			// 判断当前看到的，最后一个条目是谁,如果是脚底view,就执行加载更多的操作
			int lastVisiblePosition = getLastVisiblePosition(); // 获得当前屏幕可见的，最后一个条目的位置
			
			int count = getAdapter().getCount()-1;
			
			System.out.println("lastVisiblePosition::"+lastVisiblePosition);
			System.out.println("count::"+count);
			
			if(lastVisiblePosition == count && !isAddMoreing){// 当用户看到脚view时，
				System.out.println("lastVisiblePosition::"+lastVisiblePosition);
				// 加载更多，
				isAddMoreing = true;
				// 将脚显示出来
				refresh_footer.setPadding(0, 0, 0, 0);
				
//				// 发送handler 信息，模拟加载更多的操作
//				handler.sendEmptyMessageDelayed(88, 1000);
				if(refreshFinishListener!=null){
					refreshFinishListener.onLoadingMore();
				}
				
			}
		}
		
		@Override
		// 当listView 滑动时，调用该方法
		/**
		 * firstVisibleItem 当前能看到的，第一个条目的位置
		 * visibleItemCount 当前屏幕中能看到的条目的总数
		 * totalItemCount listView的总的条目个数
		 */
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			
			// 当第一个可见条目，是listview的第一个条目时，下拉才有效
//			System.out.println("firstVisibleItem::"+firstVisibleItem);
			mFirstVisibleItem = firstVisibleItem;
		}
	};
	
	int touchDownY = -1;
	
	private void flushState() {
		switch (currState) {
		case STATE_PULL_REFRESH: // 下拉刷新的状态
			refresh_header_text.setText("下拉刷新");
			refresh_header_imageview.startAnimation(restoreAnim);
			
			break;
		case STATE_RELEASE_REFRESH: // 释放刷新的状态
			refresh_header_text.setText("释放刷新");
			refresh_header_imageview.startAnimation(startFreshAnim);
			
			break;
		case STATE_REFRESH_ING: // 正在刷新的状态
			refresh_header_text.setText("正在刷新...");
			// 取消动画
			refresh_header_imageview.clearAnimation();
			refresh_header_imageview.setVisibility(View.GONE);
			refresh_header_progressbar.setVisibility(View.VISIBLE);
			// 将刷新头的pading 设为  0
			refresh_header_view.setPadding(0, 0, 0, 0);
			
			// 模拟 刷新的动作
//			handler.sendEmptyMessageDelayed(99, 2000);
			if(refreshFinishListener!=null){
				refreshFinishListener.onDownRefresh();
			}
			
			break;
		}
	}
	
//	private Handler handler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			
//			// 刷新完成
//			freshFinish();
//			
//		};
//	};
	
	
	public void freshFinish(){
		if(currState == STATE_REFRESH_ING){ // 更正刷新，，然后，刷新完成
			// 刷新完成，将页面恢复成初始状态
			refresh_header_text.setText("下拉刷新");
			refresh_header_imageview.setVisibility(View.VISIBLE);
			refresh_header_progressbar.setVisibility(View.INVISIBLE);
			refresh_header_view.setPadding(0, -refreshHeight, 0, 0);
			// 改为初始状态
			currState = STATE_PULL_REFRESH;
		}
		
		if(isAddMoreing){ // 正在加载更多,加载更多，完成后，刷新
			isAddMoreing = false;
			// 隐藏加载更多的进度条
			refresh_footer.setPadding(0, -footerHeight, 0, 0);
//			Toast.makeText(getContext(), "加载更多，完成...", 1).show();
		}
	};
	
	/**
	 * 下拉刷新
	 */
	private final int STATE_PULL_REFRESH = 100;
	
	/**
	 * 满足条件后，释放刷新
	 */
	private final int STATE_RELEASE_REFRESH = 101;
	
	/**
	 * 正在刷新的状态
	 */
	private final int STATE_REFRESH_ING = 102;
	
	/**
	 * 默认状态，下拉刷新
	 */
	private int currState = STATE_PULL_REFRESH;

	/**
	 * 轮播图
	 */
	private View customHeader;

	private View refresh_footer;
	
	/**
	 * 响应listView 的触摸事件
	 */
	public boolean onTouchEvent(android.view.MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// down 事件，有可能执行，也有可能不执行，如果点中了子view，而子view也需要事件，此事，down就不会执行
			touchDownY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			// 只有当轮播图完成显示在listView中后，向下拉，才改变刷新头的padding值
			// listview 在屏幕中的位置    轮播图在屏幕中的位置
			int[] location = new int[2];
			this.getLocationInWindow(location);
			int listViewY = location[1]; // listView在窗体中的高度
			
			customHeader.getLocationInWindow(location);
			
			int customHeaderY = location[1];
			System.out.println("listViewY::"+listViewY);
			System.out.println("customHeaderY::"+customHeaderY);
			
			if(listViewY > customHeaderY){//此时不用做任务处理，让listview自己处理
				touchDownY = -1;
				break;
			}
			
			if(touchDownY == -1){ // 说明没有down事件
				touchDownY = (int) ev.getY(); // 将第一个move事件中的值，来当做downY的值
				break;
			}
			
			
			// 计算手指在屏幕上移动的距离 ,从而改变 刷新头的 padingTop 值
			int padingTop = (int) (-refreshHeight + ev.getY() - touchDownY);
			
			// 可见的第一个条目是listView的第一个条目时，才去改变 刷新头的高度
			if(mFirstVisibleItem == 0 && padingTop > -refreshHeight){
			
				refresh_header_view.setPadding(0, padingTop, 0, 0); // 设置刷新头的高度
				
				// 往下拉时，当 pading值 >=0,且当前状态为 下拉刷新时，此时应该改变状态为  释放刷新 
				if(padingTop >=0 && currState == STATE_PULL_REFRESH){
					currState = STATE_RELEASE_REFRESH;
					// 刷新状态 // 执行动画
					flushState();
				}
				
				// 当往下拉后，没有释放，而是向上滑动
				// 此时，如果 padingTop < 0 并且 当前状态为 释放刷新的状态，
				// 那么应该改变状态为  下拉刷新的状态
				if(padingTop< 0 && currState == STATE_RELEASE_REFRESH){
					currState = STATE_PULL_REFRESH;
					// 刷新状态，执行动画
					refresh_header_imageview.startAnimation(restoreAnim);
					flushState();
				}
				// 此时就不让listview 滑动，否则页面显示就不准确，无法确定页面上滑是padingTop 减少引起的，还是listview向上滑动引起的
				return true;
			}
			
			break;
		case MotionEvent.ACTION_UP:
			// 如果当前状态是 释放刷新时，
			if(currState == STATE_RELEASE_REFRESH){
				// 开始刷新
				currState = STATE_REFRESH_ING;
				flushState();
			}
			
			// 如果此时，状态是，下拉刷新，那么，抬起手指后，就恢复原状
			if(currState == STATE_PULL_REFRESH){
				// 复原
				refresh_header_view.setPadding(0, -refreshHeight, 0, 0);
			}
			touchDownY = -1;
			break;
		}
		return super.onTouchEvent(ev);
	};
	
	
	
	
	


	private void initAnimation() {
		startFreshAnim = new RotateAnimation(
				0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		startFreshAnim.setDuration(200);
		startFreshAnim.setFillAfter(true);
		
		restoreAnim = new RotateAnimation(
				-180, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		restoreAnim.setDuration(200);
		restoreAnim.setFillAfter(true);
		
	}

	/**
	 * 为listView 添加一个显示的，通用的头，在此处，实际就是那个，轮播图
	 * @param view
	 */
	public void addCustomHeader(View view){
		this.customHeader = view;
		allHeader.addView(view);// 将轮播图，添加至listView 的头
	}
	
	
	public void setOnRefrshFinishListener(IonRefreshFinishListener refreshFinishListener){
		this.refreshFinishListener = refreshFinishListener;
	}
	
	private IonRefreshFinishListener refreshFinishListener;
	
	/**
	 * 当listView 刷新完成时的监听
	 * @author leo
	 *
	 */
	public interface IonRefreshFinishListener{
		
		/**
		 * 下拉刷新的回调方法
		 */
		void onDownRefresh();
		
		/**
		 * 加载更多时的回调方法
		 */
		void onLoadingMore();
	}

}
