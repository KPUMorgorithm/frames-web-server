package org.morgorithm.frames.repository;

import org.morgorithm.frames.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device,Long> {
    List<Device> findByBno(Long bno);
}
