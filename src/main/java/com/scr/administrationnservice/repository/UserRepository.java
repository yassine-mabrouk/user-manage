package com.scr.administrationnservice.repository;

import com.scr.administrationnservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);


    @Query("select u from User u where u.email=?1")
    User loadbymail(String email);

}
