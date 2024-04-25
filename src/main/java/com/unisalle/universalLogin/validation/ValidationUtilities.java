package com.unisalle.universalLogin.validation;


import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Objects;
import java.util.regex.Pattern;
@Component
public class ValidationUtilities {
    private static Pattern p = Pattern.compile("^[\\S]\\w{5,50}$");
    private static Pattern n = Pattern.compile("^[a-zA-Z ]{1,50}$");
    public  void validName(String name, Errors errors)  {
        if(!Objects.nonNull(name)) errors.reject("100", "Null value");
        if(!n.matcher(name).matches()){
            errors.reject("100","Invalid naming format: "+ name);
        }
    }

    public  void validEmail(String email, Errors errors)  {
        if(!Objects.nonNull(email))  errors.reject("100", "Null value");
        if (!(EmailValidator.getInstance().isValid(email))) {
            errors.reject("101", "Invalid email address: " + email);
        }
    }

    public  void validPassword(String password, Errors errors) {
        if(!Objects.nonNull(password)) errors.reject("100", "Null value");
        if (!p.matcher(password).matches()) {
            errors.reject("102","Invalid password format");
        }
    }
}