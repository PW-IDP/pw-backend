package com.pweb.backend.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) @Getter(AccessLevel.PUBLIC)
    private Long id;
    @Setter(AccessLevel.NONE) @Getter(AccessLevel.PUBLIC)
    private String value;

    public TestEntity() {}

    public TestEntity(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
