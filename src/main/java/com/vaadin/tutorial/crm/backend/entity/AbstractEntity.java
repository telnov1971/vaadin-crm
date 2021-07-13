package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass   // класс является базовым для других сущностей
public abstract class AbstractEntity {
    @Id             // поле является индексом
    @GeneratedValue(strategy= GenerationType.SEQUENCE)  // индекс генерируется как последовательность
    private Long id;
    public Long getId() {
        return id;
    }
    public boolean isPersisted() {
        return id != null;
    }
    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractEntity other = (AbstractEntity) obj;
        if (getId() == null || other.getId() == null) {
            return false;
        }
        return getId().equals(other.getId());
    }
}