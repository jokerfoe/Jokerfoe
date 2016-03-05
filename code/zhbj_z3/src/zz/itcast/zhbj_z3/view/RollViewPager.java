package zz.itcast.zhbj_z3.view;

import java.util.List;

import zz.itcast.zhbj_z3.R;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;

/**
 *  新闻中心，条目页面，中 显示轮播图 的viewPager
 * @author leo
 *
 */
public class RollViewPager extends ViewPager {

	// 描述文字的列表
	private List<String> topNewsDescList;
	// 保存存 图片的地址列表
	private List<String> topNewsImageUrlList;
	
	// 保存，指示点的列表
	private List<ImageView> pointList;

	
	private BitmapUtils bitmapUtils;
	
	public RollViewPager(Context context) {
		super(context);
		bitmapUtils = new BitmapUtils(context);
		
		this.setOnPageChangeListener(pageCnagedListener);
		
	}
	
	private OnPageChangeListener pageCnagedListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			// 改变描述文字
			top_news_title.setText(topNewsDescList.get(position));
			// 改变指示点
			// 将上一个指示点，改为正常显示
			pointList.get(lastDotsPosition).setBackgroundResource(R.drawable.dot_normal);
			// 将当前的指示点，显示为获得焦点的样子
			pointList.get(position).setBackgroundResource(R.drawable.dot_focus);
			
