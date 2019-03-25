package lv.agg.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String email;
    private String password;
    private String passwordRepeat;
}
