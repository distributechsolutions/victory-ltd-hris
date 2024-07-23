package com.victorylimited.hris.views.profile;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@PageTitle("Employee Details")
@Route(value = "employee-details", layout = MainLayout.class)
public class EmployeeDetailsView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource
    private final EmployeeService employeeService;
    private EmployeeDTO employeeDTO;

    private final VerticalLayout employeeDetailsLayout = new VerticalLayout();
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
        Span recordIdValueSpan = new Span(employeeDTO.getId().toString());
        recordIdValueSpan.getStyle().setFontWeight("bold");

        Span recordIdSpan = new Span();
        recordIdSpan.add(new Span("Record ID: "), recordIdValueSpan);

        Span employeeValueSpan = new Span(employeeDTO.getEmployeeNumber());
        employeeValueSpan.getStyle().setFontWeight("bold");

        Span employeeNoSpan = new Span();
        employeeNoSpan.add(new Span("Employee No: "), employeeValueSpan);

        Span biometricNoValueSpan = new Span(employeeDTO.getBiometricsNumber());
        biometricNoValueSpan.getStyle().setFontWeight("bold");

        Span biometricNoSpan = new Span();
        biometricNoSpan.add(new Span("Biometric No: "), biometricNoValueSpan);

        String fullName = employeeDTO.getFirstName().concat(" ")
                .concat(employeeDTO.getMiddleName())
                .concat(" ")
                .concat(employeeDTO.getLastName())
                .concat(employeeDTO.getSuffix() != null ? " ".concat(employeeDTO.getSuffix()) : "");

        Span fullNameValueSpan = new Span(fullName);
        fullNameValueSpan.getStyle().setFontWeight("bold");

        Span fullNameSpan = new Span();
        fullNameSpan.add(new Span("Full Name: "), fullNameValueSpan);

        Span genderValueSpan = new Span(employeeDTO.getGender());
        genderValueSpan.getStyle().setFontWeight("bold");

        Span genderSpan = new Span();
        genderSpan.add(new Span("Gender: "), genderValueSpan);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
        String dateHired = df.format(employeeDTO.getDateHired());

        Span dateHiredValueSpan = new Span(dateHired);
        dateHiredValueSpan.getStyle().setFontWeight("bold");

        Span dateHiredSpan = new Span();
        dateHiredSpan.add(new Span("Date Hired: "), dateHiredValueSpan);

        Span atmAccountNoValueSpan = new Span(employeeDTO.getAtmAccountNumber());
        atmAccountNoValueSpan.getStyle().setFontWeight("bold");

        Span atmAccountNoSpan = new Span();
        atmAccountNoSpan.add(new Span("ATM Account Number: "), atmAccountNoValueSpan);

        employeeDetailsLayout.add(recordIdSpan,
                                  employeeNoSpan,
                                  biometricNoSpan,
                                  fullNameSpan,
                                  genderSpan,
                                  dateHiredSpan,
                                  atmAccountNoSpan);
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
