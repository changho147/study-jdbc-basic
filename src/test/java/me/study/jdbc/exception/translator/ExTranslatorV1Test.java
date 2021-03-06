package me.study.jdbc.exception.translator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.exception.MyDbException;
import me.study.jdbc.repository.exception.MyDuplicateKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static me.study.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ExTranslatorV1Test {

    Repository repository;
    Service service;

    @BeforeEach
    public void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    public void duplicateKeySave() {
        service.create("myId");
        service.create("myId");
    }

    @RequiredArgsConstructor
    static class Service {
        private final Repository repository;

        void create(String memberId) {
            try {
                repository.save(new Member(memberId, 0));
                log.info("save id = {}", memberId);
            } catch (MyDuplicateKeyException e) {
                log.info("duplicate key. try again");
                String newKey = generateNewId(memberId);
                log.info("new key = {}", newKey);

                repository.save(new Member(newKey, 0));
            } catch (MyDbException e) {
                log.info("myDbException Error", e);
            }
        }

        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt();
        }
    }

    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "INSERT INTO MEMBER(MEMBER_ID, MONEY) VALUES(?, ?)";
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = dataSource.getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());

                pstmt.executeUpdate();

                return member;
            } catch (SQLException e) {
                if (e.getErrorCode() == 23505)
                    throw new MyDuplicateKeyException(e);

                throw new MyDbException(e);
            } finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(conn);
            }
        }


    }
}
