package org.morgorithm.frames.repository;

import org.morgorithm.frames.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FacilityRepository extends JpaRepository<Facility,Long> {
    @Query("select count(f) from Facility f")
    int getFacilityNum();

}
