package lv.agg.service;

import lv.agg.dto.ServiceDTO;
import lv.agg.dto.mapping.ServiceDTOMapper;
import lv.agg.entity.ServiceEntity;
import lv.agg.entity.UserEntity;
import lv.agg.repository.ServiceRepository;
import lv.agg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private UserRepository userRepository;

    public List<ServiceDTO> getServices() {
        return serviceRepository.findAll().stream()
                .map(ServiceDTOMapper::map)
                .collect(Collectors.toList());
    }

    public void createService(ServiceDTO serviceDTO) {
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setName(serviceDTO.getName());
        serviceRepository.save(serviceEntity);
    }

    public void updateService(Long id, ServiceDTO serviceDTO) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        service.setName(serviceDTO.getName());
        serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    public List<ServiceDTO> getAvailableServices(Long merchantId) {
        UserEntity user = userRepository.getOne(merchantId);
        return user.getServices().stream()
                .map(ServiceDTOMapper::map)
                .collect(Collectors.toList());
    }

    public void setAvailableServices(Long merchantId, List<Long> serviceIds) {
        UserEntity user = userRepository.findById(merchantId)
                .orElseThrow(RuntimeException::new);
        user.setServices(serviceRepository.findByIdIn(serviceIds));
        userRepository.save(user);
    }

    public void removeAvailableServices(Long merchantId, List<Long> serviceIds) {
        UserEntity user = userRepository.findById(merchantId)
                .orElseThrow(RuntimeException::new);
        List<ServiceEntity> services = user.getServices();
        user.setServices(services.stream()
                .filter(e -> serviceIds.contains(e.getId()))
                .collect(Collectors.toList()));
        userRepository.save(user);
    }

}
