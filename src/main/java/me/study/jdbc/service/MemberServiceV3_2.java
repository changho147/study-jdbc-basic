package me.study.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.MemberRepositoryV3;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

@Slf4j
public class MemberServiceV3_2 {
    private final TransactionTemplate transactionTemplate;
    private final MemberRepositoryV3 repository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 repository) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.repository = repository;
    }

    public void accountTransfer(String fromId, String toId, int money) {
        transactionTemplate.executeWithoutResult((transactionStatus) -> {
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);

        repository.update(fromId, fromMember.getMoney() - money);
        validate(toMember);
        repository.update(toId, fromMember.getMoney() + money);
    }

    private void validate(Member toMember) {
        if (toMember.getMemberId().equals("ex"))
            throw new IllegalStateException("이체중 예외 발생");
    }
}
