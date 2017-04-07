package usercrawl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.LinkedBlockingQueue;

public class getuserid {
	    private String jdbc_driver="com.mysql.jdbc.Driver";
	    private String url="jdbc:mysql://localhost:3306/test";
	    private Connection connection = null;
		private Statement state = null;
		private ResultSet sql;
		LinkedBlockingQueue<Integer> list;
		public getuserid(LinkedBlockingQueue<Integer> list){
			this.list=list;
		}
		public void get_id() throws SQLException{
			  try{
					Class.forName(jdbc_driver);
					connection=DriverManager.getConnection(url,"root","zch123");
	 				state=connection.createStatement();
					}
					catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
			  sql=state.executeQuery("SELECT id FROM `users` LIMIT 0,500");
			  while(sql.next()){
				 try {
					list.put(Integer.parseInt(sql.getString("id")));
				} catch (NumberFormatException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
			}
}
