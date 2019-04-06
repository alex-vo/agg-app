package lv.agg.dto.mapping;

import lv.agg.dto.AppointmentDTO;
import lv.agg.entity.AppointmentEntity;

public class AppointmentDTOMapper {
    public static AppointmentDTO map(AppointmentEntity appointmentEntity) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setFrom(appointmentEntity.getDateFrom());
        dto.setTo(appointmentEntity.getDateTo());
        dto.setServiceId(appointmentEntity.getService().getId());
        dto.setMerchantId(appointmentEntity.getMerchant().getId());
        dto.setStatus(appointmentEntity.getStatus().name());
        return dto;
    }
}
