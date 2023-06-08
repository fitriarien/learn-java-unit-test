package com.mycompany.springboot.restfulapi.model.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TokenResponse {

    private String token;

    private long expiredAt;
}
