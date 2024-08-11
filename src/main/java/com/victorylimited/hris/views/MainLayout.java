package com.victorylimited.hris.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

import com.victorylimited.hris.dtos.admin.UserDTO;
import com.victorylimited.hris.services.admin.UserService;
import com.victorylimited.hris.utils.SecurityUtil;
import com.victorylimited.hris.views.admin.DepartmentListView;
import com.victorylimited.hris.views.admin.PositionListView;
import com.victorylimited.hris.views.admin.UserListView;
import com.victorylimited.hris.views.common.DashboardView;
import com.victorylimited.hris.views.compenben.RatesListView;
import com.victorylimited.hris.views.profile.EmployeeDepartmentListView;
import com.victorylimited.hris.views.profile.EmployeeListView;
import com.victorylimited.hris.views.profile.EmployeePositionListView;

import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    private UserDTO userDTO;
    private H1 viewTitle;

    public MainLayout(UserService userService) {

        // Gets the user data transfer object based from the logged in user.
        if (SecurityUtil.getAuthenticatedUser() != null) {
            userDTO = userService.findByParameter(SecurityUtil.getAuthenticatedUser().getUsername()).get(0);
        }

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.NONE);
        viewTitle.getStyle().setWidth("50%");

        // This will show the logout button if the user is logged in the application.
        if (userDTO != null) {
            Button logoutButton = new Button("Logout", click -> SecurityUtil.logout());
            logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Span loggedUserSpan = new Span("Welcome " + userDTO.getEmployeeDTO().getFirstName().concat("!"));
            loggedUserSpan.getStyle().set("padding-right", "10px");

            Div logInfoDiv = new Div(loggedUserSpan, logoutButton);

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.add(logInfoDiv);
            verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, logInfoDiv);

            addToNavbar(true, toggle, viewTitle, verticalLayout);
        } else {
            addToNavbar(true, toggle, viewTitle);
        }
    }

    private void addDrawerContent() {
        Span appName = new Span("Victory Ltd HRIS");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.DASHCUBE.create()));

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
            userDTO.getRole().equals("ROLE_HR_MANAGER") ||
            userDTO.getRole().equals("ROLE_HR_SUPERVISOR") ||
            userDTO.getRole().equals("ROLE_HR_EMPLOYEE")) {
            nav.addItem(new SideNavItem("Employees", EmployeeListView.class, LineAwesomeIcon.ID_BADGE_SOLID.create()));
            nav.addItem(new SideNavItem("Employee Position", EmployeePositionListView.class, LineAwesomeIcon.ID_CARD_SOLID.create()));
            nav.addItem(new SideNavItem("Employee Department", EmployeeDepartmentListView.class, LineAwesomeIcon.PORTRAIT_SOLID.create()));
        }

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
            userDTO.getRole().equals("ROLE_HR_MANAGER") ||
            userDTO.getRole().equals("ROLE_HR_SUPERVISOR")) {
            nav.addItem(new SideNavItem("Rates", RatesListView.class, LineAwesomeIcon.MONEY_CHECK_SOLID.create()));
            nav.addItem(new SideNavItem("Positions", PositionListView.class, LineAwesomeIcon.GLASSES_SOLID.create()));
            nav.addItem(new SideNavItem("Departments", DepartmentListView.class, LineAwesomeIcon.BUILDING_SOLID.create()));
        }

        if (userDTO.getRole().equals("ROLE_ADMIN") ||
            userDTO.getRole().equals("ROLE_HR_MANAGER")) {
            nav.addItem(new SideNavItem("Users", UserListView.class, LineAwesomeIcon.USER_LOCK_SOLID.create()));
        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
