package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.UserProfileDTO;
import lv.agg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Secured("ROLE_MERCHANT")
@RequestMapping("api/v1/profile")
@Slf4j
public class MyProfileController {

    @Autowired
    private UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UserProfileDTO getMyProfile(){
        return userService.getProfile();
    }

    @PutMapping
    public void updateProfile(@RequestBody @Valid UserProfileDTO userProfileDTO) {
        userService.updateProfile(userProfileDTO);
        log.info("Updated profile {}", userProfileDTO.getEmail());
    }

}
