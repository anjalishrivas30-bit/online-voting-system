package com.internship.voting.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.BindingResult;

/** Converts Spring's BindingResult into a simple field->message map that JSP pages read as ${errors['field']}. */
public final class FormErrors {

    private FormErrors() { }

    public static Map<String, String> from(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        if (result != null) {
            result.getFieldErrors().forEach(e -> map.putIfAbsent(e.getField(), e.getDefaultMessage()));
        }
        return map;
    }
}
