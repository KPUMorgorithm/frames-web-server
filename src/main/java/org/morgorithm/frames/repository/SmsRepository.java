package org.morgorithm.frames.repository;


import org.morgorithm.frames.entity.Sms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsRepository extends JpaRepository<Sms,Long> {
}
