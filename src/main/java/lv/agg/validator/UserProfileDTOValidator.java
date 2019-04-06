package lv.agg.validator;

import lombok.extern.slf4j.Slf4j;
import lv.agg.dto.UserProfileDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
@Slf4j
public class UserProfileDTOValidator implements Validator {

    public static final Integer PASSWORD_MIN_LENGTH = 5;
    public static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Override
    public boolean supports(Class<?> clazz) {
        return UserProfileDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        try {
            UserProfileDTO dto = (UserProfileDTO) target;
            if (StringUtils.length(dto.getPassword()) < PASSWORD_MIN_LENGTH) {
                errors.reject("Password too short");
            }
            if (!StringUtils.equals(dto.getPassword(), dto.getPasswordRepeat())) {
                errors.reject("Passwords do not match");
            }
            if (StringUtils.isBlank(dto.getEmail()) || !EMAIL_REGEX.matcher(dto.getEmail()).matches()) {
                errors.reject("Email is not valid");
            }
        } catch (Exception e) {
            String errorMessage = "Failed to validate user profile";
            errors.reject(errorMessage);
            log.error(errorMessage, e);
        }

    }
}
