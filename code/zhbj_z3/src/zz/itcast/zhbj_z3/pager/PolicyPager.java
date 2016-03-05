package zz.itcast.zhbj_z3.pager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 *政务指南页面
 * @author Administrator
 *
 */
public class PolicyPager extends BasePager {

	public PolicyPager(Context ctx) {
		super(ctx);
	}

	@Override
	public View initView() {
		TextView text = new TextView(ctx);
		
		text.setText("政务指南页面");
		
		return text;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
