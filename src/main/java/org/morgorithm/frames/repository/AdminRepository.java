package org.morgorithm.frames.repository;

import org.morgorithm.frames.entity.Admin;
import org.morgorithm.frames.entity.MemberImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByUsername(String username);
}
