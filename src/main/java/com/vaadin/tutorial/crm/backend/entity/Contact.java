package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Entity     // сущность контакта
public class Contact extends AbstractEntity implements Cloneable {
    public enum Status {
        ImportedLead, NotContacted, Contacted, Customer, ClosedLost
    }
    @NotNull        // поле не может быть неопределенным
    @NotEmpty       // поле не может быть пустым
    private String firstName = "";

    @NotNull
    @NotEmpty
    private String lastName = "";

    @ManyToOne      // много контактов ссылаются на одну компанию
    @JoinColumn(name = "company_id")    // название столбца таблицы в БД
    private Company company;            // название поля в объекте

    @Enumerated(EnumType.STRING)        // значения берутся из перечисления как строки
    @NotNull
    private Contact.Status status;

    @Email          // поле содержит емайл
    @NotNull
    @NotEmpty
    private String email = "";

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public Company getCompany() {
        return company;
    }
    @Override       // для перевода сущности в строку
    public String toString() {
        return firstName + " " + lastName;
    }
}