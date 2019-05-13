package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.ServiceDTO;
import lv.agg.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@Secured("ROLE_ADMIN")
@RequestMapping("api/v1/admin/service")
@Slf4j
public class ServiceForAdminController {

    @Autowired
    private ServiceService serviceService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createService(@RequestBody ServiceDTO serviceDTO) {
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
