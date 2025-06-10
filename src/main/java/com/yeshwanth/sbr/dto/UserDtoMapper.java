package com.yeshwanth.sbr.dto;

import com.yeshwanth.sbr.model.User;




public class UserDtoMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setImageUrl(user.getImageUrl());
        return userDto;
    }

}
