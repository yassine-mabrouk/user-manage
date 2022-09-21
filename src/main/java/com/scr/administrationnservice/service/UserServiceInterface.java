package com.scr.administrationnservice.service;

import com.scr.administrationnservice.dto.UserDTO;
import com.scr.administrationnservice.entities.Role;
import com.scr.administrationnservice.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserServiceInterface {

    User create(UserDTO user);
    User update(Long id,UserDTO user);
    void deleteuser(Long id);

    Role createRole(Role role);
    List<User> getusers();
    List<Role> getroles();


    User loadbymail(String email);



}
