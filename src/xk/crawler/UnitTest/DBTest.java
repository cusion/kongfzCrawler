package xk.crawler.UnitTest;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import xk.crawler.utils.DBFactory;

public class DBTest {
	DBFactory dbf = new DBFactory();
	@Test
	public void testConnection() {
		String sql = "insert into book value('a', 'b', 'c', 'd', 'e','f')";
		Connection conn = dbf.getConnection();
		try {
			Statement stmt = conn.createStatement();
			int cnt = stmt.executeUpdate(sql);
			if (cnt < 1) {
				System.out.println("Fail");
			} else {
				System.out.println("Success");
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Create and Execute statement error!");
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
