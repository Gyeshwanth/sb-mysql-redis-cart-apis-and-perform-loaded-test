package com.yeshwanth.sbr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartEntryDto {
    private Long id;
    private Integer quantity;
    private Double price;
    private String productCode;
    private String productName;
}
