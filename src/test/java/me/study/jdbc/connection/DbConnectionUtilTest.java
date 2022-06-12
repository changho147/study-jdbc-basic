package me.study.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class DbConnectionUtilTest {

    @Test
    public void connection() throws Exception {
        Connection connection = DbConnectionUtil.getConnection();

        assertThat(connection).isNotNull();
    }

}