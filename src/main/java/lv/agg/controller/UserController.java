package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.UserProfileDTO;
import lv.agg.service.UserService;
import lv.agg.validator.UserProfileDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProfileDTOValidator userProfileDTOValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(userProfileDTOValidator);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid UserProfileDTO userProfileDTO) {
        userService.register(userProfileDTO);
        log.info("Created profile {}", userProfileDTO.getEmail());
    }

    @PutMapping
    public void updateProfile(UserProfileDTO userProfileDTO) {
        log.info("Updated profile");
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@PathVariable("page") Long id) {
        log.info("Deleted profile");
    }

}
