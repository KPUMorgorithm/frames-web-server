package org.morgorithm.frames.repository;


import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status,Long>, QuerydslPredicateExecutor<Status> {
    @Query("select s.facility, count(s.state) from Status s  group by s.facility, s.state")
    List<Object[]> getFacilityInInfo();

    @Query("select s.facility from Status s where s.member.mno=:mno")
    List<Object> getMemberFacility(Long mno);

    @Query("select s.regDate from Status s where s.member.mno=:mno")
    List<Object> getRegDate(Long mno);




}