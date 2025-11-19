package com.canteen.canteen_system.controller;

import com.canteen.canteen_system.model.MenuItem;
import com.canteen.canteen_system.repository.MenuRepository;
import com.canteen.canteen_system.repository.OrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MenuController {
    public MenuController(OrderRepository orderRepository) {}

//    @GetMapping("/menu")
//    public List<MenuItem> getMenu() {
//        return MenuRepository;
//    }
}

