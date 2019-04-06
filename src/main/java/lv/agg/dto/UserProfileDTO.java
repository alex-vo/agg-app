package lv.agg.dto;

import lombok.Data;
import lv.agg.entity.UserEntity;

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
}
