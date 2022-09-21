package com.scr.administrationnservice.repository;


import com.scr.administrationnservice.entities.Authority;
import com.scr.administrationnservice.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByName( String name );
}
