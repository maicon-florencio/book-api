package com.udemy.api.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookDTO {

    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String author;

    @NonNull
    private String isbn;
}
