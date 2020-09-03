package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static client.Constants.*;

public class Login {
    boolean doLogin(String id, char[] pass) {
        if (id.length() == 0) {
            return false;
        }
        if (pass.length == 0) {
            return false;
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        String sql;
        String format_sql;
        String sqlPass = "";
        String strPass = new String(pass);
        String user_name = "";

        final String url = POSTGRES_URL;
        final String user = POSTGRES_USER;
        final String password = POSTGRES_PASSWORD;

        try {
            // PostgreSQLへ接続
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            sql = "SELECT password FROM users WHERE id='%s'";
            format_sql = String.format(sql, id);
            System.out.println(format_sql);
            rset = stmt.executeQuery(format_sql);
            // SELECT結果の受け取り
            while (rset.next()) {
                sqlPass = rset.getString(1).trim();
            }
            conn.commit();

            if (rset != null)
                rset.close();

            // user name取得
            if (strPass.equals(sqlPass)) {
                sql = "SELECT name FROM users WHERE id ='%s' AND password = '%s'";
                format_sql = String.format(sql, id, strPass);
                System.out.println(format_sql);

                rset = stmt.executeQuery(format_sql);
                // SELECT結果の受け取り
                while (rset.next()) {
                    user_name = rset.getString(1).trim();
                }
                conn.commit();
                System.out.println("user_name: " + user_name);

                return true;
            } else {
                return false;
            }

        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        } catch (final NumberFormatException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                strPass = "0";
                sqlPass = "0";
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
    }

}