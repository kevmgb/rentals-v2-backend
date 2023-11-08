package com.example.rentalsv2backend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Page<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
}
