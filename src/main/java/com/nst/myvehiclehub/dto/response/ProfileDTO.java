package com.nst.myvehiclehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileDTO {
  private String firstName;
  private String lastName;
  private String email;
  private Integer age;
}
