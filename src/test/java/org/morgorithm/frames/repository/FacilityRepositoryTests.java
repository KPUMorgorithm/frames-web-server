package org.morgorithm.frames.repository;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@SpringBootTest
public class FacilityRepositoryTests {
    @Autowired
    private FacilityRepository facilityRepository;

    @Commit
    @Transactional
    @Test
    public void insertFacilities(){
        IntStream.rangeClosed(1,10).forEach(i->{
            Facility facility=Facility.builder().building("building"+i).build();
            facilityRepository.save(facility);
        });
    }


}
