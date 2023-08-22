package com.workwise.springjwt.payload.request;

import java.util.Set;

import com.workwise.springjwt.models.ERole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
  @NotBlank
  @Size(min = 1, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private Set<String> role;

  private Integer access_level;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  @NotBlank
  private String firstname;
  private String middlename;
  @NotBlank
  private String lastname;

  private Integer status;
  private String image_url;
}
