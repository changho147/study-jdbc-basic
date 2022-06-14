package me.study.jdbc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.MemberRepositoryV3;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 repository;

    public void accountTransfer(String fromId, String toId, int money) {
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            bizLogic(fromId, toId, money);

            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw new IllegalStateException(e);
        }
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
