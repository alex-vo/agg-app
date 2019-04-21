package lv.agg.controller;

import lv.agg.dto.AppointmentDTO;
import lv.agg.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@Secured("ROLE_CUSTOMER")
@RequestMapping("api/v1/customer/appointment")
public class AppointmentForCustomerController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createAppointment(@RequestBody AppointmentDTO appointmentDTO,
                                            UriComponentsBuilder uriComponentsBuilder) {
        Long appointmentId = appointmentService.createAppointment(appointmentDTO);
        return ResponseEntity.created(uriComponentsBuilder.path("api/v1/customer/appointment/{id}")
                .buildAndExpand(appointmentId).toUri()).build();
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AppointmentDTO getCustomerAppointmentById(@PathVariable("id") Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppointmentDTO> searchCustomerAppointments(
            @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo
    ) {
        return appointmentService.searchCustomerAppointments(dateFrom, dateTo);
    }

}
