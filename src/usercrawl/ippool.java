package usercrawl;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class ippool{
	private Map<String,Integer> ip_table=new HashMap<String,Integer>();
	private loopqueue useable_ip_table=new loopqueue();
	private StringBuffer save=new StringBuffer();
	public boolean test_ip(String ip,int port){
        String href="http://stackoverflow.com/"; 
		try {
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
		URL url = new URL(href);
		HttpURLConnection urlcon = (HttpURLConnection)url.openConnection(proxy);
		urlcon.setConnectTimeout(5000);
		urlcon.setReadTimeout(5000);
        if(urlcon.getResponseCode()==HttpURLConnection.HTTP_OK)
        	return true;
        else return false;
		} catch (Exception e) {
	//	e.printStackTrace();
		}
        return false;
		}
	public void request_ip(int count){           //获取new ip
		URL url;
		try {
			url = new URL("http://tvp.daxiangdaili.com/ip/?tid=557056205049095&num=100&operator=1&delay=1&category=2&filter=on");
			HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
			InputStream is = urlcon.getInputStream();  
			BufferedReader buffer = new BufferedReader(new InputStreamReader(is));  
			String l = null;  
			while((l=buffer.readLine())!=null){  
				String[] ip=new String[2];
            	ip=l.split(":");
            	ip_table.put(ip[0],Integer.parseInt(ip[1]));
		    }  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void save(String path,String data){
		try{
		      File file =new File(path);
		      //if file doesnt exists, then create it
		      if(!file.exists()){
		       file.createNewFile();
		      }

		      //true = append file
		      FileWriter writer = new FileWriter(file); 
		      // 向文件写入内容
		      writer.write(data); 
		      writer.flush();
		      writer.close();
		            
		     }catch(IOException e){
		      e.printStackTrace();
		     }
	}
	public Map<String,Integer> readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            System.out.println(fileName);
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	String[] ip=new String[2];
            	ip=tempString.split(":");
            	ip_table.put(ip[0],Integer.parseInt(ip[1]));
              
             //   line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return ip_table;
    }
	private void clean(){
		Iterator<Entry<String, Integer>> it = ip_table.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			String key = entry.getKey();
			Integer value = entry.getValue();
			if(test_ip(key,value)){
			useable_ip_table.add(key,value);
			save.append(key+":"+value+"\n");
			System.out.println("true key:"+key);
			}
			else System.out.println("false key:"+key);
			
	}
	}
	public loopqueue get_userable_ip(){
		request_ip(50);
		readFileByLines("j:/true.txt");
		clean();
		save("j://true.txt",save.toString());
		return useable_ip_table;
	}
	public loopqueue getipbydb(int count){
		try{  
			//连接SQLite的JDBC  
			Class.forName("org.sqlite.JDBC");  
			//建立一个数据库名zieckey.db的连接，如果不存在就在当前目录下创建之  
			Connection conn =DriverManager.getConnection("jdbc:sqlite:/j:/IPProxyPool-master/data/proxy.db");  
			Statement stat = conn.createStatement();   
			ResultSet rs = stat.executeQuery("select * from proxys order by score DESC;");//查询数据  
			while(rs.next()){//将查询到的数据打印出来  
		    String ip=rs.getString("ip");
		    int port=rs.getInt("port");
			//System.out.print("ip = "+ rs.getString("ip")+" ");//列属性一  
			//System.out.println("port = "+ rs.getString("port"));//列属性二  		
		 //   System.out.println(ip+":"+port+" "+test_ip(ip, port));
		    useable_ip_table.add(ip, port);
			}  
			rs.close();  
			conn.close();//结束数据库的连接  
			}  
			catch(Exception e ){  
			e.printStackTrace();  
			}  
		return useable_ip_table;
	   }
}
