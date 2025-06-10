package com.yeshwanth.sbr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @Column(nullable = false, updatable = false, name="code")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String code;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String Category;


}
