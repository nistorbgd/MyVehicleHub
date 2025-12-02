package com.nst.myvehiclehub.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String lastName;
    private String firstName;
    private int age;
}
