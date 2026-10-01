package com.internship.voting.model;

import jakarta.persistence.*;

/** A security role. Names follow Spring Security's ROLE_ prefix convention. */
@Entity
@Table(name = "roles")
public class Role {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String VOTER = "ROLE_VOTER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    protected Role() { }

    public Role(String name) { this.name = name; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
