package com.victorylimited.hris.views.profile;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.*;

import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR",
               "ROLE_HR_EMPLOYEE"})
@PageTitle("Employee Details")
@Route(value = "employee-details", layout = MainLayout.class)
public class EmployeeDetailsView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource
    private final EmployeeService employeeService;
    private EmployeeDTO employeeDTO;

    private final FormLayout employeeDetailsLayout = new FormLayout();
    private final Tabs employeeInformationTabs = new Tabs();

    public EmployeeDetailsView(EmployeeService employeeService) {
        this.employeeService = employeeService;

        setSizeFull();
        setMargin(true);
        add(employeeDetailsLayout, employeeInformationTabs);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
        if (parameter != null) {
            UUID parameterId = UUID.fromString(parameter);
            employeeDTO = employeeService.getById(parameterId);
        }

        buildEmployeeDetailsLayout();
        buildEmployeeInformationTabs();
    }

    public void buildEmployeeDetailsLayout() {
        Span recordIdLabelSpan = new Span("Record ID");
        recordIdLabelSpan.getStyle().set("text-align", "right");

        Span recordIdValueSpan = new Span(employeeDTO.getId().toString());
        recordIdValueSpan.getStyle().setFontWeight("bold");

        Span employeeNoLabelSpan = new Span("Employee No");
        employeeNoLabelSpan.getStyle().set("text-align", "right");

        Span employeeNoValueSpan = new Span(employeeDTO.getEmployeeNumber());
        employeeNoValueSpan.getStyle().setFontWeight("bold");

        Span biometricNoLabelSpan = new Span("Biometric No");
        biometricNoLabelSpan.getStyle().set("text-align", "right");

        Span biometricNoValueSpan = new Span(employeeDTO.getBiometricsNumber());
        biometricNoValueSpan.getStyle().setFontWeight("bold");

        Span fullNameLabelSpan = new Span("Full Name");
        fullNameLabelSpan.getStyle().set("text-align", "right");

        String fullName = employeeDTO.getFirstName().concat(" ")
                .concat(employeeDTO.getMiddleName())
                .concat(" ")
                .concat(employeeDTO.getLastName())
                .concat(employeeDTO.getSuffix() != null ? " ".concat(employeeDTO.getSuffix()) : "");

        Span fullNameValueSpan = new Span(fullName);
        fullNameValueSpan.getStyle().setFontWeight("bold");

        Span genderLabelSpan = new Span("Gender");
        genderLabelSpan.getStyle().set("text-align", "right");

        Span genderValueSpan = new Span(employeeDTO.getGender());
        genderValueSpan.getStyle().setFontWeight("bold");

        Span dateHiredLabelSpan = new Span("Date Hired");
        dateHiredLabelSpan.getStyle().set("text-align", "right");

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
        String dateHired = df.format(employeeDTO.getDateHired());

        Span dateHiredValueSpan = new Span(dateHired);
        dateHiredValueSpan.getStyle().setFontWeight("bold");

        Span atmAccountNoLabelSpan = new Span("ATM Account Number");
        atmAccountNoLabelSpan.getStyle().set("text-align", "right");

        Span atmAccountNoValueSpan = new Span(employeeDTO.getAtmAccountNumber());
        atmAccountNoValueSpan.getStyle().setFontWeight("bold");

        employeeDetailsLayout.add(recordIdLabelSpan,
                                  recordIdValueSpan,
                                  employeeNoLabelSpan,
                                  employeeNoValueSpan,
                                  biometricNoLabelSpan,
                                  biometricNoValueSpan,
                                  fullNameLabelSpan,
                                  fullNameValueSpan,
                                  genderLabelSpan,
                                  genderValueSpan,
                                  dateHiredLabelSpan,
                                  dateHiredValueSpan,
                                  atmAccountNoLabelSpan,
                                  atmAccountNoValueSpan);
        employeeDetailsLayout.setWidth("720px");
    }

    public void buildEmployeeInformationTabs() {
        Tab personalInformationTab = new Tab("Personal Information");
        Tab addressesTab = new Tab("Addresses");
        Tab dependentsTab = new Tab("Dependents");
        Tab employeeRequirementsTab = new Tab("Employment Requirements");

        employeeInformationTabs.add(personalInformationTab, addressesTab, dependentsTab, employeeRequirementsTab);
        employeeInformationTabs.addThemeVariants(TabsVariant.LUMO_CENTERED);
    }
}
