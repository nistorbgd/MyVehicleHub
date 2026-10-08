package com.nst.myvehiclehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
  private String firstName;
  private String lastName;
  private String email;
  private Integer age;
}
