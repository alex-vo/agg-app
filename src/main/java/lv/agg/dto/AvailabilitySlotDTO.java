package lv.agg.dto;

import lombok.Data;

@Data
public class AvailabilitySlotDTO {
    private Long serviceId;
    private Long userId;
    private TimeSlotDTO timeSlotDTO;
}
