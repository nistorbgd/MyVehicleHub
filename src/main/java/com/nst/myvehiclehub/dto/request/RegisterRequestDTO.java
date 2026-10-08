package com.nst.myvehiclehub.dto.request;

import lombok.Data;

@Data
public class RegisterRequestDTO {
  private String email;
  private String password;
  private String lastName;
  private String firstName;
  private int age;
}
