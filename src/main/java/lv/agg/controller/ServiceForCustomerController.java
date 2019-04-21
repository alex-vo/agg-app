package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.ServiceDTO;
import lv.agg.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Secured("ROLE_CUSTOMER")
@RequestMapping("api/v1/service")
@Slf4j
public class ServiceForCustomerController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public List<ServiceDTO> getAllServices() {
        return serviceService.getServices();
    }
}
