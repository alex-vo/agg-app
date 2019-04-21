package lv.agg.controller;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.AvailabilitySlotDTO;
import lv.agg.service.AvailabilityService;
import lv.agg.validator.AvailabilityDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Secured("ROLE_MERCHANT")
@RequestMapping("api/v1/availability")
@Slf4j
public class AvailabilityForMerchantController {

    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private AvailabilityDTOValidator availabilityDTOValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(availabilityDTOValidator);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAvailability(@RequestBody @Valid AvailabilitySlotDTO availabilitySlotDTO) {
        availabilityService.createAvailability(availabilitySlotDTO);
    }

    @PutMapping("{id}")
    public void updateAvailability(@PathVariable("id") Long availabilityId,
                                   @RequestBody @Valid AvailabilitySlotDTO availabilitySlotDTO) {
        availabilityService.updateAvailability(availabilityId, availabilitySlotDTO);
    }

    @DeleteMapping("{id}")
    public void deleteAvailability(@PathVariable("id") Long availabilityId) {
        availabilityService.deleteAvailabitiy(availabilityId);
    }


}
