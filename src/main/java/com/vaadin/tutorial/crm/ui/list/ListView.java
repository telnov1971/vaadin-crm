package com.vaadin.tutorial.crm.ui.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.service.CompanyService;
import com.vaadin.tutorial.crm.backend.service.ContactService;
import com.vaadin.tutorial.crm.ui.MainLayout;

// Любой Vaadin компонент может быть указан целью навигации с помощью
// аннотации @Route("<path>"). Навигация может быть вложенной с определением
// родительского уровня в аннотации: @Route(value = "list", parent=MainView.class)
@Route(value="", layout = MainLayout.class)
@PageTitle("Contacts | Vaadin CRM")
public class ListView extends VerticalLayout {
    // задание сущности для построения таблицы
    private Grid<Contact> grid = new Grid(Contact.class,false);
    private final ContactService contactService;
    private final CompanyService companyService;
    // поле для указания фильтра
    private TextField filterText = new TextField();
    private HorizontalLayout toolbar;
    // форма редактирования контакта
    private ContactForm form;

    public ListView(ContactService contactService, CompanyService companyService) {
        this.contactService = contactService;
        this.companyService = companyService;
        addClassName("list-view");  // класс CSS
        setSizeFull();              // размер во всё окно
        toolbar = getToolbar();          // настройка инструментов
        configureGrid();            // настройка таблицы

        form = new ContactForm(this.companyService.findAll());
        // установка реакции на события формы
        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactForm.CloseEvent.class, e -> closeEditor());

        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();
        add(toolbar, content);

        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        // автоматическое сообщение об обновлении после нескольких введенных букв
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        // вызов функции при обновлении
        filterText.addValueChangeListener(e -> updateList());

        Button addContactButton = new Button("Add contact");
        addContactButton.addClickListener(event -> addContact());

        HorizontalLayout toolbar = new HorizontalLayout(filterText,addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();

        // удалить описание солдбца компании по умолчанию (ломает добавление столбца)
        // grid.removeColumnByKey("company");

        // указание столбцов показанных в таблице
        grid.setColumns("firstName", "lastName", "email", "status");

        // добавить собственные настройки столбца
        grid.addColumn(contact -> {                             // для всех контактов
            Company company = contact.getCompany();             // извлекаем ссылку на компанию
            return company == null ? "-" : company.getName();   // возвращем название или прочерк
        }).setHeader("Company");                                // ставим заголовок

        // автоматическая ширина стобцов таблицы
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        // обработка события смены выбранной строки таблицы
        grid.asSingleSelect().addValueChangeListener(event ->
                editContact(event.getValue()));
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    public void editContact(Contact contact) {
        if (contact == null) {
            closeEditor();
        } else {
            form.setContact(contact);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setContact(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        // загрузим в таблицу все контакты
        grid.setItems(contactService.findAll(filterText.getValue()));
    }

    private void  saveContact(ContactForm.SaveEvent event) {
        contactService.save(event.getContact());
        updateList();
        closeEditor();
    }

    private void deleteContact(ContactForm.DeleteEvent event) {
        contactService.delete(event.getContact());
        updateList();
        closeEditor();
    }
}
