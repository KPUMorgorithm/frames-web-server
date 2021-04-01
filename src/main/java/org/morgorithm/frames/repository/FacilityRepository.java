package org.morgorithm.frames.repository;

import org.morgorithm.frames.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility,Long> {
}
