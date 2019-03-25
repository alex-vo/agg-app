package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.ServiceDTO;
import lv.agg.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/service")
@Secured("ROLE_SERVICE_PROVIDER")
@Slf4j
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public List<ServiceDTO> getAllServices() {
        return serviceService.getServices();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createService(ServiceDTO serviceDTO) {
        serviceService.createService(serviceDTO);
        log.info("Created a service {}", serviceDTO.getName());
    }

    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateService(@PathVariable("id") Long id, ServiceDTO serviceDTO) {
        serviceService.updateService(id, serviceDTO);
        log.info("Updated a service");
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteService(@PathVariable("id") Long id) {
        serviceService.deleteService(id);
        log.info("Deleted a service {}", id);
    }

}
