package me.study.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static me.study.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class MemberServiceV3_3Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepositoryV3 repository;

    @Autowired
    private MemberServiceV3_3 service;

    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepositoryV3 repository() {
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 service() {
            return new MemberServiceV3_3(repository());
        }
    }


    @AfterEach
    public void afterEach() throws SQLException {
        repository.delete(MEMBER_A);
        repository.delete(MEMBER_B);
        repository.delete(MEMBER_EX);
    }

    @Test
    public void aopCheck() throws Exception {
        log.info("memberService class={}", service.getClass());
        log.info("memberRepository class={}", repository.getClass());

        assertThat(AopUtils.isAopProxy(service)).isTrue();
        assertThat(AopUtils.isAopProxy(repository)).isFalse();
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