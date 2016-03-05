package zz.itcast.zhbj_z3.pager.newscenter;

import java.util.List;

import zz.itcast.zhbj_z3.R;
import zz.itcast.zhbj_z3.adapter.CZAdapter;
import zz.itcast.zhbj_z3.bean.NewsCenterBean.NewsCenterItem;
import zz.itcast.zhbj_z3.bean.PhotosBean;
import zz.itcast.zhbj_z3.bean.PhotosBean.PhotosItem;
import zz.itcast.zhbj_z3.pager.BasePager;
import zz.itcast.zhbj_z3.utils.ImageCacheUtils;
import zz.itcast.zhbj_z3.utils.ItcastUrl;
import zz.itcast.zhbj_z3.utils.JsonCacheUtils;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 组图
 * @author leo
 *
 */
public class ArrPicPager extends BasePager {

	public ArrPicPager(Context ctx, NewsCenterItem newsCenterItem) {
		super(ctx);
	}

	@ViewInject(R.id.lv)
	private ListView listView;
	
	@ViewInject(R.id.gv)
	private GridView gridView;
	
	
	private ImageCacheUtils imageUtils;
	@Override
	public View initView() {
//		TextView text = new TextView(ctx);
//		text.setText("组图页面");
		
		imageUtils = new ImageCacheUtils(ctx);
		
		View view = View.inflate(ctx, R.layout.layout_pic, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		// 获取数据，显示内容

		String jsonCache = JsonCacheUtils.getCache(ctx, ItcastUrl.PHOTOS_URL);
		if(jsonCache!=null){
			parseData(jsonCache);
		}
		
		// 发出联网，获得新内容
		
		requestUrl(HttpMethod.GET, ItcastUrl.PHOTOS_URL, null, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String json = responseInfo.result;
				System.out.println(json);
				parseData(json);
				// 更新缓存的json串
				JsonCacheUtils.saveCache(ctx, ItcastUrl.PHOTOS_URL, json);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				printfErrorLog(error);
			}
		});
		
	}

	
	private void parseData(String jsonCache) {
		
		photoBean = new Gson().fromJson(jsonCache, PhotosBean.class);
		
		adapter = new MyAdapter(photoBean.data.news);
		listView.setAdapter(adapter);
		gridView.setAdapter(adapter);
		
	}

	private MyAdapter adapter ;
	
	private class MyAdapter extends CZAdapter<PhotosItem>{

		public MyAdapter(List<PhotosItem> dataList) {
			super(dataList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder vh;
			
			if(convertView == null){
				view = View.inflate(ctx, R.layout.layout_pic_item, null);
				vh = new ViewHolder();
				vh.image = (ImageView) view.findViewById(R.id.image);
				vh.text = (TextView) view.findViewById(R.id.tv);
				view.setTag(vh);
			}else{
				view = convertView;
				vh = (ViewHolder) view.getTag();
			}
			
			// 绑定数据
			
			PhotosItem photoItem = dataList.get(position); // dataList 在 CZAdapter 的构造函数中，赋值
			
			vh.text.setText(photoItem.title);
			// 实际上应由 
//			photoItem.listimage 这个url 获得图片内容，显示出来
			vh.image.setImageResource(R.drawable.news_pic_default);
			
			// 将position 与 iamge 绑定
			vh.image.setTag(position);
			
			imageUtils.display(vh.image,photoItem.listimage);
			
			
			return view;
		}
		
	}
	
	private class ViewHolder{
		ImageView image;
		TextView text ;
	}
	
	
	
	/**
	 * 判断当前显示的样式风格
	 * true 显示listView 风格
	 * false 显示 gridView的风格
	 */
	private boolean isListStyle = true;

	private PhotosBean photoBean;
	
	public void switchShowStyle(ImageButton btn_right) {
		
		
		if(isListStyle){
			btn_right.setImageResource(R.drawable.icon_pic_grid_type);
			// 显示gridView 隐藏listView
			gridView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}else{
			btn_right.setImageResource(R.drawable.icon_pic_list_type);
			// 隐藏gridView 显示listView
			gridView.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			
		}
		// 刷新页面
		if(adapter!=null){
			adapter.notifyDataSetChanged();
		}
			
		isListStyle = !isListStyle;
	}

}
