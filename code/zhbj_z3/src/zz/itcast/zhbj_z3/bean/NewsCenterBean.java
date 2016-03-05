package zz.itcast.zhbj_z3.bean;

import java.util.List;

public class NewsCenterBean {

	public int retcode;

	public List<NewsCenterItem> data;
	public List<String> extend;

	public class NewsCenterItem {
		public List<NewsChildren> children;
		public String id;
		public String title;
		public String type;
		public String url;
		public String url1;
		public String dayurl;
		public String excurl;
		public String weekurl;
	}

	public class NewsChildren {
		public String id;
		public String title;
		public String type;
		public String url;
	}

}
