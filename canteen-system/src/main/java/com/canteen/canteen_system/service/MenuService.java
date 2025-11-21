package com.canteen.canteen_system.service;

import com.canteen.canteen_system.dto.MenuItemDto;
import com.canteen.canteen_system.mapper.MenuItemMapper;
import com.canteen.canteen_system.model.MenuItem;
import com.canteen.canteen_system.repository.MenuRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuItemMapper menuItemMapper;

    public List<MenuItem> getMenu() {
        return menuRepository.findAll();
    }

    public Optional<MenuItem> getMenuItemByName(String itemname) {
        return Optional.ofNullable(menuRepository.findByItemname(itemname));
    }

    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuRepository.findById(id);
    }

    public MenuItem addMenuItem(MenuItemDto dto) {
        MenuItem item = menuItemMapper.menuDtoToMenu(dto);
        return menuRepository.save(item);
    }

    public Optional<MenuItem> updateMenuItem(Long id, MenuItemDto dto) {
        return menuRepository.findById(id)
                .map(existing -> {
                    MenuItem updated = menuItemMapper.menuDtoToMenu(dto);
                    updated.setId(id);
                    return menuRepository.save(updated);
                });
    }

    public boolean deleteMenuItem(Long id) {
        if (menuRepository.existsById(id)) {
            menuRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}