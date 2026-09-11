package com.nst.myvehiclehub.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetProfileResponse {
  private String firstName;
  private String lastName;
  private String email;
  private Integer age;
}
