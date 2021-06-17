package org.morgorithm.frames.service;

import org.junit.jupiter.api.Test;
import org.morgorithm.frames.entity.Status;
import org.morgorithm.frames.projection.AccessSet;
import org.morgorithm.frames.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccessSetServiceTest {
    @Autowired
    private AccessSetService accessSetService;

    @Test
    void getStatusOverlapped() {
        List<AccessSet> accessSets = accessSetService.getAllAccessSet(null);
        List<Status> statuses = accessSetService.getStatusOverlapped(accessSets.get(0));
        System.out.println(statuses.toString());
    }
}