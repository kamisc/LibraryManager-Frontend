package com.sewerynkamil.librarymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author Kamil Seweryn
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private String name;
    private String surname;
    private String email;
    private Integer phoneNumber;
    private String password;
}