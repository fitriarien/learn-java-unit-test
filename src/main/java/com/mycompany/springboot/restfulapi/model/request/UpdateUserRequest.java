package com.mycompany.springboot.restfulapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateUserRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String password;
}
