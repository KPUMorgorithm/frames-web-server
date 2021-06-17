package org.morgorithm.frames.repository;

import org.morgorithm.frames.entity.Sms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SmsRepository extends JpaRepository<Sms, Long> {
    @Query("select s from Sms s")
    List<Object[]> getAllList();
}
