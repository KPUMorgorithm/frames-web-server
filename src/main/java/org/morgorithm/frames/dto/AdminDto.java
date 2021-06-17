package org.morgorithm.frames.dto;

import lombok.*;
import org.morgorithm.frames.entity.Admin;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {
    private Long id;
    private String username;
    private String password;
    private String passwordRpt;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Admin toEntity() {
        return Admin.builder()
                .id(id)
                .username(username)
                .password(password)
                .build();
    }
}
