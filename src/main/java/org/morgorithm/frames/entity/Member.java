package org.morgorithm.frames.entity;


import lombok.*;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mno;

    private String name;

    private String phone;
    public void changeName(String name){
        this.name=name;
    }
    public void changePhone(String phone){
        this.phone=phone;
    }
}
