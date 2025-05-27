package nl.devcraft.cb.resourcemanager;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLClient {

  private final MysqlDataSource mysqlDataSource;

  MySQLClient(String url, String user, String password, String database) {
    mysqlDataSource = new MysqlDataSource();
    mysqlDataSource.setURL(url);
    mysqlDataSource.setUser(user);
    mysqlDataSource.setPassword(password);
    mysqlDataSource.setDatabaseName(database);
  }

  public void execute(String sql) {
    try (var conn = mysqlDataSource.getConnection()) {
        //noinspection SqlSourceToSinkFlow
        conn.createStatement().execute(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void execute(InputStream is) {
    try {
      this.execute(new String(is.readAllBytes()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ResultSet select(String sql) {
    try (var conn = mysqlDataSource.getConnection()) {
        //noinspection SqlSourceToSinkFlow
        return conn.createStatement().executeQuery(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
