package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static client.Constants.*;

public class CreateAccount {
	public boolean createAccount(String id, char[] pass, String name, String sex, int age) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		String str_age = String.valueOf(age);
		String sql;
		String format_sql;
		final String strpass = new String(pass);

		final String url = POSTGRES_URL;
		final String user = POSTGRES_USER;
		final String password = POSTGRES_PASSWORD;

		try {
			conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(false);
			System.out.println("do INSERT");
			
			sql = "INSERT INTO users (id, password, name, sex, age) VALUES ('%s','%s','%s','%s',%s)";
			format_sql = String.format(sql, id, strpass, name, sex, str_age);
			stmt = conn.createStatement();
			stmt.executeUpdate(format_sql);
			conn.commit();
			return true;
		} catch (final SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null)
					rset.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}