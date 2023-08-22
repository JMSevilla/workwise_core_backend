package com.workwise.springjwt.models;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "users", 
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email") 
    })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  @NotBlank
  private String firstname;
  private String middlename;
  @NotBlank
  private String lastname;

  private Integer status;

  private String image_url;
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(  name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  private Integer access_level;
  public User() {
  }

  public User(String username, String email, String password,
              String firstname, String middlename,
              String lastname, Integer status,
              String image_url, Integer access_level) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.firstname = firstname;
    this.middlename = middlename;
    this.lastname = lastname;
    this.status = status;
    this.image_url = image_url;
    this.access_level = access_level;
  }

}
