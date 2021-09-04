package org.keumann.domain.member.repository;

import org.keumann.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("memberRepository")
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Page<Member> findByName(String name, Pageable pageable);

    boolean existsByEmail(String email);

}