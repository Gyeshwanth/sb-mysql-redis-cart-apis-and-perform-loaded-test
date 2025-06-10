package com.yeshwanth.sbr.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartSummary {

    private Long cartId;
    private Collection<CartEntryDto> entrys;
    private BigDecimal totalPrice;
    private int totalQuantity;
    private UserDto user;


}
