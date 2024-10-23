package com.victorylimited.hris.views.profile;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.*;

import com.victorylimited.hris.dtos.info.AddressInfoDTO;
import com.victorylimited.hris.dtos.info.DependentInfoDTO;
import com.victorylimited.hris.dtos.info.PersonalInfoDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.info.AddressInfoService;
import com.victorylimited.hris.services.info.DependentInfoService;
import com.victorylimited.hris.services.info.PersonalInfoService;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.vaadin.lineawesome.LineAwesomeIcon;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR",
               "ROLE_HR_EMPLOYEE"})
@PageTitle("Employee Details")
@Route(value = "employee-details", layout = MainLayout.class)
public class EmployeeDetailsView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource private final EmployeeService employeeService;
    @Resource private final PersonalInfoService personalInfoService;
    @Resource private final AddressInfoService addressInfoService;
    @Resource private final DependentInfoService dependentInfoService;

    private EmployeeDTO employeeDTO;
    private PersonalInfoDTO personalInfoDTO;
    private List<AddressInfoDTO> addressInfoDTOList;
    private List<DependentInfoDTO> dependentInfoDTOList;

    private final FormLayout employeeDetailsLayout = new FormLayout();
    private final VerticalLayout personalInfoLayout = new VerticalLayout();
    private final VerticalLayout addressInfoLayout = new VerticalLayout();
    private final VerticalLayout dependentInfoLayout = new VerticalLayout();
    private final VerticalLayout employeeRequirementsLayout = new VerticalLayout();

    private TabSheet employeeInformationTabSheets = new TabSheet();

    enum MessageLevel {
        INFO,
        SUCCESS,
        WARNING,
        DANGER
    }

    public EmployeeDetailsView(EmployeeService employeeService,
                               PersonalInfoService personalInfoService,
                               AddressInfoService addressInfoService,
                               DependentInfoService dependentInfoService) {
        this.employeeService = employeeService;
        this.personalInfoService = personalInfoService;
        this.addressInfoService = addressInfoService;
        this.dependentInfoService = dependentInfoService;

        setSizeFull();
        setMargin(true);
        add(employeeDetailsLayout, employeeInformationTabSheets);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
        if (parameter != null) {
            UUID parameterId = UUID.fromString(parameter);
            employeeDTO = employeeService.getById(parameterId);
        }

        buildEmployeeDetailsLayout();
        buildEmployeeInformationTabSheets();
        buildPersonalInfoLayout();
        buildAddressInfoLayout();
        buildDependentInfoLayout();
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
        employeeDetailsLayout.setWidth("768px");
    }

    private void buildEmployeeInformationTabSheets() {
        employeeInformationTabSheets.add("Personal", personalInfoLayout);
        employeeInformationTabSheets.add("Addresses", addressInfoLayout);
        employeeInformationTabSheets.add("Dependents", dependentInfoLayout);
        employeeInformationTabSheets.add("Documents", employeeRequirementsLayout);
    }

    private void buildPersonalInfoLayout() {
        personalInfoDTO = personalInfoService.getByEmployeeDTO(employeeDTO);

        if (personalInfoDTO != null) {
            FormLayout personalInfoFormLayout = new FormLayout();

            Span dateOfBirthLabelSpan = new Span("Date of birth");
            dateOfBirthLabelSpan.getStyle().set("text-align", "right");

            Span dateOfBirthValueSpan = new Span(DateTimeFormatter.ofPattern("MMM dd, yyyy").format(personalInfoDTO.getDateOfBirth()));
            dateOfBirthValueSpan.getStyle().setFontWeight("bold");

            Span placeOfBirthLabelSpan = new Span("Place of birth");
            placeOfBirthLabelSpan.getStyle().set("text-align", "right");

            Span placeOfBirthValueSpan = new Span(personalInfoDTO.getPlaceOfBirth());
            placeOfBirthValueSpan.getStyle().setFontWeight("bold");

            Span maritalStatusLabelSpan = new Span("Marital Status");
            maritalStatusLabelSpan.getStyle().set("text-align", "right");

            Span maritalStatusValueSpan = new Span(personalInfoDTO.getMaritalStatus());
            maritalStatusValueSpan.getStyle().setFontWeight("bold");

            Span spouseLabelSpan = new Span("Spouse Name");
            spouseLabelSpan.getStyle().set("text-align", "right");

            Span spouseValueSpan = new Span(personalInfoDTO.getSpouseName() != null ? personalInfoDTO.getSpouseName() : "");
            spouseValueSpan.getStyle().setFontWeight("bold");

            Span contactNoLabelSpan = new Span("Contact Number");
            contactNoLabelSpan.getStyle().set("text-align", "right");

            Span contactNoValueSpan = new Span(String.valueOf(personalInfoDTO.getContactNumber()));
            contactNoValueSpan.getStyle().setFontWeight("bold");

            Span emailLabelSpan = new Span("Email");
            emailLabelSpan.getStyle().set("text-align", "right");

            Span emailValueSpan = new Span(personalInfoDTO.getEmailAddress());
            emailValueSpan.getStyle().setFontWeight("bold");

            Span tinLabelSpan = new Span("TIN");
            tinLabelSpan.getStyle().set("text-align", "right");

            Span tinValueSpan = new Span(personalInfoDTO.getTaxIdentificationNumber());
            tinValueSpan.getStyle().setFontWeight("bold");

            Span sssLabelSpan = new Span("SSS");
            sssLabelSpan.getStyle().set("text-align", "right");

            Span sssValueSpan = new Span(personalInfoDTO.getSssNumber());
            sssValueSpan.getStyle().setFontWeight("bold");

            Span hdmfLabelSpan = new Span("Pag-Ibig HDMF");
            hdmfLabelSpan.getStyle().set("text-align", "right");

            Span hdmfValueSpan = new Span(personalInfoDTO.getHdmfNumber());
            hdmfValueSpan.getStyle().setFontWeight("bold");

            Span philhealthLabelSpan = new Span("Philhealth");
            philhealthLabelSpan.getStyle().set("text-align", "right");

            Span philhealthValueSpan = new Span(personalInfoDTO.getPhilhealthNumber());
            philhealthValueSpan.getStyle().setFontWeight("bold");

            personalInfoFormLayout.add(dateOfBirthLabelSpan,
                                       dateOfBirthValueSpan,
                                       placeOfBirthLabelSpan,
                                       placeOfBirthValueSpan,
                                       maritalStatusLabelSpan,
                                       maritalStatusValueSpan,
                                       spouseLabelSpan,
                                       spouseValueSpan,
                                       contactNoLabelSpan,
                                       contactNoValueSpan,
                                       emailLabelSpan,
                                       emailValueSpan,
                                       tinLabelSpan,
                                       tinValueSpan,
                                       sssLabelSpan,
                                       sssValueSpan,
                                       hdmfLabelSpan,
                                       hdmfValueSpan,
                                       philhealthLabelSpan,
                                       philhealthValueSpan);
            personalInfoFormLayout.setWidth("768px");

            personalInfoLayout.add(personalInfoFormLayout);
        } else {
            Div profileMessageNotification = this.buildNotification("Employee has not yet filled up this information.",
                                                                    EmployeeDetailsView.MessageLevel.INFO,
                                                                    LineAwesomeIcon.INFO_CIRCLE_SOLID.create());

            personalInfoLayout.add(profileMessageNotification);
        }

        personalInfoLayout.setWidth("768px");
    }

    private void buildAddressInfoLayout() {
        addressInfoDTOList = addressInfoService.getByEmployeeDTO(employeeDTO);

        if (!addressInfoDTOList.isEmpty()) {
            Grid<AddressInfoDTO> addressInfoDTOGrid = new Grid<>(AddressInfoDTO.class, false);
            addressInfoDTOGrid.setItems(addressInfoDTOList);
            addressInfoDTOGrid.addColumn(AddressInfoDTO::getAddressType)
                              .setHeader("Address Type");
            addressInfoDTOGrid.addColumn(addressDTO -> addressDTO.getAddressDetail()
                                                                 .concat(" ")
                                                                 .concat(addressDTO.getStreetName())
                                                                 .concat(", ")
                                                                 .concat(addressDTO.getBarangayDTO().getBarangayDescription()))
                              .setHeader("Address Details");
            addressInfoDTOGrid.addColumn(addressInfoDTO -> addressInfoDTO.getMunicipalityDTO().getMunicipalityDescription())
                              .setHeader("Municipality");
            addressInfoDTOGrid.addColumn(addressInfoDTO -> addressInfoDTO.getProvinceDTO().getProvinceDescription())
                              .setHeader("Province");
            addressInfoDTOGrid.addColumn(addressInfoDTO -> addressInfoDTO.getRegionDTO().getRegionDescription())
                              .setHeader("Region");
            addressInfoDTOGrid.addColumn(AddressInfoDTO::getPostalCode)
                              .setHeader("Postal Code");
            addressInfoDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                                                GridVariant.LUMO_COLUMN_BORDERS,
                                                GridVariant.LUMO_WRAP_CELL_CONTENT);
            addressInfoDTOGrid.setAllRowsVisible(true);

            addressInfoLayout.add(addressInfoDTOGrid);
        } else {
            Div addressMessageNotification = this.buildNotification("Employee has not yet filled up this information.",
                                                                    EmployeeDetailsView.MessageLevel.INFO,
                                                                    LineAwesomeIcon.INFO_CIRCLE_SOLID.create());

            addressInfoLayout.add(addressMessageNotification);
        }

        addressInfoLayout.setWidth("768px");
    }

    private void buildDependentInfoLayout() {
        dependentInfoDTOList = dependentInfoService.getByEmployeeDTO(employeeDTO);

        if (!dependentInfoDTOList.isEmpty()) {
            Grid<DependentInfoDTO> dependentInfoDTOGrid = new Grid<>(DependentInfoDTO.class, false);
            dependentInfoDTOGrid.setItems(dependentInfoDTOList);
            dependentInfoDTOGrid.addColumn(DependentInfoDTO::getFullName).setHeader("Name");
            dependentInfoDTOGrid.addColumn(new LocalDateRenderer<>(DependentInfoDTO::getDateOfBirth, "MMM dd, yyyy")).setHeader("Date of Birth");
            dependentInfoDTOGrid.addColumn(DependentInfoDTO::getAge).setHeader("Age");
            dependentInfoDTOGrid.addColumn(DependentInfoDTO::getRelationship).setHeader("Relationship");
            dependentInfoDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                                                  GridVariant.LUMO_COLUMN_BORDERS,
                                                  GridVariant.LUMO_WRAP_CELL_CONTENT);
            dependentInfoDTOGrid.setAllRowsVisible(true);

            dependentInfoLayout.add(dependentInfoDTOGrid);
        } else {
            Div dependentMessageNotification = this.buildNotification("Employee has not yet filled up this information.",
                    EmployeeDetailsView.MessageLevel.INFO,
                    LineAwesomeIcon.INFO_CIRCLE_SOLID.create());

            dependentInfoLayout.add(dependentMessageNotification);
        }

        dependentInfoLayout.setWidth("768px");
    }

    private Div buildNotification(String message, EmployeeDetailsView.MessageLevel messageLevel, SvgIcon svgIcon) {
        Div text = new Div(new Text(message));

        HorizontalLayout layout = new HorizontalLayout(svgIcon, text);
        layout.setAlignItems(Alignment.CENTER);

        Div notificationDiv = new Div();
        notificationDiv.getStyle().set("padding", "20px")
                .set("border-radius", "3px")
                .set("color", "#fdfefe")
                .set("margin-bottom", "5px");

        // Change the background color based on the message level.
        switch (messageLevel) {
            case INFO -> notificationDiv.getStyle().set("background-color", "#2196F3");
            case SUCCESS -> notificationDiv.getStyle().set("background-color", "#04AA6D");
            case WARNING -> notificationDiv.getStyle().set("background-color", "#ff9800");
            case DANGER -> notificationDiv.getStyle().set("background-color", "#f44336");
        }

        notificationDiv.add(layout);

        return notificationDiv;
    }
}
