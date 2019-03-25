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
public class AggregatorStartup implements ApplicationRunner {

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
        serviceRepository.saveAndFlush(s);

        UserEntity serviceProvider = new UserEntity();
        serviceProvider.setEmail("service_provider@mail.com");
        serviceProvider.setPassword(passwordEncoder.encode("123"));
        serviceProvider.setUserRole(UserEntity.UserRole.ROLE_SERVICE_PROVIDER);
        serviceProvider.setServices(serviceRepository.findAll());
        userRepository.saveAndFlush(serviceProvider);

        UserEntity customer = new UserEntity();
        customer.setEmail("customer@mail.com");
        customer.setPassword(passwordEncoder.encode("123"));
        customer.setUserRole(UserEntity.UserRole.ROLE_CUSTOMER);
        userRepository.saveAndFlush(customer);
    }
}
