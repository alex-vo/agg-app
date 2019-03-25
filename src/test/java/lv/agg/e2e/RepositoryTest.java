package lv.agg.e2e;

import lv.agg.entity.ServiceEntity;
import lv.agg.entity.UserEntity;
import lv.agg.repository.ServiceRepository;
import lv.agg.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ServiceRepository serviceRepository;

    @Test
    public void adsdasd() {
//        ServiceEntity s = new ServiceEntity();
//        s.setName("aaa");
//        serviceRepository.saveAndFlush(s);
//
//        UserEntity u = new UserEntity();
//        u.setUsername("vasja");
//        u.setPassword("asd");
//        u.setServices(serviceRepository.findAll());
//        userRepository.saveAndFlush(u);

        UserEntity serviceProvider = new UserEntity();
        serviceProvider.setEmail("service_provider@mail.com");
        serviceProvider.setPassword("123");
        serviceProvider.setUserRole(UserEntity.UserRole.ROLE_SERVICE_PROVIDER);
        userRepository.saveAndFlush(serviceProvider);

        UserEntity customer = new UserEntity();
        customer.setEmail("customer@mail.com");
        customer.setPassword("123");
        customer.setUserRole(UserEntity.UserRole.ROLE_CUSTOMER);
        userRepository.saveAndFlush(customer);

        ServiceEntity s = new ServiceEntity();
        s.setName("haircut");
        s.setId(1L);
        s.setUsers(userRepository.findAll());
        serviceRepository.saveAndFlush(s);

        UserEntity sp = userRepository.findUserWithServicesByEmail("service_provider@mail.com").get();
        sp.getServices()
                .forEach(serviceEntity -> {
                    System.out.println(serviceEntity.getId() + " " + serviceEntity.getName());
                });
        ServiceEntity hc = serviceRepository.findASDdasdasdsad("haircut").get();
        System.out.println(hc.getName() + " " + hc.getId());
        hc.getUsers()
                .forEach(userEntity -> {
                    System.out.println(userEntity.getId() + " " + userEntity.getEmail());
                });
    }

}
