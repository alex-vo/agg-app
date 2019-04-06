package lv.agg.controller;

import lv.agg.dto.AppointmentDTO;
import lv.agg.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/merchant/appointment")
public class MerchantAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Secured("ROLE_MERCHANT")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppointmentDTO> searchMerchantAppointments(
            @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo
    ) {
        return appointmentService.searchMerchantAppointments(dateFrom, dateTo);
    }

    @Secured("ROLE_MERCHANT")
    @PutMapping("{appointmentId}/confirm")
    public void confirmAppointment(@PathVariable("appointmentId") Long appointmentId) {
        appointmentService.confirmAppointment(appointmentId);
    }

    @Secured("ROLE_MERCHANT")
    @PutMapping("{appointmentId}/decline")
    public void declineAppointment(@PathVariable("appointmentId") Long appointmentId) {
        appointmentService.declineAppointment(appointmentId);
    }

    @Secured("ROLE_MERCHANT")
    @PutMapping("{appointmentId}/cancel")
    public void cancelAppointment(@PathVariable("appointmentId") Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
    }
}
