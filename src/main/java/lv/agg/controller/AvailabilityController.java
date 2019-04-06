package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.AvailabilitySlotDTO;
import lv.agg.dto.TimeSlotDTO;
import lv.agg.service.AvailabilityService;
import lv.agg.validator.AvailabilityDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/availability")
@Slf4j
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private AvailabilityDTOValidator availabilityDTOValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(availabilityDTOValidator);
    }

    @Secured("ROLE_CUSTOMER")
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

    @Secured("ROLE_MERCHANT")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAvailability(@RequestBody @Valid AvailabilitySlotDTO availabilitySlotDTO) {
        availabilityService.createAvailability(availabilitySlotDTO);
    }

    @Secured("ROLE_MERCHANT")
    @PutMapping("{id}")
    public void updateAvailability(@PathVariable("id") Long availabilityId, @RequestBody @Valid AvailabilitySlotDTO availabilitySlotDTO) {
        availabilityService.updateAvailability(availabilityId, availabilitySlotDTO);
    }

    @Secured("ROLE_MERCHANT")
    @DeleteMapping("{id}")
    public void deleteAvailability(@PathVariable("id") Long availabilityId) {
        availabilityService.deleteAvailabitiy(availabilityId);
    }

}
