package com.canteen.canteen_system.mapper;

import com.canteen.canteen_system.model.MenuItem;
import com.canteen.canteen_system.dto.MenuItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    MenuItemDto menuToMenuDto(MenuItem menuItem);
    MenuItem menuDtoToMenu(MenuItemDto menuItemDto);
}
