package org.morgorithm.frames.repository;

import org.morgorithm.frames.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility,Long> {
    @Query("select count(f) from Facility f")
    int getFacilityNum();
    @Query("select f.building from Facility f")
    List<Object[]> getFacilityNames();
}
