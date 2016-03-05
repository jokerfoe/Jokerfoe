package zz.itcast.zhbj_z3.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class ImageCacheUtils {

	private Context ctx;
	
	private LruCache<String, Bitmap> lruCache;
	
	private File cacheDir;

	/**
	 * 有5个线程的线程池
	 */
	private ExecutorService threadPool;
	

	
	public ImageCacheUtils(Context ctx) {
		this.ctx = ctx;
		
		// 将虚拟机总内存的 8分之一做为缓存来用
		int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
		
		lruCache = new LruCache<String, Bitmap>(maxSize){
			@Override
			// 告诉lruCache 每一个 value 占多少内存
			protected int sizeOf(String key, Bitmap value) {
				// 一行的字节数，* 行数，就是图片的真实的大小
				return value.getRowBytes() * value.getHeight();
			}
		};
		
		threadPool = Executors.newFixedThreadPool(5);
		
		cacheDir = ctx.getFilesDir(); //  /data/data/xxx.xx.xx/files/
	}

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
	
	
	/**
	 * 给图片显示指定的内容
	 * @param image
	 * @param listimage
	 */
	public void display(ImageView image, String imageUrl) {
		Bitmap bitmap = null;
		// 第一步，从缓存取图片
		bitmap = lruCache.get(imageUrl);
		if(bitmap!=null){
			System.out.println(" 该图片，从缓存取得");
			image.setImageBitmap(bitmap);
			return ;
		}
		// 第二步，从文件中取图片
		// 根据图片的url 获得图片的名称，做法有二种，  1，直接截取名称， 2、根据url 换算出图片名
		
		String jpgName = MD5Encoder.encode(imageUrl); // 如果嫌32 位字符的名，太长，可以进行随意截取
		File jpgFile = new File(cacheDir, jpgName);
		if(jpgFile.exists()){ // 文件存在，是true
			bitmap = BitmapFactory.decodeFile(jpgFile.getAbsolutePath());
			// 将图片存入缓存
			lruCache.put(imageUrl, bitmap);
			// 将图片赋值为imageView
			image.setImageBitmap(bitmap);
			System.out.println(" 该图片，从文件取得");
			return ;
		}
		// 第三步，联网取图片 ,耗时操作，一定要开子线程，
		
		int urlPosition = (Integer) image.getTag();
		threadPool.execute(new DownloadBitmapTask(image,imageUrl,urlPosition));
		
	}
	
//	private Handler handler = new Handler(Looper.getMainLooper()){};
	
	private class DownloadBitmapTask implements Runnable{

		/**
		 * 图片的路径
		 */
		private String imageUrl;
		
		private ImageView iamgeView;

		/**
		 * imageUrl 路径对应的图片，应显示在listview 上的位置
		 */
		private int position;
		
		public DownloadBitmapTask(ImageView image,String imageUrl,int position){
			this.imageUrl = imageUrl;
			this.iamgeView = image;
			this.position = position;
		}
		@Override
		public void run() {
			
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				
				conn.setConnectTimeout(60000);
				conn.setReadTimeout(60000);
				
				int responseCode = conn.getResponseCode();
				if(responseCode == 200){
					InputStream inputStream = conn.getInputStream();
					
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					// 存文件一份
					
					String fileName = MD5Encoder.encode(imageUrl);
					FileOutputStream outputStream = new FileOutputStream(cacheDir.getAbsolutePath()+File.separator+fileName);
					bitmap.compress(CompressFormat.JPEG, 100, outputStream); // 将图片以jpeg 的格式存储,100是指无压缩
					// 存缓存一份
					lruCache.put(imageUrl, bitmap);
					
					
					Thread.sleep(3000); // 为了模拟联网耗时操作，在此休眠
					
					// 显示在ImageView 上 ,注意要在主线程，操作imageView 
					
					((Activity)ctx).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							System.out.println(" 该图片，从网络取得");
							
							int imageViewPositon = (Integer) iamgeView.getTag(); // 当前imageView 对象在listView中的位置
							
							if(position == imageViewPositon){
								iamgeView.setImageBitmap(lruCache.get(imageUrl));
							}else{
								System.out.println("imageView已被复用，应当显示新位置的图片，");
							}
							
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
			
		}
	}

}
