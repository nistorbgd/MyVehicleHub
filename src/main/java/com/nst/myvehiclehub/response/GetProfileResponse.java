package com.nst.myvehiclehub.response;

import com.nst.myvehiclehub.entity.AuthProvider;
import com.nst.myvehiclehub.entity.Role;
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