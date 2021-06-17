package org.morgorithm.frames.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"devices"})
public class Facility {
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private final List<Device> devices = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;
    private String building;
}
