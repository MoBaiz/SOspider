package usercrawl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.tuple.Pair;


public class loopqueue {
private   LinkedBlockingQueue<socket> queue=new LinkedBlockingQueue<socket>();
public synchronized socket pop(){
	 socket temp=queue.poll();
	 queue.offer(temp);
	 return temp;
}
public void add(String ip,int port){
	socket temp=new socket(ip,port);
	queue.offer(temp);
}
public String toString(){
	return queue.toString();
}
}
class socket{
    private String ip;
    private int port;
    private int count=0;
	socket(String ip,int port){
		this.ip=ip;
		this.port=port;
	}
	public String get_ip(){
		return ip;
	}
	public int port(){
		return port;
	}
	public int get_falsecount(){
		return count;
	}
	public void cleanfasle(){
		count=0;
	}
	public String toString(){
		return ip+":"+port+"\n";
	}
}