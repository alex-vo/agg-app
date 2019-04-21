package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.TimeSlotDTO;
import lv.agg.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Secured("ROLE_CUSTOMER")
@RequestMapping("api/v1/availability")
@Slf4j
public class AvailabilityForCustomerController {

    @Autowired
    private AvailabilityService availabilityService;

    @GetMapping
    public List<TimeSlotDTO> findTimeSlots(
            @RequestParam("serviceId") Long serviceId,
            @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateFrom,
            @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTo
    ) {
        //TODO location
        return availabilityService.findAvailabiltySlots(serviceId, dateFrom, dateTo)
                .asRanges()
                .stream()
                .map(r -> {
                    TimeSlotDTO dto = new TimeSlotDTO();
                    dto.setDateFrom(r.lowerEndpoint());
                    dto.setDateTo(r.upperEndpoint());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
