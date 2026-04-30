package org.example.personalaccount.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor // Автоматически создает конструктор для полей final

@Entity
@Setter
@Getter
@Table(name = "organization")
public class Organization {
    @Column(name = "ogrn", unique = true, nullable = false)
    private final String ogrn;

    @Column(name = "kpp", unique = true, nullable = false)
    private final String kpp;

    @Column(name = "shortNameOrganization", unique = true, nullable = false)
    private final String shortNameOrganization;

    @Column(name = "fullNameOrganization", unique = true, nullable = false)
    private final String fullNameOrganization;

    @Column(name = "typeOfOrganization", unique = true, nullable = false)
    private final String typeOfOrganization;
}
