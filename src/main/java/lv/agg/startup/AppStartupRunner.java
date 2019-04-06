package lv.agg.startup;

import lv.agg.entity.ServiceEntity;
import lv.agg.entity.UserEntity;
import lv.agg.repository.ServiceRepository;
import lv.agg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements ApplicationRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public void run(ApplicationArguments args) {
        ServiceEntity s = new ServiceEntity();
        s.setName("haircut");
        s.setId(1L);
        serviceRepository.save(s);

        UserEntity merchant = new UserEntity();
        merchant.setEmail("merchant@mail.com");
        merchant.setPassword(passwordEncoder.encode("123"));
        merchant.setUserRole(UserEntity.UserRole.ROLE_MERCHANT);
        merchant.setServices(serviceRepository.findAll());
        userRepository.save(merchant);

        UserEntity customer = new UserEntity();
        customer.setEmail("customer@mail.com");
        customer.setPassword(passwordEncoder.encode("123"));
        customer.setUserRole(UserEntity.UserRole.ROLE_CUSTOMER);
        userRepository.save(customer);

        UserEntity admin = new UserEntity();
        admin.setEmail("admin@mail.com");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setUserRole(UserEntity.UserRole.ROLE_ADMIN);
        userRepository.save(admin);
    }
}
