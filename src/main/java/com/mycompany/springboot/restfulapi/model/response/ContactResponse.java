package com.mycompany.springboot.restfulapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ContactResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}
