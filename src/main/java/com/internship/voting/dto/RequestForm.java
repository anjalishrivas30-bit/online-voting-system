package com.internship.voting.dto;

import com.internship.voting.model.RequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Form object for "Submit Request" (voter). */
public class RequestForm {

    @NotNull(message = "Please choose a request type")
    private RequestType type;

    @NotBlank(message = "Please describe your request")
    @Size(min = 10, max = 1000, message = "Description must be 10 to 1000 characters")
    private String description;

    public RequestType getType() { return type; }
    public void setType(RequestType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
