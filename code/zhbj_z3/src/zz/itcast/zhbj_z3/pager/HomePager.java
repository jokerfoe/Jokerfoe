package zz.itcast.zhbj_z3.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * 首页面
 * @author Administrator
 *
 */
public class HomePager extends BasePager {

	public HomePager(Context ctx) {
		super(ctx);
	}

	@Override
	public View initView() {
		TextView text = new TextView(ctx);
		
		text.setText("首页");
		
		return text;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
