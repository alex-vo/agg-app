package lv.agg.dto.mapping;

import lv.agg.dto.UserProfileDTO;
import lv.agg.entity.ServiceEntity;
import lv.agg.entity.UserEntity;

import java.util.stream.Collectors;

public class UserProfileDTOMapper {

    public static UserProfileDTO map(UserEntity userEntity) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setEmail(userEntity.getEmail());
        dto.setFirstName(userEntity.getFirstName());
        dto.setLastName(userEntity.getLastName());
        dto.setCompanyName(userEntity.getCompanyName());
        dto.setAddress(userEntity.getAddress());
        dto.setServiceIds(userEntity.getServices()
                .stream()
                .map(ServiceEntity::getId)
                .collect(Collectors.toList()));
        dto.setUserRole(userEntity.getUserRole());
        dto.setIsCompany(userEntity.getMerchantType() == UserEntity.MerchantType.COMPANY);
        return dto;
    }

}
