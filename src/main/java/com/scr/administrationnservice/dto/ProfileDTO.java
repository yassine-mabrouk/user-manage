package com.scr.administrationnservice.dto;

import com.scr.administrationnservice.entities.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class ProfileDTO {
    private Long id;

    private String name;

    private Set<String> authorities;

    public ProfileDTO() {
    }

}
