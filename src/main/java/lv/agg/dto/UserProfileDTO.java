package lv.agg.dto;

import lombok.Data;
import lv.agg.entity.UserEntity;

import java.util.List;

@Data
public class UserProfileDTO {
    private String email;
    private String password;
    private String passwordRepeat;
    private UserEntity.UserRole userRole;
    private Boolean isCompany;
    private String firstName;
    private String lastName;
    private String companyName;
    private String address;
    private List<Long> serviceIds;
}
