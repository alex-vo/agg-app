package lv.agg.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TimeSlotDTO {
    private ZonedDateTime dateFrom;
    private ZonedDateTime dateTo;
}
