package lv.agg.service;

import lv.agg.dto.UserProfileDTO;
import lv.agg.entity.UserEntity;
import lv.agg.repository.UserRepository;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void register(UserProfileDTO userProfileDTO) {
        userRepository.findByEmail(userProfileDTO.getEmail()).ifPresent(e -> {
            throw new RuntimeException("Email already exists");
        });
        UserEntity user = new UserEntity();
        user.setEmail(userProfileDTO.getEmail());
        user.setPassword(MD5Encoder.encode(userProfileDTO.getPassword().getBytes()));
        userRepository.save(user);
    }

}
