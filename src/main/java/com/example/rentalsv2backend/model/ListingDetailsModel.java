package com.example.rentalsv2backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ListingDetailsModel {
    @JsonProperty("name")
    private String name;

    @JsonProperty("beds")
    private int beds;

    @JsonProperty("baths")
    private int baths;

    @JsonProperty("description")
    private String description;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("contact")
    private String contact;
}
