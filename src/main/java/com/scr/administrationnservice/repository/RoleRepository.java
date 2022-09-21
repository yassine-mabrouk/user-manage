package com.scr.administrationnservice.repository;


import com.scr.administrationnservice.entities.Role;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String ROLE);
}
