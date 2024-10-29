package com.victorylimited.hris.views.compenben;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.*;

import com.victorylimited.hris.dtos.compenben.LeaveBenefitsDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.compenben.LeaveBenefitsService;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.utils.SecurityUtil;
import com.victorylimited.hris.utils.StringUtil;

import com.victorylimited.hris.views.MainLayout;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR"})
@PageTitle("Leave Benefits Form")
@Route(value = "leave-benefits-form", layout = MainLayout.class)
public class LeaveBenefitsFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource private final LeaveBenefitsService leaveBenefitsService;
    @Resource private final EmployeeService employeeService;

    private LeaveBenefitsDTO leaveBenefitsDTO;
    private UUID parameterId;

    private final FormLayout leaveBenefitsDTOFormLayout = new FormLayout();
    private ComboBox<EmployeeDTO> employeeDTOComboBox;
    private ComboBox<String> leaveTypeComboBox;
    private IntegerField leaveForYearIntegerField;
    private IntegerField leaveCountIntegerField;
    private Checkbox leaveActiveCheckbox;

    public LeaveBenefitsFormView(LeaveBenefitsService leaveBenefitsService,
                                 EmployeeService employeeService) {
        this.leaveBenefitsService = leaveBenefitsService;
        this.employeeService = employeeService;

        setSizeFull();
        setMargin(true);
        add(leaveBenefitsDTOFormLayout);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        if (s != null) {
            parameterId = UUID.fromString(s);
            leaveBenefitsDTO = leaveBenefitsService.getById(parameterId);
        }

        buildLeaveBenefitsFormLayout();
    }

    private void buildLeaveBenefitsFormLayout() {
        // Create the query object that will do the pagination of employee records in the combo box component.
        Query<EmployeeDTO, Void> employeeQuery = new Query<>();

        employeeDTOComboBox = new ComboBox<>("Employee");
        employeeDTOComboBox.setItems((employeeDTO, filterString) -> employeeDTO.getEmployeeFullName().toLowerCase().contains(filterString.toLowerCase()),
                                                                    employeeService.getAll(employeeQuery.getPage(), employeeQuery.getPageSize()));
        employeeDTOComboBox.setItemLabelGenerator(EmployeeDTO::getEmployeeFullName);
        employeeDTOComboBox.setClearButtonVisible(true);
        employeeDTOComboBox.setRequired(true);
        employeeDTOComboBox.setRequiredIndicatorVisible(true);
        if (leaveBenefitsDTO != null) employeeDTOComboBox.setValue(leaveBenefitsDTO.getEmployeeDTO());

        leaveTypeComboBox = new ComboBox<>("Leave Type");
        leaveTypeComboBox.setItems("(VL) Vacation Leave",
                                   "(SL) Sick Leave",
                                   "(ML) Maternity Leave",
                                   "(PL) Paternity Leave",
                                   "(PLSP) Parental Leave for Solo Parent",
                                   "(VAWC) Victims of Violence Against Women and Their Children Leave",
                                   "(BL) Bereavement Leave");
        leaveTypeComboBox.setClearButtonVisible(true);
        leaveTypeComboBox.setRequired(true);
        leaveTypeComboBox.setRequiredIndicatorVisible(true);
        if (leaveBenefitsDTO != null) leaveTypeComboBox.setValue(leaveBenefitsDTO.getLeaveType());

        leaveForYearIntegerField = new IntegerField("Leave for Year");
        leaveForYearIntegerField.setMin(LocalDate.now().getYear());
        leaveForYearIntegerField.setMax(LocalDate.now().getYear() + 1);
        leaveForYearIntegerField.setClearButtonVisible(true);
        leaveForYearIntegerField.setRequired(true);
        leaveForYearIntegerField.setRequiredIndicatorVisible(true);
        if (leaveBenefitsDTO != null) leaveForYearIntegerField.setValue(leaveBenefitsDTO.getLeaveForYear());

        leaveCountIntegerField = new IntegerField("Leave Count");
        leaveCountIntegerField.setMin(1);
        leaveCountIntegerField.setMax(20);
        leaveCountIntegerField.setClearButtonVisible(true);
        leaveCountIntegerField.setRequired(true);
        leaveCountIntegerField.setRequiredIndicatorVisible(true);
        if (leaveBenefitsDTO != null) leaveCountIntegerField.setValue(leaveBenefitsDTO.getLeaveCount());

        leaveActiveCheckbox = new Checkbox("Is Leave Active?");
        if (leaveBenefitsDTO != null) leaveActiveCheckbox.setValue(leaveBenefitsDTO.isLeaveActive());

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            saveOrUpdateLeaveBenefitsDTO();
            saveButton.getUI().ifPresent(ui -> ui.navigate(LeaveBenefitsListView.class));
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> cancelButton.getUI().ifPresent(ui -> ui.navigate(LeaveBenefitsListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMaxWidth("720px");
        buttonLayout.setPadding(true);

        leaveBenefitsDTOFormLayout.add(employeeDTOComboBox,
                                       leaveTypeComboBox,
                                       leaveForYearIntegerField,
                                       leaveCountIntegerField,
                                       leaveActiveCheckbox,
                                       buttonLayout);
        leaveBenefitsDTOFormLayout.setColspan(employeeDTOComboBox, 2);
        leaveBenefitsDTOFormLayout.setColspan(buttonLayout, 2);
        leaveBenefitsDTOFormLayout.setMaxWidth("768px");
    }

    private void saveOrUpdateLeaveBenefitsDTO() {
        String loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        if (parameterId != null) {
            leaveBenefitsDTO = leaveBenefitsService.getById(parameterId);
        } else {
            leaveBenefitsDTO = new LeaveBenefitsDTO();
            leaveBenefitsDTO.setCreatedBy(loggedInUser);
        }

        leaveBenefitsDTO.setEmployeeDTO(employeeDTOComboBox.getValue());
        leaveBenefitsDTO.setLeaveCode(StringUtil.generateLeaveCode(leaveTypeComboBox.getValue(),
                                                                   employeeDTOComboBox.getValue().getEmployeeNumber(),
                                                                   leaveForYearIntegerField.getValue().toString()));
        leaveBenefitsDTO.setLeaveType(leaveTypeComboBox.getValue());
        leaveBenefitsDTO.setLeaveForYear(leaveForYearIntegerField.getValue());
        leaveBenefitsDTO.setLeaveCount(leaveCountIntegerField.getValue());
        leaveBenefitsDTO.setLeaveActive(leaveActiveCheckbox.getValue());
        leaveBenefitsDTO.setUpdatedBy(loggedInUser);

        leaveBenefitsService.saveOrUpdate(leaveBenefitsDTO);
    }
}
