package com.scr.administrationnservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FilterConfig extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public FilterConfig (AuthenticationManager authenticationManager){
        this.authenticationManager=authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        StringBuffer jb = new StringBuffer();
        String line = null;
        String username=null;
        String password=null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
        } catch (Exception e) {
            System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRROR:");
            System.out.println(e);
        }

        try {
            JSONObject jsonObject =new JSONObject(jb.toString());
            password = (String) jsonObject.get("password");;
            username=(String) jsonObject.get("username");
            // will return price value.
        } catch (JSONException e) {

        }
        log.info("username is :{}",username);
        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(username,
                password);
        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("authenticated");
        User user=(User) authResult.getPrincipal();
        Algorithm algorithm=Algorithm.HMAC256("mysecret123");
        String jwt_accesToken=JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+15*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",user.getAuthorities().stream().map(autoritie -> autoritie.getAuthority()
                ).collect(Collectors.toList()))
                .sign(algorithm);
        String jwt_refreshToken= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+24*60*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        Map<String,String> tokens=new HashMap<>();
        tokens.put("acces_token",jwt_accesToken);
        tokens.put("refresh_token",jwt_refreshToken);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(),tokens);

    }
}
