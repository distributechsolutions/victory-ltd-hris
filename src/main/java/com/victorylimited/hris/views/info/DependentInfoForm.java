package com.victorylimited.hris.views.info;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.victorylimited.hris.dtos.admin.UserDTO;
import com.victorylimited.hris.dtos.info.DependentInfoDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.admin.UserService;
import com.victorylimited.hris.services.info.DependentInfoService;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.utils.SecurityUtil;
import com.victorylimited.hris.views.common.DashboardView;

import jakarta.annotation.Resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.vaadin.lineawesome.LineAwesomeIcon;

public class DependentInfoForm extends VerticalLayout {
    @Resource private final DependentInfoService dependentInfoService;
    @Resource private final UserService userService;
    @Resource private final EmployeeService employeeService;

    private List<DependentInfoDTO> dependentInfoDTOList;
    private DependentInfoDTO dependentInfoDTO;
    private UserDTO userDTO;
    private EmployeeDTO employeeDTO;

    private String loggedInUser;

    private Grid<DependentInfoDTO> dependentInfoDTOGrid;
    private FormLayout dependentInfoFormLayout;
    private TextField fullNameTextField;
    private DatePicker dateOfBirthDatePicker;
    private ComboBox<String> relationshipComboBox;
    private Button saveButton;
    private Button cancelButton;
    private Button viewButton;
    private Button editButton;

    public DependentInfoForm(DependentInfoService dependentInfoService,
                             UserService userService,
                             EmployeeService employeeService) {
        this.dependentInfoService = dependentInfoService;
        this.userService = userService;
        this.employeeService = employeeService;

        loggedInUser = Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername();

        if (loggedInUser != null) {
            userDTO = userService.getByUsername(loggedInUser);
        }

        if (userDTO != null) {
            employeeDTO = userDTO.getEmployeeDTO();
        }

        if (employeeDTO != null) {
            dependentInfoDTOList = dependentInfoService.getByEmployeeDTO(employeeDTO);
        }

        dependentInfoDTOGrid = new Grid<>(DependentInfoDTO.class, false);
        dependentInfoFormLayout = new FormLayout();

        this.buildDependentInfoFormLayout();
        this.buildDependentInfoDTOGrid();

        this.add(dependentInfoFormLayout, dependentInfoDTOGrid);
    }

    private void buildDependentInfoFormLayout() {
        fullNameTextField = new TextField("Full Name");
        fullNameTextField.setRequired(true);
        fullNameTextField.setRequiredIndicatorVisible(true);

        dateOfBirthDatePicker = new DatePicker("Date of birth");
        dateOfBirthDatePicker.setRequired(true);
        dateOfBirthDatePicker.setRequiredIndicatorVisible(true);

        relationshipComboBox = new ComboBox<>("Relationship");
        relationshipComboBox.setItems("Father", "Mother", "Sibling", "Grandfather", "Grandmother", "Spouse", "Son", "Daughter");
        relationshipComboBox.setRequired(true);
        relationshipComboBox.setRequiredIndicatorVisible(true);

        saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            // Save the dependent and clear the fields.
            this.saveDependentInfoDTO();
            this.clearFields();

            // Show notification message.
            Notification notification = Notification.show("You have successfully saved your dependent information.",  5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Update the dependent grid table.
            dependentInfoDTOList = dependentInfoService.getByEmployeeDTO(employeeDTO);
            dependentInfoDTOGrid.setItems(dependentInfoDTOList);
        });

        cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> buttonClickEvent.getSource().getUI().ifPresent(ui -> ui.navigate(DashboardView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setPadding(true);

        dependentInfoFormLayout.setColspan(fullNameTextField, 2);
        dependentInfoFormLayout.setColspan(buttonLayout, 2);
        dependentInfoFormLayout.add(fullNameTextField,
                                    dateOfBirthDatePicker,
                                    relationshipComboBox,
                                    buttonLayout);
        dependentInfoFormLayout.setWidth("768px");
    }

    private void buildDependentInfoDTOGrid() {
        dependentInfoDTOGrid.addColumn(DependentInfoDTO::getFullName).setHeader("Name");
        dependentInfoDTOGrid.addColumn(new LocalDateRenderer<>(DependentInfoDTO::getDateOfBirth, "MMM dd, yyyy")).setHeader("Date of Birth");
        dependentInfoDTOGrid.addColumn(DependentInfoDTO::getAge).setHeader("Age");
        dependentInfoDTOGrid.addColumn(DependentInfoDTO::getRelationship).setHeader("Relationship");
        dependentInfoDTOGrid.addComponentColumn(addressDTO -> this.buildDependentInfoRowToolbar()).setHeader("Action");
        dependentInfoDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                                              GridVariant.LUMO_COLUMN_BORDERS,
                                              GridVariant.LUMO_WRAP_CELL_CONTENT);
        dependentInfoDTOGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        dependentInfoDTOGrid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        dependentInfoDTOGrid.setItems(dependentInfoDTOList);
    }

    private Component buildDependentInfoRowToolbar() {
        HorizontalLayout rowToolbarLayout = new HorizontalLayout();

        viewButton = new Button();
        viewButton.setTooltipText("View Address");
        viewButton.setIcon(LineAwesomeIcon.SEARCH_SOLID.create());
        viewButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        viewButton.addClickListener(buttonClickEvent -> this.loadDependentInfoDTO(true));

        editButton = new Button();
        editButton.setTooltipText("Edit Address");
        editButton.setIcon(LineAwesomeIcon.PENCIL_ALT_SOLID.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        editButton.addClickListener(buttonClickEvent -> this.loadDependentInfoDTO(false));

        rowToolbarLayout.add(viewButton, editButton);
        rowToolbarLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        rowToolbarLayout.getStyle().set("flex-wrap", "wrap");

        return rowToolbarLayout;
    }

    private void saveDependentInfoDTO() {
        if (dependentInfoDTO == null) {
            dependentInfoDTO = new DependentInfoDTO();
            dependentInfoDTO.setEmployeeDTO(employeeDTO);
            dependentInfoDTO.setCreatedBy(loggedInUser);
        }

        dependentInfoDTO.setFullName(fullNameTextField.getValue());
        dependentInfoDTO.setDateOfBirth(dateOfBirthDatePicker.getValue());
        dependentInfoDTO.setAge(LocalDate.now().getYear() - dateOfBirthDatePicker.getValue().getYear());
        dependentInfoDTO.setRelationship(relationshipComboBox.getValue());
        dependentInfoDTO.setUpdatedBy(loggedInUser);

        dependentInfoService.saveOrUpdate(dependentInfoDTO);
    }

    private void clearFields() {
        fullNameTextField.clear();
        dateOfBirthDatePicker.clear();
        relationshipComboBox.clear();
    }

    private void loadDependentInfoDTO(boolean readOnly) {
        dependentInfoDTO = dependentInfoDTOGrid.getSelectionModel().getFirstSelectedItem().get();

        fullNameTextField.setValue(dependentInfoDTO.getFullName());
        fullNameTextField.setReadOnly(readOnly);

        dateOfBirthDatePicker.setValue(dependentInfoDTO.getDateOfBirth());
        dateOfBirthDatePicker.setReadOnly(readOnly);

        relationshipComboBox.setValue(dependentInfoDTO.getRelationship());
        relationshipComboBox.setReadOnly(readOnly);
    }
}
