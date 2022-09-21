package com.scr.administrationnservice;

import com.scr.administrationnservice.dto.ProfileDTO;
import com.scr.administrationnservice.dto.UserDTO;
import com.scr.administrationnservice.entities.Authority;
import com.scr.administrationnservice.entities.Role;


import com.scr.administrationnservice.repository.AuthorityRepository;

import com.scr.administrationnservice.service.ProfileService;
import com.scr.administrationnservice.service.Userservice;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class AdministrationnServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdministrationnServiceApplication.class, args);
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(Userservice userservice, AuthorityRepository authorityRepository, ProfileService profileService) {
        return args -> {
            try {


                userservice.createRole(new Role(null, "ADMIN"));
                userservice.createRole(new Role(null, "USER"));
                profileService.createauthority(new Authority(null, "RC_quote"));
                profileService.createauthority(new Authority(null, "SL_qoute"));
                profileService.createauthority(new Authority(null, "NP_quote"));
                Set<String> autority = new HashSet<>();
                autority.add("RC_quote");
                autority.add("SL_qoute");
                autority.add("NP_quote");

                Set<String> roles = new HashSet<>();
                roles.add("ADMIN");
                profileService.createProfile(new ProfileDTO(null, "admin", autority));
                ;
                userservice.create(new UserDTO(null, "admin", "admin", "admin@scr.com", "1234", roles, true, null));
            }
            catch  (Exception e){
                System.out.println(e);
            }



        };

    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000");
            }
        };
    }
}
