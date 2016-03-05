package zz.itcast.zhbj_z3.bean;

import java.util.List;

public class NewsCenterItemBean {

	public int retcode;
	
	public  NewsCenterItemData   data;
	
	public class NewsCenterItemData{
		
		public String countcommenturl;
		public String more;
		public String title;
		
		public List<News> news;
		public List<Topic> topic;
		public List<Topnews> topnews;
		
	}
	
	public class News{
		public boolean comment;
		public String commentlist;
		public String commenturl;
		public String id;
		public String listimage;
		public String pubdate;
		public String title;
		public String type;
		public String url;
	}
	
	public class Topic{
		public String description;
		public String id;
		public String listimage;
		public String sort;
		public String title;
		public String url;
		
	}
	
	public class Topnews{
		public boolean comment;
		public String commentlist;
		public String commenturl;
		public String id;
		public String pubdate;
		public String title;
		public String topimage;
		public String type;
		public String url;
	}
	
	
}
