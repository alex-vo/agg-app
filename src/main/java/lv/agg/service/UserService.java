package lv.agg.service;

import lv.agg.configuration.AggregatorAppPrincipal;
import lv.agg.dto.JwtAuthenticationResponse;
import lv.agg.dto.UserProfileDTO;
import lv.agg.dto.mapping.UserProfileDTOMapper;
import lv.agg.entity.UserEntity;
import lv.agg.repository.ServiceRepository;
import lv.agg.repository.UserRepository;
import lv.agg.security.JwtTokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ServiceRepository serviceRepository;

    public UserProfileDTO getProfile() {
        Long currentUserId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return getProfile(currentUserId);
    }

    public UserProfileDTO getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserProfileDTOMapper.map(user);
    }

    public void register(UserProfileDTO userProfileDTO) {
        userRepository.findByEmail(userProfileDTO.getEmail()).ifPresent(e -> {
            throw new RuntimeException("Email already exists");
        });
        UserEntity user = new UserEntity();
        setFieldValues(user, userProfileDTO, true);
        userRepository.save(user);
    }

    public void updateProfile(UserProfileDTO userProfileDTO) {
        Long merchantId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UserEntity user = userRepository.getOne(merchantId);
        setFieldValues(user, userProfileDTO, false);
        userRepository.save(user);
    }

    private void setFieldValues(UserEntity user, UserProfileDTO userProfileDTO, boolean setPassword) {
        user.setEmail(userProfileDTO.getEmail());
        if (setPassword) {
            user.setPassword(passwordEncoder.encode(userProfileDTO.getPassword()));
        }
        user.setUserRole(userProfileDTO.getUserRole());
        if (UserEntity.UserRole.ROLE_MERCHANT.equals(userProfileDTO.getUserRole())) {
            user.setMerchantType(userProfileDTO.getIsCompany() ? UserEntity.MerchantType.COMPANY : UserEntity.MerchantType.INDIVIDUAL);
            if (userProfileDTO.getIsCompany()) {
                user.setCompanyName(userProfileDTO.getCompanyName());
            }
        }
        user.setAddress(userProfileDTO.getAddress());
        user.setFirstName(userProfileDTO.getFirstName());
        user.setLastName(userProfileDTO.getLastName());
        if (CollectionUtils.isEmpty(userProfileDTO.getServiceIds())) {
            user.setServices(null);
        } else {
            user.setServices(serviceRepository.findByIdIn(userProfileDTO.getServiceIds()));
        }
    }

    public JwtAuthenticationResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String refreshToken = RandomStringUtils.randomAlphanumeric(255);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken((AggregatorAppPrincipal) authentication.getPrincipal());
        return new JwtAuthenticationResponse(accessToken, refreshToken);
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
