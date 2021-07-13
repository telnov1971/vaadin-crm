package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.tutorial.crm.backend.entity.Company;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.service.CompanyService;
import com.vaadin.tutorial.crm.backend.service.ContactService;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route("")
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
public class MainView extends VerticalLayout {
    // задание сущности для построения таблицы
    private Grid<Contact> grid = new Grid(Contact.class,false);
    private final ContactService contactService;
    private final CompanyService companyService;
    // поле для указания фильтра
    private TextField filterText = new TextField();
    // форма редактирования контакта
    private ContactForm form;

    public MainView(ContactService contactService, CompanyService companyService) {
        this.contactService = contactService;
        this.companyService = companyService;
        addClassName("list-view");  // класс CSS
        setSizeFull();              // размер во всё окно
        configureFilter();          // настройка фильтра
        configureGrid();            // настройка таблицы

        form = new ContactForm(this.companyService.findAll());
        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();
        add(filterText, content);

        updateList();
        closeEditor();
    }

    private void configureFilter() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        // автоматическое сообщение об обновлении после нескольких введенных букв
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        // вызов функции при обновлении
        filterText.addValueChangeListener(e -> updateList());
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
}
