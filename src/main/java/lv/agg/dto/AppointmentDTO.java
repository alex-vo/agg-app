package lv.agg.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AppointmentDTO {
    private Long serviceId;
    private Long merchantId;
    private ZonedDateTime from;
    private ZonedDateTime to;
    private String status;
}
