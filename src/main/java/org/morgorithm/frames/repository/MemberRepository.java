package org.morgorithm.frames.repository;

import org.morgorithm.frames.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
