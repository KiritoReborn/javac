package com.canteen.canteen_system.controller;

import com.canteen.canteen_system.dto.MenuItemDto;
import com.canteen.canteen_system.mapper.MenuItemMapper;
import com.canteen.canteen_system.model.MenuItem;
import com.canteen.canteen_system.service.MenuService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@AllArgsConstructor
public class MenuController {
    private final MenuService menuService;
    private final MenuItemMapper menuItemMapper;

    @GetMapping
    public List<MenuItem> getMenu() {
        return menuService.getMenu();
    }

    @GetMapping("/{itemname}")
    public ResponseEntity<MenuItemDto> getMenuItemByName(@PathVariable String itemname) {
        return menuService.getMenuItemByName(itemname)
                .map(menuItemMapper::menuToMenuDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<MenuItemDto> getMenuItemById(@PathVariable Long id) {
        return menuService.getMenuItemById(id)
                .map(menuItemMapper::menuToMenuDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<MenuItemDto> addMenuItem(@RequestBody MenuItemDto dto) {
        MenuItem saved = menuService.addMenuItem(dto);
        return ResponseEntity.ok(menuItemMapper.menuToMenuDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<MenuItemDto> updateMenuItem(
            @PathVariable Long id,
            @RequestBody MenuItemDto dto) {
        return menuService.updateMenuItem(id, dto)
                .map(menuItemMapper::menuToMenuDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Staff')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        return menuService.deleteMenuItem(id) ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
    }
}
