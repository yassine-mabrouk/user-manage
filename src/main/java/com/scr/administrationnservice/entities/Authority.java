package com.scr.administrationnservice.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@Entity
@Table(name = "AUTHORITY")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 50)
    private String name;

    public Authority(){}

    public Authority(String name){
        this.name = name;
    }
    public Authority(Long id  , String name){
        this.name = name;
        this.id= id ;
    }


}
