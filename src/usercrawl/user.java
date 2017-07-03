package usercrawl;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class user {

	public static void main(String args[]) {
		String url2 = "https://api.stackexchange.com/2.0/questions?key=X4bDsjJ)39KjZX1p895B0Q((&pagesize=1&site=stackoverflow&tagged=xpages&order=desc&sort=creation&page=";
		ippool pool = new ippool();
		loopqueue q = pool.getipbydb(100);  //?????IP??
		//ExecutorService theard_pool = Executors.newFixedThreadPool(100);
		for(int i=0;i<100;i++){
			new pagedown(q, url2, list);
		}
	}
}

class Global {
	public static int count = 0;
	public static int S_count = 0;
}
