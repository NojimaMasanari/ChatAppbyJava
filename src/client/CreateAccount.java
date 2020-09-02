package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateAccount {
    public boolean createAccount(String id, char[] pass, String name, String sex, int age) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		String str_age = String.valueOf(age);
		String sql;
		final String strpass = new String(pass);

		// 接続文字列 String url = "jdbc:postgresql://<接続先DBサーバのIP or
		// ホスト名>:<DBのポート番号>/<DB名>";
		final String url = "jdbc:postgresql://localhost:5432/chat_application";
		final String user = "postgres";
		final String password = "postgres";

		try {
			// PostgreSQLへ接続
			conn = DriverManager.getConnection(url, user, password);
			// 自動コミットOFF
			conn.setAutoCommit(false);
			// INSERT文の実行
			System.out.println("do INSERT");
			sql = "INSERT INTO users (id, password, name, sex, age) VALUES ('" + id + "','" + strpass + "','" + name
					+ "','" + sex + "'," + str_age + ")";
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
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