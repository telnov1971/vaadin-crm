package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.tutorial.crm.backend.entity.Contact;
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

    public MainView(ContactService contactService) {
        this.contactService = contactService;
        addClassName("list-view");  // класс CSS
        setSizeFull();              // размер во всё окно
        configureGrid();
        add(grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();

        // указание столбцов показанных в таблице
        grid.setColumns("firstName", "lastName", "email", "status");
    }

    private void updateList() {
        // загрузим в таблицу все контакты
        grid.setItems(contactService.findAll());
    }
}
