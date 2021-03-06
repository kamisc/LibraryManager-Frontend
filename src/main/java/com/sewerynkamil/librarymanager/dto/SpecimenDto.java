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
public class SpecimenDto {
    private Long id;
    private String status;
    private String publisher;
    private Integer yearOfPublication;
    private String bookTitle;
    private Long isbn;

    public SpecimenDto(String bookTitle) {
        this.bookTitle = bookTitle;
    }
}