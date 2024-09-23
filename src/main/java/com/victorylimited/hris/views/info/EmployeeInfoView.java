package com.victorylimited.hris.views.info;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.victorylimited.hris.services.admin.UserService;
import com.victorylimited.hris.services.info.PersonalInfoService;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Employee Information")
@Route(value = "employee-information", layout = MainLayout.class)
public class EmployeeInfoView extends VerticalLayout {
    @Resource private final PersonalInfoService personalInfoService;
    @Resource private final UserService userService;
    @Resource private final EmployeeService employeeService;

    private TabSheet infoTabSheets = new TabSheet();

    public EmployeeInfoView(PersonalInfoService personalInfoService,
                            UserService userService,
                            EmployeeService employeeService) {
        this.personalInfoService = personalInfoService;
        this.userService = userService;
        this.employeeService = employeeService;

        this.setSizeFull();
        this.createInfoTabSheets();
        this.add(infoTabSheets);
    }

    public void createInfoTabSheets() {
        PersonalInfoForm personalInfoForm = new PersonalInfoForm(personalInfoService, userService, employeeService);

        infoTabSheets.add("Personal", personalInfoForm);
        infoTabSheets.add("Addresses", new Div());
        infoTabSheets.add("Dependents", new Div());
    }

}
