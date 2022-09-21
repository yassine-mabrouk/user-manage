package com.scr.administrationnservice.service;

import com.scr.administrationnservice.dto.ProfileDTO;
import com.scr.administrationnservice.entities.Authority;
import com.scr.administrationnservice.entities.Profile;
import com.scr.administrationnservice.exceptions.APIException;
import com.scr.administrationnservice.repository.AuthorityRepository;
import com.scr.administrationnservice.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProfileServiceImpli implements  ProfileService {
    @Autowired
    private  ProfileRepository profileRepository;
    @Autowired
    private AuthorityRepository authorityRepository;


    public Profile getProfileById(Long profileId) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if(!profile.isPresent()) throw new APIException("Profile does not exist with this Id :"+profileId);
        return profile.get();
    }

    public Profile createProfile(ProfileDTO profileDTO) {
        Profile findbyname = profileRepository.findByName(profileDTO.getName());
        if (findbyname != null) {
            throw new IllegalStateException("already existe");
        } else {
            log.debug("Request to create new profile: {}");
            Profile profile = new Profile();
            Set<Authority> authorities = new HashSet<>();
            profile.setName(profileDTO.getName());
            if (profileDTO.getAuthorities() != null) {
                for (String name : profileDTO.getAuthorities()) {
                    Authority authority = authorityRepository.findByName(name);
                    authorities.add(authority);
                }
            }
            profile.setAuthorities(authorities);
            return profileRepository.save(profile);
        }
    }

    private ProfileDTO toDTO(Profile profile) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(profile.getId());
        profileDTO.setName(profile.getName());
        profileDTO.setAuthorities(profile.getAuthorities().stream()
            .map(Authority::getName)
            .collect(Collectors.toSet()));

        return profileDTO;
    }

//    private Profile toEntity(ProfileDTO profileDTO) {
//        Profile profile = new Profile();
//        Set<Authority> authorities= new HashSet<>();
//        profile.setId(profileDTO.getId());
//        profile.setName(profileDTO.getName());
//        if (profileDTO.getAuthorities()!=null ) {
//            for (String name   : profileDTO.getAuthorities()) {
//                 Authority authority = authorityRepository.findByName(name);
//                 authorities.add(authority);
//            }
//        }
//        profile.setAuthorities(authorities);
//        return profile;
//    }

    public Page<ProfileDTO> getProfiles(Pageable pageable) {
        log.debug("Request to get a page of profile");
        return profileRepository.findAll(pageable)
            .map(this::toDTO);
    }

    public List<Profile> getAllProfiles() {
        log.debug("Request to get a page of profiles");

        return profileRepository.findAll();
    }

    public void updateProfile(Long id , ProfileDTO profileDTO) {
        Profile findbyname = profileRepository.findByName(profileDTO.getName());
        if (findbyname != null) {
            throw new IllegalStateException("already existe");
        } else {


            log.debug("Request to update a profile");
            Optional<Profile> profile = profileRepository.findById(id);
            if (!profile.isPresent()) throw new APIException("Profile not exit with this Id :" + id);
            Profile profileEntity = profile.get();

            Set<Authority> authorities = new HashSet<>();
            profileEntity.setName(profileDTO.getName());
            if (profileDTO.getAuthorities() != null) {
                for (String name : profileDTO.getAuthorities()) {
                    Authority authority = authorityRepository.findByName(name);
                    authorities.add(authority);
                }
            }
            profileEntity.setAuthorities(authorities);
            profileRepository.save(profileEntity);
        }
    }

    public void deleteProfile(Long profileId) {
        log.debug("Request to delete a profile by id - {}", profileId);
        this.profileRepository.deleteById(profileId);
    }

    public Profile getProfileByName(String name){
        log.debug("Request to search a profile by name - {}", name);
        Profile profile = profileRepository.findByName(name);
        if(profile == null) throw new APIException("Profile does not exist with this name  :"+name);
        return profile;
    }

    @Override
    public Authority createauthority(Authority authority) {
        if(authorityRepository.findByName(authority.getName())!=null){
            throw new IllegalStateException("already existe");
        }
        else{
            return authorityRepository.save(authority);
        }
    }

    @Override
    public List<Authority> getallauthorities() {
        return authorityRepository.findAll();
    }


}
