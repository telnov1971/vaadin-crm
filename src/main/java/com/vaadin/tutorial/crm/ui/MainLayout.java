package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.tutorial.crm.ui.dashboard.DashboardView;
import com.vaadin.tutorial.crm.ui.list.ListView;

@CssImport("./styles/shared-styles.css")
// создает javascript контролирующий трафик приложения
@PWA(name = "VaadinCRM",
        shortName = "CRM",
        offlineResources = {
        "./styles/offline.css",
        "./images/offline.png"})
public class MainLayout extends AppLayout {
    public MainLayout() {
        createHeader();
        createDrawer();
    }
    private void createHeader() {
        H1 logo = new H1("Vaadin CRM");
        logo.addClassName("logo");
        Anchor logout = new Anchor("logout", "Log out");
        // сворачиваемый заголовок
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(
                FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("header");
        addToNavbar(header);
    }
    private void createDrawer() {
        // создаем ссылку для перехода к списку контактов
        RouterLink listLink = new RouterLink("List", ListView.class);
        listLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink dashLink = new RouterLink("Dashboard", DashboardView.class);
        dashLink.setHighlightCondition(HighlightConditions.sameLocation());

        addToDrawer(new VerticalLayout(listLink, dashLink));
    }
}
