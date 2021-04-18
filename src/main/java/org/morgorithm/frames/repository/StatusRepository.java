package org.morgorithm.frames.repository;


import org.apache.tomcat.jni.Local;
import org.morgorithm.frames.entity.Member;
import org.morgorithm.frames.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.Timestamp;
public interface StatusRepository extends JpaRepository<Status,Long>, QuerydslPredicateExecutor<Status> {
    @Query("select s.facility, count(s.state), s.facility.bno, s.state from Status s  group by s.facility, s.state")
    List<Object[]> getFacilityInInfo();

    @Query("select s.facility, count(s.state), s.facility.bno, s.state, s.regDate from Status s WHERE (s.regDate >= :timeFrom AND s.regDate < :timeTo) group by s.facility, s.state order by s.facility.bno")
    List<Object[]> getFacilityInInfoOneDay(@Param("timeFrom") LocalDateTime fromTime, @Param("timeTo") LocalDateTime timeTo);


    @Query("select s.facility from Status s where s.member.mno=:mno")
    List<Object> getMemberFacility(Long mno);

    @Query("select s.regDate, s.state from Status s where s.member.mno=:mno")
    List<Object[]> getRegDateAndState(Long mno);

    @Query("select s.member.mno from Status s")
    List<Object> getAllMemberMno();

    @Modifying
    @Transactional
    @Query(value="SET SQL_SAFE_UPDATES = 0", nativeQuery = true)
    void setSafeUpdate();


    @Modifying
    @Transactional
    @Query(value="set @cnt=0", nativeQuery = true)
    void initialCnt();


    @Modifying
    @Transactional
    @Query(value="update status set status.statusnum=@cnt\\:=@cnt+1",nativeQuery = true)
    void reorderKeyId();

    @Modifying
    @Transactional
    @Query(value="ALTER TABLE status AUTO_INCREMENT = 1", nativeQuery = true)
    void initialAutoIncrementToTheLatest();




}
