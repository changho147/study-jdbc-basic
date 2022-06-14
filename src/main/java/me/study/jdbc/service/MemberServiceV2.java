package me.study.jdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.MemberRepositoryV2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 repository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false);

            bizLogic(conn, fromId, toId, money);

            conn.commit();
        } catch (Exception e) {
            conn.rollback();

            throw new IllegalStateException(e);
        } finally {
            release(conn);
        }
    }

    private void bizLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = repository.findById(conn, fromId);
        Member toMember = repository.findById(conn, toId);

        repository.update(conn, fromId, fromMember.getMoney() - money);
        validate(toMember);
        repository.update(conn, toId, fromMember.getMoney() + money);
    }

    private void validate(Member toMember) {
        if (toMember.getMemberId().equals("ex"))
            throw new IllegalStateException("이체중 예외 발생");
    }

    private void release(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }
}
