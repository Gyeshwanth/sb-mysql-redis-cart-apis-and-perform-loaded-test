package com.yeshwanth.sbr.dto;

import com.yeshwanth.sbr.model.CartEntry;


import java.util.ArrayList;
import java.util.Collection;


public class CartEntryDtoMapper {

   //source to target cartEntry TO CartEntryDto
   public static Collection<CartEntryDto> mapToCartEntryDto(Collection<CartEntry> cartEntrys) {

       Collection<CartEntryDto> cartEntryDtos = new ArrayList<>();

       for (CartEntry cartEntry :cartEntrys){
           CartEntryDto cartEntryDto = new CartEntryDto();
           cartEntryDto.setId(cartEntry.getId());
           cartEntryDto.setQuantity(cartEntry.getQuantity());
           cartEntryDto.setPrice(cartEntry.getPrice());
           cartEntryDto.setProductCode(cartEntry.getProduct().getCode());
           cartEntryDto.setProductName(cartEntry.getProduct().getName());
           cartEntryDtos.add(cartEntryDto);
       }
       return cartEntryDtos;

   }

}
