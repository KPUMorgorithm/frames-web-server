package org.morgorithm.frames.repository;


import org.morgorithm.frames.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status,Long> {
}
