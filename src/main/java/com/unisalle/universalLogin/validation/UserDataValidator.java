package com.unisalle.universalLogin.validation;

import com.unisalle.universalLogin.dtos.UserEntityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class UserDataValidator implements Validator {
    @Autowired
    private ValidationUtilities validationUtilities;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserEntityDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserEntityDTO userEntityDTO = (UserEntityDTO) target;

        validationUtilities.validName(userEntityDTO.getFirstName(), errors);
        validationUtilities.validName(userEntityDTO.getLastName(), errors);
        validationUtilities.validEmail(userEntityDTO.getEmail(), errors);
        validationUtilities.validPassword(userEntityDTO.getPassword(), errors);

        if (userEntityDTO.getNationalId() == null) {
            errors.rejectValue("identification", "NotNull.userEntityDTO.identification", "Identification cannot be null");
        }

    }

}
