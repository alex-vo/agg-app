package lv.agg.service;

import lv.agg.configuration.AggregatorAppPrincipal;
import lv.agg.dto.JwtAuthenticationResponse;
import lv.agg.dto.UserProfileDTO;
import lv.agg.entity.UserEntity;
import lv.agg.repository.UserRepository;
import lv.agg.security.JwtTokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider tokenProvider;

    public void register(UserProfileDTO userProfileDTO) {
        userRepository.findByEmail(userProfileDTO.getEmail()).ifPresent(e -> {
            throw new RuntimeException("Email already exists");
        });
        UserEntity user = new UserEntity();
        user.setEmail(userProfileDTO.getEmail());
        user.setPassword(MD5Encoder.encode(userProfileDTO.getPassword().getBytes()));
        userRepository.save(user);
    }

    public JwtAuthenticationResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String refreshToken = RandomStringUtils.randomAlphanumeric(255);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        String token = tokenProvider.generateAccessToken((AggregatorAppPrincipal) authentication.getPrincipal());
        return new JwtAuthenticationResponse(token, refreshToken);
    }

    public JwtAuthenticationResponse refreshTokens(String oldRefreshToken) {
        UserEntity user = userRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new RuntimeException(String.format("User with refresh token %s not found", oldRefreshToken)));
        String accessToken = tokenProvider.generateAccessToken(new AggregatorAppPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name()))
        ));
        String refreshToken = RandomStringUtils.randomAlphanumeric(255);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

}
