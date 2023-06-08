package com.mycompany.springboot.restfulapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactRequest {

    private String name;

    private String phone;

    private String email;

    @NotBlank
    private int page;

    @NotBlank
    private int size;
}
