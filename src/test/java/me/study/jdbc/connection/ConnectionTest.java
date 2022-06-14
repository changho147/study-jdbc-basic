package me.study.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

import static me.study.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    public void driverManager() throws Exception {
        Connection conn1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection conn2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        log.info("conn1={}", conn1);
        log.info("conn2={}", conn2);
    }

    @Test
    public void dataSourceDriverManager() throws Exception {
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        useDataSource(dataSource);
    }

    @Test
    public void dataSourceConnectionPool() throws Exception {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
//        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        Thread.sleep(1000);
    }


    private void useDataSource(DataSource dataSource) throws Exception {
        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();

        log.info("conn1={}", conn1);
        log.info("conn2={}", conn2);
    }
}
