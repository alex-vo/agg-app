package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.configuration.AggregatorAppPrincipal;
import lv.agg.dto.ServiceDTO;
import lv.agg.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/service")
@Slf4j
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @Secured({"ROLE_MERCHANT", "ROLE_CUSTOMER"})
    @GetMapping
    public List<ServiceDTO> getAllServices() {
        return serviceService.getServices();
    }

    @Secured("ROLE_MERCHANT")
    @GetMapping(value = "get", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceDTO> getAvailableServices() {
        Long merchantId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return serviceService.getAvailableServices(merchantId);
    }

    @Secured("ROLE_MERCHANT")
    @PostMapping(value = "set", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setAvailableServices(@RequestBody List<Long> serviceIds) {
        Long merchantId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        serviceService.setAvailableServices(merchantId, serviceIds);
        log.info("Set available services {} for merchant {}", serviceIds, merchantId);
    }

    @Secured("ROLE_MERCHANT")
    @DeleteMapping(value = "remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removeAvailableServices(List<Long> serviceIds) {
        Long merchantId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        serviceService.removeAvailableServices(merchantId, serviceIds);
        log.info("Removed available services {} for merchant {}", serviceIds, merchantId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createService(@RequestBody ServiceDTO serviceDTO) {
        serviceService.createService(serviceDTO);
        log.info("Created a service {}", serviceDTO.getName());
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateService(@PathVariable("id") Long id, ServiceDTO serviceDTO) {
        serviceService.updateService(id, serviceDTO);
        log.info("Updated a service");
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteService(@PathVariable("id") Long id) {
        serviceService.deleteService(id);
        log.info("Deleted a service {}", id);
    }

}
