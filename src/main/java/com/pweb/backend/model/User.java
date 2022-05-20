package com.pweb.backend.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "identity", unique = true)
    private String identity;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user")
    private Set<Residence> residences;

    public Long getId() {
        return id;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Residence> getResidences() {
        return residences;
    }

    public void setResidences(Set<Residence> residences) {
        this.residences = residences;
    }
}
