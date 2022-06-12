package me.study.jdbc.repository;

import me.study.jdbc.domain.Member;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    public void crud() throws Exception {
        Member member = new Member("member1", 10000);
        Member savedMember = repository.save(member);

        assertThat(savedMember).isEqualTo(member);
    }

}