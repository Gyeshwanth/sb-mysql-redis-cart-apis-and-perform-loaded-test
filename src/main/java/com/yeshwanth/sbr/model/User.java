package com.yeshwanth.sbr.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private String imageUrl;

}
