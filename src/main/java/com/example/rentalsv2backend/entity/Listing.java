package com.example.rentalsv2backend.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table("listings")
public class Listing {
    @Id
    @Column("id")
    private int id;

    @Column("name")
    private String name;

    @Column("beds")
    private int beds;

    @Column("baths")
    private int baths;

    @Column("user_id")
    private int userId;
}
