package me.study.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.MemberRepositoryV3;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Slf4j
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 repository;

    public MemberServiceV3_3(MemberRepositoryV3 repository) {
        this.repository = repository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
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
