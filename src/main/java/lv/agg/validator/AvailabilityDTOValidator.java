package lv.agg.validator;

import lv.agg.dto.AvailabilitySlotDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AvailabilityDTOValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return AvailabilitySlotDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AvailabilitySlotDTO dto = (AvailabilitySlotDTO) target;
        if (dto.getTimeSlotDTO() == null || dto.getTimeSlotDTO().getDateFrom() == null
                || dto.getTimeSlotDTO().getDateTo() == null) {
            errors.reject("Availability dates not specified");
        }
    }
}
