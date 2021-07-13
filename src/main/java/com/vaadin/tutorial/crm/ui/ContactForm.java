package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;

import java.util.List;

// Хорошее эмпирическое правило при разработке API для многоразового компонента:
// свойства входят, события выходят.
// Пользователи должны иметь возможность полностью настроить компонент,
// задав свойства. Они должны быть уведомлены обо всех соответствующих событиях,
// без необходимости вручную вызывать получателей, чтобы узнать, не изменилось ли что-то

// класс для формы контактов
public class ContactForm extends FormLayout {
    private Contact contact;

    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private EmailField email = new EmailField("Email");
    private ComboBox<Contact.Status> status = new ComboBox<>("Status");
    private ComboBox<Company> company = new ComboBox<>("Company");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Button close = new Button("Cancel");

    // связной для полей обекта Контакт и полей формы
    // может использовать расширенные функции для проверки значений и
    // преобразования данных (здесь не используется)
    // использует проверки данных заданные в определении сущности
    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

    // компании нужно загрузить в список выбора до редактирования контакта
    public ContactForm(List<Company> companies) {
        addClassName("contact-form");

        company.setItems(companies);
        // в списке отображается только имя компании
        company.setItemLabelGenerator(Company::getName);

        // список статусов из перечисления
        status.setItems(Contact.Status.values());

        // связывает все совпадающие по именам поля формы и сущности
        binder.bindInstanceFields(this);

        add(firstName,
                lastName,
                email,
                company,
                status,
                createButtonsLayout());
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        // читает данные из БД и копирует в привязанные поля
        binder.readBean(contact);
    }

    private HorizontalLayout createButtonsLayout() {
        // стиль кнопок
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        // привязка к клавишам
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        // вызов функций при нажатии кнопок
        // проверить и сохранить
        save.addClickListener(event -> validateAndSave());
        // удалить контакт
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, contact)));
        // закрыть форму
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        // кнопка сохранить доступна когда данные корректны
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            // связной записывает значения полей формы в поля объекта
            binder.writeBean(contact);
            // генерируется событие сохранения объекта
            fireEvent(new SaveEvent(this, contact));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // классы для добавления управления событиями для формы контакта
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
        private Contact contact;

        // защищенный конструктор виден потомкам
        protected ContactFormEvent(ContactForm source, Contact contact) {
            super(source, false);
            this.contact = contact;
        }
        public Contact getContact() {
            return contact;
        }
    }
    public static class SaveEvent extends ContactFormEvent {
        SaveEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }
    public static class DeleteEvent extends ContactFormEvent {
        DeleteEvent(ContactForm source, Contact contact) {
            super(source, contact);
        }
    }
    public static class CloseEvent extends ContactFormEvent {
        CloseEvent(ContactForm source) {
            super(source, null);
        }
    }

    // регистрация слушателей событий формы
    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType,
            ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}