package com.mycompany.springboot.restfulapi.model.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResponse {

    private String username;

    private String name;

}
