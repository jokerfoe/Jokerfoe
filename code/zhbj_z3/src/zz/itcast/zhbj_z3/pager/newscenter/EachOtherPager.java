package zz.itcast.zhbj_z3.pager.newscenter;

import zz.itcast.zhbj_z3.bean.NewsCenterBean.NewsCenterItem;
import zz.itcast.zhbj_z3.pager.BasePager;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * 互动页面
 * @author leo
 *
 */
public class EachOtherPager extends BasePager {

	public EachOtherPager(Context ctx, NewsCenterItem newsCenterItem) {
		super(ctx);
	}

	@Override
	public View initView() {
		TextView text = new TextView(ctx);
		text.setText("互动页面");
		return text;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
