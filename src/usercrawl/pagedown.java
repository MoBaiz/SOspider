package usercrawl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class pagedown extends Thread{
    private String jdbc_driver="com.mysql.jdbc.Driver";
    private String db_url="jdbc:mysql://localhost:3306/so";
    private Connection connection = null;
    private Statement state = null;
    private ResultSet sql;
    private String user_name;
    private int user_id;
    private String img;
    private String current_position;  //company
    private int answer_count;
    private int question_count;
    private String about_me;
    private String icon_location;
    private String icon_github;
    private String icon_site;
    private String icon_history;//created time;
    private int icon_eye;
    private String icon_time;//last seen time;
    private String icon_twitter;
    private int people_reached=0;
    private int glod=0;
    private int sliver=0;
    private int bronze=0;
    private String url;
    private String ip;
    private int port;
    private loopqueue q;
    private LinkedBlockingQueue<Integer> list;
    private boolean flag;
    private int pageid;
    public pagedown(loopqueue q,String url,int pageid){
    	 this.url=url;
    	 this.q=q;
    	 this.list=list;
    	 this.pageid=pageid;
    }
    public void run(){
    	while(true){
        	 socket temp=q.pop();  //代理IP
        	 this.ip=temp.get_ip();
        	 this.port=temp.port();
    		 crawl();		 
    	}	
    }
    private void crawlbyapi() throws IOException{
    	getJsonByHttpUrl(url, ip, port);
    }
    /*private void crawl(){
    	Document doc=null;;
		
			try {
			//	doc = getDocByJsoup2(url+Global.S_count++, ip, port);
				getDocByJsoup2(url+Global.S_count++,"115.182.16.123", 1080);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(doc==null)
				return ;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
		System.out.println("succ+"+Global.count++);
		return;
			//doc = Jsoup.connect(url+user_id).get();
		//Document doc = Jsoup.connect(url).get();
	//	doc.select(".user-card-name").get(0).select(".top-badge").remove().html();
		img=doc.select("#avatar-card > div.avatar > a > div > img").attr("src");
		// TODO 完成图片下载功能
		URL   img_url   =   new   URL(img);         //下载图片
		URLConnection   uc   =   img_url.openConnection(); 
		InputStream   is   =   uc.getInputStream(); 
		File   file   =   new   File( "F://a.jpge"); 
		FileOutputStream   out   =   new   FileOutputStream(file); 
		int   j=0; 
		while   ((j=is.read())!=-1)   { 
		out.write(j); 
		} 
		is.close();
		user_name=doc.select(".user-card-name").text();
	//	System.out.println(user_name);
		Elements temp=doc.select("div.current-position");
		if(temp.size()!=0)
		current_position=doc.select("div.current-position").get(0).text();
		temp=doc.select("div.col-left.col-8.about > div.bio");
		if(temp.size()!=0)
		about_me=doc.select("div.col-left.col-8.about > div.bio").get(0).html().toString();
		answer_count=Integer.parseInt("0"+doc.select("div.stat.answers.col-3 > span").text().replace(",",""));
		question_count=Integer.parseInt("0"+doc.select("div.stat.questions.col-3 > span").text().replace(",",""));                                
		people_reached=phrase_people_reached(doc.select("div.stat.people-helped.col-5 > span").text());
		Elements links=doc.select("div.user-links > ul > li");
		glod=Integer.parseInt("0"+doc.select(" div.badges-number.badge1-alternate > span.number").text().replace(",",""));
		sliver=Integer.parseInt("0"+doc.select("div.badges-number.badge2-alternate > span.number").text().replace(",",""));
		bronze=Integer.parseInt("0"+doc.select("div.badges-number.badge3-alternate > span.number").text().replace(",",""));
		for(int i=0;i<links.size();i++){
			switch (links.get(i).getElementsByAttribute("class").attr("class")) {
			case "icon-eye":
				icon_eye=Integer.parseInt("0"+StringUtils.substringBefore(links.get(i).text().replace(",","")," "));
				//System.out.println(icon_eye);
				break;
		    case "icon-github":
				icon_github=links.get(i).getElementsByAttribute("href").attr("href");
				//System.out.println(icon_github);
				break;
		    case "icon-history":
		    	icon_history=StringUtils.substringBefore(links.get(i).getElementsByAttribute("title").attr("title"),"Z");
		    	//System.out.println(icon_history);
				break;
		    case "icon-location":
				icon_location=links.get(i).text();
				//System.out.println(icon_location);
				break;
		    case "icon-site":
				icon_site=links.get(i).text();
				//System.out.println(icon_site);
				break;
		    case "icon-time":
		    	icon_time=StringUtils.substringBefore(links.get(i).getElementsByAttribute("title").attr("title"),"Z");
		    //	System.out.println(icon_time);
		    	break;
		    case "icon-twitter":
		    	icon_twitter=links.get(i).getElementsByAttribute("href").attr("href");
		    //	System.out.println(icon_twitter);
			default:
				break;
			}
		}
		temp=doc.select("div.view-more");
		if(temp.size()!=0)
		System.out.println(doc.select("div.view-more").get(0).getElementsByAttribute("href").attr("href"));
	    //	System.out.println(icon_twitter););
		System.out.println("succeed"+Global.S_count+++"user_id"+user_id);	
		try{
			Class.forName(jdbc_driver);
			connection=DriverManager.getConnection(db_url,"root","zch123");
				state=connection.createStatement();
			}
			catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
	    try {
			state.execute("insert into user values("+"'"+user_name.replace("'","\'")+"'"+","+user_id+","+"'"+img+"'"+","+"'"+current_position.replace("'","\'")+"'"+","+answer_count+","+question_count+","+"'"+about_me.replace("'","\\'")+"'"+","+"'"+icon_location.replace("'","\'")+"'"+","+"'"+icon_github+"'"+","+"'"+icon_site+"'"+","+"'"+icon_history+"'"+","+icon_eye+","+"'"+icon_time+"'"+","+"'"+icon_twitter+"'"+","+people_reached+","+glod+","+sliver+","+bronze+")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    */
    public String getJsonByHttpUrl(String href,String ip,int port) throws IOException{
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
		URL url = new URL(href);
		HttpURLConnection urlcon = (HttpURLConnection)url.openConnection(proxy);  
		urlcon.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
		urlcon.connect();         //获取连接  
        int response=urlcon.getResponseCode();  //返回错误码
        System.out.println(response);
		InputStream is = urlcon.getInputStream();  
		GZIPInputStream gzis=new GZIPInputStream(is);
		BufferedReader buffer = new BufferedReader(new InputStreamReader(gzis,"utf-8"));  
		StringBuffer bs = new StringBuffer();  
		String l = null;  
		while((l=buffer.readLine())!=null)
           bs.append(l);  
		return bs.toString();
}
    private int phrase_people_reached(String s){
		int num=0;
		s=StringUtils.substringAfter(s,"~");
		if(s.length()==0)
			return 0;
		char ch=s.charAt(s.length()-1);
	 	if(ch=='k'){
	 	s=StringUtils.substringBefore(s,"k");
	 	num=(int)Float.parseFloat(s)*1000;
	 	}
	 	else if(ch=='m'){
	 		s=StringUtils.substringBefore(s,"m");
		 	num=(int)Float.parseFloat(s)*1000000;	
	 	}
	 	else {
			num=Integer.parseInt("0"+s);
		}
		return num;
	}
    private void down_topics(String url,String ip,int port){
    	Document doc;
		try {
			doc = getDocByJsoup(url+user_id, ip, port);
			if(doc==null)
			return ;
		} catch (IOException | MyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		System.out.println("url"+doc.baseUri());
    }
    public Document getDocByJsoup(String href,String ip,int port) throws IOException, MyException{
    	Document doc=null;
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
			URL url = new URL(href);
			HttpURLConnection urlcon = (HttpURLConnection)url.openConnection(proxy);  
	      
			urlcon.connect();         //获取连接  
	        int response=urlcon.getResponseCode();  //返回错误码
	        if(response!=HttpURLConnection.HTTP_OK){
	        	System.out.println("错误码:"+response+" id:"+user_id);
	            throw new MyException("http error:"+response);
	            }
			InputStream is = urlcon.getInputStream();  
	
			BufferedReader buffer = new BufferedReader(new InputStreamReader(is));  
	
			StringBuffer bs = new StringBuffer();  
	
			String l = null;  
	
			while((l=buffer.readLine())!=null)
	           bs.append(l);   
		doc = Jsoup.parse(bs.toString());
		    return doc;
    }
    public boolean hasnext(){
    	return flag;
    }
}