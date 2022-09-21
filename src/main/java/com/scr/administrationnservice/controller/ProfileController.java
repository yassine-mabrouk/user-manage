package com.scr.administrationnservice.controller;

import com.scr.administrationnservice.dto.ProfileDTO;
import com.scr.administrationnservice.entities.Authority;
import com.scr.administrationnservice.entities.Profile;
import com.scr.administrationnservice.service.ProfileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {
    @Autowired
    public   ProfileService profileService;

    private final Logger log = LoggerFactory.getLogger("customLogger");
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }



    @GetMapping ("/authorities")
    public List<Authority> getallauthroties(){
        return profileService.getallauthorities();
    }
    @GetMapping("")
    public  List<Profile> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        log.info("Request to create profile ");
        try{
            Profile profile = profileService.createProfile(profileDTO);
            log.info("Profile {} has been created successfully ",profile.getId());
        }catch(Exception e){
            log.error("Failed to create profile by ");
            throw e;
        }
    }


    @PutMapping(path = "/{id}")
    public void updateProfile(@RequestBody ProfileDTO profileDTO , @PathVariable  Long id){
        log.info("Request to update profile {} ",profileDTO.getId());
        try{
            profileService.updateProfile(id, profileDTO);
            log.info("Profile {} has been updated successfully ",profileDTO.getId());
        }catch(Exception e){
            log.error("Failed to update profile {} ",profileDTO.getId());
            throw e;
        }
    }

    @DeleteMapping(path = "/{profileId}")
    public void deleteProfile(@PathVariable(name = "profileId") Long profileId) {
        log.info("Request to delete profile {}",profileId);
        try{
            profileService.deleteProfile(profileId);
            log.info("Profile {} has been created successfully by {}");
        }catch(Exception e){
            log.error("Failed to create profile ");
            throw e;
        }
    }

    @GetMapping(path = "/byName/{name}")
    public Profile getProfileByName(@PathVariable String name) {
        return profileService.getProfileByName(name);
    }
    @GetMapping(path = "/{id}")
    public Profile getProfile(@PathVariable Long id ) {
        return profileService.getProfileById(id);
    }
}
