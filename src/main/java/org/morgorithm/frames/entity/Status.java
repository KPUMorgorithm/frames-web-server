package org.morgorithm.frames.entity;

import com.sun.istack.Nullable;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude={"member","facility"})
public class Status extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusnum;

    @ManyToOne(fetch=FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch=FetchType.LAZY)
    private Facility facility;


    private Boolean state;
}
