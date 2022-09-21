package com.scr.administrationnservice.repository;

import com.scr.administrationnservice.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {


    Profile findByName(String  name );
}
