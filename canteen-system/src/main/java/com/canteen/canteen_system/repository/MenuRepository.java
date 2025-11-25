package com.canteen.canteen_system.repository;

import com.canteen.canteen_system.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuItem,Long> {
    MenuItem findByItemname(String itemName);
    List<MenuItem> findByCategory(String category);
    List<MenuItem> findByAvailableTrue();
}
