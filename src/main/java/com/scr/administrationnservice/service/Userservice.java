package com.scr.administrationnservice.service;

import com.scr.administrationnservice.dto.UserDTO;
import com.scr.administrationnservice.entities.Profile;
import com.scr.administrationnservice.entities.Role;
import com.scr.administrationnservice.entities.User;
import com.scr.administrationnservice.repository.ProfileRepository;
import com.scr.administrationnservice.repository.RoleRepository;
import com.scr.administrationnservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class Userservice implements UserServiceInterface, UserDetailsService {

       private final RoleRepository rolerepository;
       private final UserRepository userRepository;
       private ProfileRepository profileRepository;
    private final BCryptPasswordEncoder BCryptPasswordEncoder;


       public User toentity(UserDTO user){
           User newuser = new User();
           newuser.setId(user.getId());
           newuser.setPassword((user.getPassword()));
           newuser.setCreatedDate(new Date());
           newuser.setResetDate(new Date());
           newuser.setEmail(user.getEmail().trim().toLowerCase());
           newuser.setActivated(user.isActivated());
           newuser.setFirstName(user.getFirstName());
           newuser.setLastName((user.getLastName()));
           Set<Role> roles=new HashSet<>();
           for(String role:user.getRoles()){
               Role newrole=rolerepository.findByName(role);
               roles.add(newrole);
               System.out.println(newrole);
           }
           Profile profile =profileRepository.findByName(user.getProfile());
           newuser.setProfile(profile);
           newuser.setRoles(roles);
           return newuser;
       }

    @Override
    @Transactional
    public User create(UserDTO user) {
           String email=user.getEmail();
        Optional<User> finduser=userRepository.findByEmail(email);
        if(finduser.isPresent()){
            throw new IllegalStateException("already in use");
        }
        else{

            user.setPassword(BCryptPasswordEncoder.encode(user.getPassword()));
            return userRepository.save(toentity(user));
        }
    }

    @Override
    @Transactional
    public User update(Long id,UserDTO user) {
     Optional<User> newuser=userRepository.findById(id);
      if(!newuser.isPresent()){
          throw new IllegalStateException("user with"+id+"doesnt exist");
      }
      User realuser= newuser.get();
      if(user.getPassword()!=null && user.getPassword().length()>0){
          realuser.setPassword(BCryptPasswordEncoder.encode(user.getPassword()));
      }
      else{
          realuser.setPassword(realuser.getPassword());
      }
      if(user.getEmail()!=null && user.getEmail().length()>0){
          Optional<User> finduserbymail=userRepository.findByEmail(user.getEmail());
          if(finduserbymail.isPresent()){
              throw new IllegalStateException("email already in use");
          }
          else{
              realuser.setEmail(user.getEmail());
          }
      }
      else{
          realuser.setEmail(realuser.getEmail());
      }
      if(user.getLastName()!=null && user.getLastName().length()>0){
          realuser.setLastName(user.getLastName());
      }
      else{
          realuser.setLastName(realuser.getLastName());
      }
      if(user.getFirstName()!=null && user.getFirstName().length()>0) {
          realuser.setFirstName(user.getFirstName());

      }
      else{
          realuser.setFirstName(realuser.getFirstName());
      }
      if(user.getProfile()!=null && user.getProfile().length()>0){
          realuser.setProfile(profileRepository.findByName(user.getProfile()));
      }
      else{
          realuser.setProfile(realuser.getProfile());
      }
      if(user.getRoles()!=null && !user.getRoles().isEmpty()){
          Set<Role> roles=new HashSet<>();
          for(String role:user.getRoles()){
              Role newrole=rolerepository.findByName(role);
              roles.add(newrole);
              System.out.println(newrole);
          }
          realuser.setRoles(roles);
      }
      else{
          realuser.setRoles(realuser.getRoles());
      }
      return userRepository.save(realuser);
    }


    @Override
    public void deleteuser(Long id) {
        log.debug("Request to delete a profile by id - {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public Role createRole(Role role) {
           if(rolerepository.findByName(role.getName())!=null){
               throw new IllegalStateException("already existe");
           }
           else{
               return rolerepository.save(role);
           }

    }


    @Override
    public List<User> getusers() {
        return userRepository.findAll();
    }
    @Override
    public List<Role> getroles(){
        return rolerepository.findAll();
    }

    @Override
    public User loadbymail(String email) {
        return userRepository.loadbymail(email);
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepository.loadbymail(username);
        if(user==null){
            throw new UsernameNotFoundException("user not found");
        }
        else {
            log.info("user {} found in the database",user);
        }
        Collection<SimpleGrantedAuthority> authroties=new ArrayList<>();
        Set<Role> role=user.getRoles();
        boolean isadmin=role.stream().anyMatch(role1 -> role1.getName().equals("ADMIN"));
        if(isadmin){

            authroties.add(new SimpleGrantedAuthority("ADMIN"));
        }
        else{
            Profile profile=user.getProfile();
            profile.getAuthorities().forEach(role1 -> {
                authroties.add(new SimpleGrantedAuthority(role1.getName()));
            });
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                authroties
        );
    }

}
