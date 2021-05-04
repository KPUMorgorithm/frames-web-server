package org.morgorithm.frames.scheduler;

import org.morgorithm.frames.store.RegisterURLStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RegisterURLScheduler {
    @Scheduled(initialDelay = 1 * 1000L, fixedDelay = 1 * 1000L)
    public void executeJob() {
        RegisterURLStore.getInstance().tick();
    }
}
