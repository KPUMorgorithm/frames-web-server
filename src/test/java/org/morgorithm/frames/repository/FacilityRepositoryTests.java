package org.morgorithm.frames.repository;
/*
import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    @Test
    public void testGetFacilityNames(){
        List<Object[]> result=facilityRepository.getFacilityNames();
        String[] bName = new String[10];
        int idx=0;
        for(Object[] a:result){
            System.out.println("name:"+a[0]);
            bName[idx++]=(String)a[0];
        }
        for(String s:bName){
            System.out.println("test b:"+s);
        }
    }


}
*/