package me.study.jdbc.service;

import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.MemberRepositoryV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

import static me.study.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceV2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV2 repository;
    private MemberServiceV2 service;

    @BeforeEach
    public void beforeEach() {
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new MemberRepositoryV2(dataSource);
        service = new MemberServiceV2(dataSource, repository);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        repository.delete(MEMBER_A);
        repository.delete(MEMBER_B);
        repository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상이체")
    public void accountTransferTest() throws Exception {
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);

        repository.save(memberA);
        repository.save(memberB);

        service.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        Member fromMember = repository.findById(memberA.getMemberId());
        Member toMember = repository.findById(memberB.getMemberId());

        assertThat(fromMember.getMoney()).isEqualTo(8000);
        assertThat(toMember.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    public void accountTransferException() throws Exception {
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);

        repository.save(memberA);
        repository.save(memberEx);

        assertThatThrownBy(() -> service.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000)).isInstanceOf(IllegalStateException.class);

        Member fromMember = repository.findById(memberA.getMemberId());
        Member toMember = repository.findById(memberEx.getMemberId());

        assertThat(fromMember.getMoney()).isEqualTo(10000);
        assertThat(toMember.getMoney()).isEqualTo(10000);

    }
}