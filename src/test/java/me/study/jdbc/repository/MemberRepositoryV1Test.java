package me.study.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import me.study.jdbc.connection.ConnectionConst;
import me.study.jdbc.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.NoSuchElementException;

import static me.study.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    public void beforeEach() {
        // DriverManager <= 항상 새로운 커넥션 획득
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // ConnectionPool
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    public void crud() throws Exception {
        Member member = new Member("member1", 10000);
        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        assertThat(findMember.getMemberId()).isEqualTo(member.getMemberId());

        repository.update(findMember.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        repository.delete(updatedMember.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }

}