			lastDotsPosition = position;
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};
	
	/**
	 * 上一个指示点的位置
	 */
	private int lastDotsPosition = 0;
	/**
	 * 显示描述信息的文字
	 */
	private TextView top_news_title;

	/**
	 * 设置轮播图描文字
	 * @param topNewsDescList
	 * @param top_news_title
	 */
	public void setDescList(List<String> topNewsDescList,
			TextView top_news_title) {
		
		// 设置初始文字
		if(topNewsDescList!=null && topNewsDescList.size()>0 && top_news_title!=null){
			top_news_title.setText(topNewsDescList.get(0));
		}
		
		this.topNewsDescList = topNewsDescList;
		this.top_news_title = top_news_title;
		
		
	}

	/**
	 * 设置轮播图 显示的图片
	 * @param topNewsImageUrlList
	 */
	public void setImageUrls(List<String> topNewsImageUrlList) {
		this.topNewsImageUrlList = topNewsImageUrlList;
	}

	/**
	 * 设置轮播图 指示点
	 * @param pointList
	 */
	public void setPointList(List<ImageView> pointList) {
		this.pointList = pointList;
		
	}

	
	@Override
	/**
	 * 当前view被加载到窗体上显示时，调用
	 */
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		isRunning = true;
	}
	
	
	@Override
	/**
	 * 当前view从窗体上移除，调用
	 */
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isRunning = false;
	}
	
	/**
	 * 判断 轮播图是否运行
	 */
	private boolean isRunning = false;
	
	/**
	 * 开始滚动
	 */
	public void startRoll() {
		
		if(adapter==null){
			adapter = new MyRollAdapter();
			this.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		isRunning = true;
		// TODO 开始滚动
		// 清空历史消息
		handler.removeMessages(ROLL);
		// 发送新的信息
		handler.sendEmptyMessageDelayed(ROLL, 3000);
	}
	
	private final int ROLL = 100;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			if(isRunning){
				// 切换至下一页
				setCurrentItem((getCurrentItem()+1)%adapter.getCount());
				handler.sendEmptyMessageDelayed(ROLL, 3000);
			}
		};
	};
	
	private int downX;
	private int downY;
	/***
	 * 类ViewGroup 中分发事件的方法 
	 */
	public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
		
		//System.out.println("RollViewPager.java :"+ev.getAction()); // down 0  up 1  move 2   cancel 3 
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/**
			 * 参数为true 父View 不中断，我们自己处理事件
			 * 参数为false 不对父View做任务要求，是否中断，看父View自已
			 */
			requestDisallowInterceptTouchEvent(true); // 向父view们。请求，别中断事件，啊，把事件给我。
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			
			
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			
			int disX = Math.abs(moveX-downX);
			int disY = Math.abs(moveY-downY);
			
			// 只有在水平滑动，大于，竖起滑动时，轮播图，才允许发出请求，获得事件
			if(disX > disY && disX >10 ){ // disX >10  防止手指在屏幕上抖动
				
				// 当是第一个页面，手指向右滑，轮播图不需要事件
				if(getCurrentItem() == 0 && moveX>downX){
					requestDisallowInterceptTouchEvent(false);
				}else if(getCurrentItem() == adapter.getCount()-1 && downX > moveX){
					// 在最后一个页面，手指向左滑，轮播图不需要事件
					requestDisallowInterceptTouchEvent(false);
				}else{
					// 其他情况下，轮播图，处理事件
					requestDisallowInterceptTouchEvent(true);
				}
				
			}else{
				
				requestDisallowInterceptTouchEvent(false);
			}
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	};
	
	
	int touchDownX;
	int touchDownY;
	long startDownTime;
	@Override
	/**
	 * 我们自己处理一部分，touch事件
	 * super.onTouchEvent(ev) 处理ViewPager 原有的逻辑
	 * 
	 *点击的动作定义：  down 时的点，和up 事件时的点,二个定距离不超过 20 个象素，同时，时间不超过，500 称之为 点击的动作
	 * 
	 */
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			System.out.println("onTouchEvent ; MotionEvent.ACTION_DOWN");
			isRunning = false;
			
			// 取消刚才发送的handler 消息
			handler.removeMessages(ROLL);
			
			// 判断点击的动作
			touchDownX = (int) ev.getX();
			touchDownY = (int) ev.getY();
			startDownTime = SystemClock.uptimeMillis(); // 获得手机，从开机，到现在的时间
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println("onTouchEvent ; MotionEvent.ACTION_MOVE");
			
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("onTouchEvent ; MotionEvent.ACTION_UP");
			startRoll();
			
			// 判断 点击的动作
			int touchUpX = (int) ev.getX();
			int touchUpY = (int) ev.getY();
			long upTime = SystemClock.uptimeMillis(); 
			
			int disX = Math.abs(touchUpX- touchDownX); // X轴的距离
			int disY = Math.abs(touchUpY- touchDownY); // Y轴的距离 
			
			int disPoint = (int) Math.sqrt(disX*disX+disY*disY); // 勾股定理，求得二个点的距离
			
			if(disPoint < 20 && (upTime-startDownTime)<500){
				//点击的动作 ,触发，点击的监听
				if(itemClickListener!=null){
					itemClickListener.onItemClick(getCurrentItem());
				}
			}
			
			/*
			 *  当我们收到down事件，和前几个move事件，然后，父view将事件中断后，
			 *  我们就收不到UP事件，此时，父view为给我们一个cancel事件
			 *  * up事件，和cancel 只能发生一个，不会二个都发生。
			 */
			break;
		case MotionEvent.ACTION_CANCEL: 
			System.out.println("onTouchEvent ; MotionEvent.ACTION_CANCEL");
			startRoll();
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	
	private MyRollAdapter adapter ;
	
	private class MyRollAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return pointList.size();
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			
			ImageView image = (ImageView) View.inflate(getContext(), R.layout.viewpager_item, null);
			
			//TODO  为image设置图片
			String imageUrl = topNewsImageUrlList.get(position);
			/*
			 * 联网获得图片，设置为imageView ,为了提高效率，在此处，都要用到图片缓存，
			 * 三级缓存  
			 * 1、 缓存在Map 集合中，<k,v> k 是图片的唯一标示（一般用图片的URL或是图片名)v 是 bitmap本身
			 * 		* 注意事项：如果存放在集合中，要防止内存溢出( OOM 异常 out of memary )
			 * 		* 解决方法 ，LruCache 基于lru 算法的集合缓存工具	lru  less recent use ( 最少，最近使用) 
			 * 2、缓存在sd卡的某个特定目录中。图片按一定的规则保存 如     /mnt/sdcard/zhbj/imageCache/xxxxx.jpg
			 * 3、从网上获取。
			 * 当ImageView需要图片时，加载图片的步骤：
			 * 	1、先从LruCache 中取，取到了就直接用，
			 *  2、如果LruCache没有取到，就从sd卡缓存目录中去取，取到了就放到LruCache 中，然后，执行第1步。
			 *  3、如果SD卡缓存目录中也没有，那么，联网获取。
			 *  4、联网下载图片保存在SD卡后，再存入LruCache 然后，再交给ImageView使用
			 *  
			 *  其他的加载图片的工具：如 SmartImageView
			 */
			bitmapUtils.display(image, imageUrl);
			
			container.addView(image);
			return image;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
			
			container.removeView((View)object);
		}
	}

	public void setOnItemClickListener(IOnItemClickListener itemClickListener){
		this.itemClickListener = itemClickListener;
	}
	
	private IOnItemClickListener itemClickListener;
	
	/**
	 * 轮播图条目的点击事件
	 * @author leo
	 * 点击的动作定义：  down 时的点，和up 事件时的点,二个定距离不超过 20 个象素，同时，时间不超过，500 称之为 点击的动作
	 */
	public interface IOnItemClickListener{
		
		/**
		 * 点击某个条目时，回调 该方法 
		 * @param position 
		 */
		void onItemClick(int position);
	}
	
}
