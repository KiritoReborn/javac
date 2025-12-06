package com.canteen.canteen_system.mapper;

import com.canteen.canteen_system.dto.LoginDto;
import com.canteen.canteen_system.dto.UpdateDto;
import com.canteen.canteen_system.dto.UserDto;
import com.canteen.canteen_system.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDto userToUserDto(User user);
    LoginDto loginToLoginDto(User user);
    void updateFromDto(UpdateDto updateDto, @MappingTarget User user);
}

