package org.morgorithm.frames.repository;

import org.morgorithm.frames.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
@SuppressWarnings("unchecked")
public interface MemberRepository extends JpaRepository<Member,Long>, QuerydslPredicateExecutor<Member> {
    @Query("select m,s, s.facility from Member m Left join Status s on s.member=m where m.mno=:mno")
    List<Object[]> getMemberWithStatus(@Param("mno") Long mno);

    @Query("select m, mi, count(mi) from Member m left outer join MemberImage mi on mi.member=m where m.mno=:mno")
    List<Object[]> getMemberWithAll(Long mno);


    @Query("select m, mi, count(mi) from Member m"+ " left outer join MemberImage mi on mi.member=m group by m")
    Page<Object[]> getListPage(Pageable pageable);

    @Query("select m from Member m")
    Page<Object> getMemberList(Pageable pageable);

    @Query("select count(m) from Member m")
    int getMemberNum();

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
    @Query(value="update member set member.mno=@cnt\\:=@cnt+1",nativeQuery = true)
    void reorderKeyId();

    @Modifying
    @Transactional
    @Query(value="ALTER TABLE member AUTO_INCREMENT = 1", nativeQuery = true)
    void initialAutoIncrementToTheLatest();




}
