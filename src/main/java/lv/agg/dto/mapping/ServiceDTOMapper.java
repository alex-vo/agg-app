package lv.agg.dto.mapping;

import lv.agg.dto.ServiceDTO;
import lv.agg.entity.ServiceEntity;

public class ServiceDTOMapper {
    public static ServiceDTO map(ServiceEntity entity) {
        ServiceDTO dto = new ServiceDTO();
        dto.setServiceId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}
