package com.scr.administrationnservice.service;

import com.scr.administrationnservice.dto.ProfileDTO;
import com.scr.administrationnservice.entities.Authority;
import com.scr.administrationnservice.entities.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProfileService {
    Profile getProfileById(Long profileId);
    public Profile createProfile(ProfileDTO profileDTO);
    public Page<ProfileDTO> getProfiles(Pageable pageable);
    public List<Profile>  getAllProfiles();
    public void updateProfile(Long id , ProfileDTO profileDTO);
    public void deleteProfile(Long profileId);
    public Profile getProfileByName(String name);
    public Authority createauthority(Authority authority);
    public List<Authority> getallauthorities();

}
