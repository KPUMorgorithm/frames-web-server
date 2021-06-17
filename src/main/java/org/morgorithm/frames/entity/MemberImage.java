package org.morgorithm.frames.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "member")
public class MemberImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inum;
    private String uuid;
    private String imgName;
    private String path;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
