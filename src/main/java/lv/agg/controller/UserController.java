package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.JwtAuthenticationResponse;
import lv.agg.dto.UserProfileDTO;
import lv.agg.service.UserService;
import lv.agg.validator.UserProfileDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JwtAuthenticationResponse login(@RequestParam("username") String username,
                                           @RequestParam("password") String password) {
        return userService.login(username, password);
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JwtAuthenticationResponse refresh(@RequestParam("refreshToken") String refreshToken) {
        return userService.refreshTokens(refreshToken);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid UserProfileDTO userProfileDTO) {
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
