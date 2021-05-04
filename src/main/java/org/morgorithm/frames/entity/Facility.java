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
@ToString
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String building;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval=true)
    @Builder.Default
    private List<Device> devices = new ArrayList<>();
}
