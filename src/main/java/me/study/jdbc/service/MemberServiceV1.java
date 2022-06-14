package me.study.jdbc.service;

import lombok.RequiredArgsConstructor;
import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.MemberRepositoryV1;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 repository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
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
