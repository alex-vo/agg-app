package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.JwtAuthenticationResponse;
import lv.agg.dto.UserProfileDTO;
import lv.agg.security.JwtTokenProvider;
import lv.agg.service.UserService;
import lv.agg.validator.UserProfileDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/user")
@Slf4j
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProfileDTOValidator userProfileDTOValidator;
    @Autowired
    private JwtTokenProvider tokenProvider;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(userProfileDTOValidator);
    }

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signin(@RequestParam("username") String username,
                                 @RequestParam("password") String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
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
