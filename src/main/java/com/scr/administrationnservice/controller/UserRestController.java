package com.scr.administrationnservice.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scr.administrationnservice.dto.UserDTO;
import com.scr.administrationnservice.entities.Profile;
import com.scr.administrationnservice.entities.Role;
import com.scr.administrationnservice.entities.User;

import com.scr.administrationnservice.service.Userservice;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
@AllArgsConstructor

public class UserRestController {
    private final Userservice userservice;

    @CrossOrigin
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getroles(){

        return ResponseEntity.ok().body(userservice.getroles());
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getusers(){
        return ResponseEntity.ok().body(userservice.getusers());
    }

    @CrossOrigin
    @PostMapping("/user")
    public ResponseEntity<User> addUser(@RequestBody UserDTO user){

        return ResponseEntity.ok().body(userservice.create(user));
    }

    @CrossOrigin
    @PutMapping("/user/{id}")
    public ResponseEntity<User> update(@PathVariable Long id,@RequestBody UserDTO user){
        return ResponseEntity.ok().body(userservice.update(id, user));
    }

    @CrossOrigin
    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable Long id){
        userservice.deleteuser(id);
    }

    @CrossOrigin
    @GetMapping("/refreshtoken")
    public void refreshtoekn(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String authorisation=request.getHeader("Authorization");
        if(authorisation!=null && authorisation.startsWith("Bearer ")) {
            try {
                String token = authorisation.substring(7);
                Algorithm algorithm = Algorithm.HMAC256("mysecret123");
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                String username = decodedJWT.getSubject();
                User user = userservice.loadbymail(username);
                List<String> authroties=new ArrayList<>();
                Set<Role> role=user.getRoles();
                boolean isadmin=role.stream().anyMatch(role1 -> role1.getName().equals("ADMIN"));
                if(isadmin){
                    authroties.add("ADMIN");
                    String jwt_accesToken = JWT.create()
                            .withSubject(user.getEmail())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("roles", authroties)
                            .sign(algorithm);

                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("acces_token", jwt_accesToken);
                    tokens.put("refresh_token", authorisation.substring(7));
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(),tokens);
                }
                else{
                    Profile profile=user.getProfile();
                    profile.getAuthorities().forEach(role1 -> {
                        authroties.add((role1.getName()));
                    });
                    String jwt_accesToken = JWT.create()
                            .withSubject(user.getEmail())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("roles", authroties)
                            .sign(algorithm);

                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("acces_token", jwt_accesToken);
                    tokens.put("refresh_token", authorisation.substring(7));
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(),tokens);
                }

            } catch (Error e) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        else{
            throw new RuntimeException("refresh toekn dosnt existe");
        }
    }



}
