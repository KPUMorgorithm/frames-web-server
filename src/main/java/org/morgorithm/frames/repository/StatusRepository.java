package org.morgorithm.frames.repository;


import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.projection.AccessSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface StatusRepository extends JpaRepository<Status,Long>, QuerydslPredicateExecutor<Status> {
    @Query("select s.state from Status s where s.member.mno=:mno")
    List<Object> getFacilityState(Long mno);


    @Query("select s.facility, count(s.state), s.facility.bno, s.state from Status s  group by s.facility, s.state")
    List<Object[]> getFacilityInInfo();

    @Query("select s.facility, count(s.state), s.facility.bno, s.state, s.regDate from Status s WHERE (s.regDate >= :timeFrom AND s.regDate < :timeTo) group by s.facility, s.state order by s.facility.bno")
    List<Object[]> getFacilityInInfoOneDay(@Param("timeFrom") LocalDateTime fromTime, @Param("timeTo") LocalDateTime timeTo);


    @Query("select distinct s.facility from Status s where s.member.mno=:mno") // unique 값들만 받아오도록 수정
    List<Object> getMemberFacility(Long mno);

    @Query("select distinct s.facility from Status s where s.member.mno=:mno and (s.regDate >= :timeFrom AND s.regDate < :timeTo)") // unique 값들만 받아오도록 수정
    List<Object> getMemberFacility(Long mno, @Param("timeFrom") LocalDateTime fromTime, @Param("timeTo") LocalDateTime timeTo);

    @Query("select s.regDate, s.state, s.facility.bno from Status s where s.member.mno=:mno")
    List<Object[]> getRegDateAndState(Long mno);

    @Query("select s.regDate, s.state, s.facility.bno from Status s where s.member.mno=:mno and (s.regDate >= :timeFrom AND s.regDate < :timeTo)")
    List<Object[]> getRegDateAndState(Long mno, @Param("timeFrom") LocalDateTime fromTime, @Param("timeTo") LocalDateTime timeTo);

    @Query("select s.regDate, s.state, s.facility.bno from Status s where s.member.mno=:mno and s.facility.bno = :bno")
    List<Object[]> getRegDateAndState(Long mno, Long bno);

    @Query("select s.regDate, s.state, s.facility.bno from Status s where s.member.mno=:mno and (s.regDate >= :timeFrom AND s.regDate < :timeTo) and s.facility.bno = :bno")
    List<Object[]> getRegDateAndState(Long mno, Long bno, @Param("timeFrom") LocalDateTime fromTime, @Param("timeTo") LocalDateTime timeTo);

    @Query("select s.member.mno from Status s")
    List<Object> getAllMemberMno();

    @Query("select s.statusnum from Status s ORDER BY s.statusnum DESC")
    List<Object> getMaxStatusNum();

    @Query("select MAX(s.regDate) from Status s")
    List<Object> getLatestDate();

    @Query("select s.regDate from Status s where s.member.mno=:mno")
    List<Object> getAllRegDate(Long mno);

    @Query("select s.regDate, s.temperature from Status s where s.member.mno=:mno order by s.regDate ASC, s.temperature ASC")
    List<Object[]> getMemberDailyTemperatureStatus(Long mno);

    @Query(value = "select s from Status s where s.statusnum > :lastStatusNum order by s.statusnum desc")
    List<Status> findRecentStatusList(Long lastStatusNum);

    Status findTopByOrderByStatusnumDesc();

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


    // --------------------시공간 질의------------------------
    @Query(value = "select s1.member_mno memberId, s1.facility_bno facilityId, s1.temperature statusEnterId, s2.temperature statusLeaveId, s1.statusnum temperatureEnter, s2.statusnum temperatureLeave, s1.regdate timeEnter, s2.regdate timeLeave " +
            "from status s1 " +
            "left join status s2 " +
            "on s2.statusnum = (" +
            "select s3.statusnum " +
            "from status s3 " +
            "where s1.facility_bno = s3.facility_bno " +
            "and s1.member_mno = s3.member_mno " +
            "and s1.state != s3.state " +
            "order by abs(datediff(s1.regdate, s3.regdate)) asc " +
            "limit 1 " +
            ") " +
            "where s1.state = 0", nativeQuery = true)
    List<AccessSet> getAllAccessSet();

    @Query(value = "select s1.member_mno memberId, s1.facility_bno facilityId, s1.temperature statusEnterId, s2.temperature statusLeaveId, s1.statusnum temperatureEnter, s2.statusnum temperatureLeave, s1.regdate timeEnter, s2.regdate timeLeave " +
            "from status s1 " +
            "left join status s2 " +
            "on s2.statusnum = (" +
            "select s3.statusnum " +
            "from status s3 " +
            "where s1.facility_bno = s3.facility_bno " +
            "and s1.member_mno = s3.member_mno " +
            "and s1.state != s3.state " +
            "order by abs(datediff(s1.regdate, s3.regdate)) asc " +
            "limit 1 " +
            ") " +
            "where s1.state = 0 and s1.member_mno = :memberId", nativeQuery = true)
    List<AccessSet> getAccessSetByMemberId(Long memberId);

    @Query(value = "select * from status s where s.facility_bno = #{accessSet.facilityId} and s.regdate between #{accessSet.timeEnter} and #{accessSet.timeLeave}", nativeQuery = true)
    List<Object[]> getStatusOverlapped(@Param("accessSet") AccessSet accessSet); // TODO: 고쳐야함
    // -----------------------------------------------------

}
