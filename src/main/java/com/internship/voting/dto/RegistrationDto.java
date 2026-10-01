package com.internship.voting.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/** Form object for voter registration, validated with Jakarta Bean Validation annotations. */
public class RegistrationDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Name must be 3 to 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 150, message = "Email is too long")
    private String email;

    @NotBlank(message = "Voter ID is required")
    @Pattern(regexp = "^[A-Za-z0-9]{5,20}$", message = "Voter ID must be 5 to 20 letters or digits")
    private String voterId;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be 8 to 64 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password needs an upper-case letter, a lower-case letter and a digit")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
