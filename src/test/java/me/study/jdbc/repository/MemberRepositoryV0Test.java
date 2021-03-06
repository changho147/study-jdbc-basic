package me.study.jdbc.repository;

import me.study.jdbc.domain.Member;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    public void crud() throws Exception {
        Member member = new Member("member5", 10000);
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