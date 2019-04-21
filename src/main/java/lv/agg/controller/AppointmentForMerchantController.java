package lv.agg.controller;

import lv.agg.dto.AppointmentDTO;
import lv.agg.enums.AppointmentStatus;
import lv.agg.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@Secured("ROLE_MERCHANT")
@RequestMapping("api/v1/merchant/appointment")
public class AppointmentForMerchantController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AppointmentDTO> searchMerchantAppointments(
            @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo
    ) {
        return appointmentService.searchMerchantAppointments(dateFrom, dateTo);
    }

    @RequestMapping(value = "{appointmentId}", method = RequestMethod.PATCH)
    public void updateAppointmentStatus(@PathVariable("appointmentId") Long appointmentId,
                                        @RequestBody Map<String, String> updates) {
        AppointmentStatus newAppointmentStatus = AppointmentStatus.valueOf(updates.get("status"));
        appointmentService.updateAppointmentStatus(appointmentId, newAppointmentStatus);
    }
}
