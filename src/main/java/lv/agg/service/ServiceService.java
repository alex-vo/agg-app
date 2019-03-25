package lv.agg.service;

import lv.agg.dto.ServiceDTO;
import lv.agg.entity.ServiceEntity;
import lv.agg.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceDTO> getServices() {
        return serviceRepository.findAll().stream()
                .map(serviceEntity -> {
                    ServiceDTO dto = new ServiceDTO();
                    dto.setName(serviceEntity.getName());
                    return dto;
                }).collect(Collectors.toList());
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

}
