package xk.crawler.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBFactory {
	final static String DRIVER= "com.mysql.jdbc.Driver";
	final static String URL = "jdbc:mysql://localhost:3306/";
	final static String DB = "bookshop";
	final static String username = "root";
	final static String password = "xiongkui";
	
	static {
		try {
			Class.forName(DRIVER);
		} catch (Exception e) {
			System.out.println("Error load driver");
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL+DB, username, password);
		} catch (Exception e) {
			System.out.println("Error get connection");
			e.printStackTrace();
		}
		
		return conn;
	}
}
