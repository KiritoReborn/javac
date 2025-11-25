package com.canteen.canteen_system.repository;

import com.canteen.canteen_system.model.User;
import com.canteen.canteen_system.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByName(String name);
    List<User> findByRole(Role role);
    Page<User> findAll(Pageable pageable);
}
