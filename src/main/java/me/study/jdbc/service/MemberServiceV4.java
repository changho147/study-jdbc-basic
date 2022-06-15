package me.study.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import me.study.jdbc.domain.Member;
import me.study.jdbc.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class MemberServiceV4 {
    private final MemberRepository repository;

    public MemberServiceV4(MemberRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) {
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
