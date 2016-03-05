package zz.itcast.zhbj_z3.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract  class CZAdapter<T> extends BaseAdapter {

	protected List<T> dataList;

	public CZAdapter(List<T> dataList){
		this.dataList = dataList;
	}
	
	
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
