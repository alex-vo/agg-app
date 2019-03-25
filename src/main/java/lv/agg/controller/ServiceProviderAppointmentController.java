package lv.agg.controller;

import lv.agg.dto.AppointmentDTO;
import lv.agg.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@Secured("ROLE_SERVICE_PROVIDER")
@RequestMapping("api/v1/serviceprovider/appointment")
public class ServiceProviderAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppointmentDTO> searchServiceProviderAppointments(
            @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo
    ) {
        return appointmentService.searchServiceProviderAppointments(dateFrom, dateTo);
    }

    @PostMapping("{appointmentId}/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public void confirmAppointment(@PathVariable("appointmentId") Long appointmentId) {
        appointmentService.confirmAppointment(appointmentId);
    }
}
