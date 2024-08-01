package com.victorylimited.hris.views.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.*;

import com.victorylimited.hris.dtos.admin.UserDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.admin.UserService;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.utils.StringUtil;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;

import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.UUID;

@PageTitle("User Form")
@Route(value = "user-form", layout = MainLayout.class)
public class UserFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource private final UserService userService;
    @Resource private final EmployeeService employeeService;
    private UserDTO userDTO;
    private UUID parameterId;

    private final FormLayout userDTOFormLayout = new FormLayout();
    private TextField usernameTextField;
    private EmailField emailField;
    private ComboBox<String> roleComboBox;
    private ComboBox<EmployeeDTO> employeeDTOComboBox;
    private Checkbox accountLockedCheckbox, accountActiveCheckbox, passwordChangedCheckbox;

    public UserFormView(UserService userService,
                        EmployeeService employeeService) {
        this.userService = userService;
        this.employeeService = employeeService;

        setSizeFull();
        setMargin(true);
        add(userDTOFormLayout);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        if (s != null) {
            parameterId = UUID.fromString(s);
            userDTO = userService.getById(parameterId);
        }

        buildUserFormLayout();
    }

    private void buildUserFormLayout() {
        // Create the query object that will do the pagination of employee records in the combo box component.
        Query<EmployeeDTO, Void> query = new Query<>();

        employeeDTOComboBox = new ComboBox<>("Employee");
        employeeDTOComboBox.setItems((employeeDTO, filterString) -> employeeDTO.getEmployeeFullName().toLowerCase().contains(filterString.toLowerCase()),
                employeeService.getAll(query.getPage(), query.getPageSize()));
        employeeDTOComboBox.setItemLabelGenerator(EmployeeDTO::getEmployeeFullName);
        employeeDTOComboBox.setClearButtonVisible(true);
        employeeDTOComboBox.setRequired(true);
        employeeDTOComboBox.setRequiredIndicatorVisible(true);
        if (userDTO != null) employeeDTOComboBox.setValue(userDTO.getEmployeeDTO());

        usernameTextField = new TextField("Username");
        usernameTextField.setClearButtonVisible(true);
        usernameTextField.setRequired(true);
        usernameTextField.setRequiredIndicatorVisible(true);
        if (userDTO != null) usernameTextField.setValue(userDTO.getUsername());

        roleComboBox = new ComboBox<>("Role");
        roleComboBox.setItems("ROLE_ADMIN",
                              "ROLE_HR_MANAGER",
                              "ROLE_HR_SUPERVISOR",
                              "ROLE_HR_EMPLOYEE",
                              "ROLE_MANAGER",
                              "ROLE_SUPERVISOR",
                              "ROLE_EMPLOYEE");
        roleComboBox.setClearButtonVisible(true);
        roleComboBox.setRequired(true);
        roleComboBox.setRequiredIndicatorVisible(true);
        if (userDTO != null) roleComboBox.setValue(userDTO.getRole());

        emailField = new EmailField("Email Address");
        emailField.setHelperText("Use personal email");
        emailField.setClearButtonVisible(true);
        emailField.setSuffixComponent(LineAwesomeIcon.ENVELOPE.create());
        emailField.setRequired(true);
        emailField.setRequiredIndicatorVisible(true);
        if (userDTO != null) emailField.setValue(userDTO.getEmailAddress());

        accountActiveCheckbox = new Checkbox("Is Account Active?");
        if (userDTO != null) accountActiveCheckbox.setValue(userDTO.isAccountActive());

        accountLockedCheckbox = new Checkbox("Is Account Locked?");
        if (userDTO != null) accountLockedCheckbox.setValue(userDTO.isAccountLocked());

        passwordChangedCheckbox = new Checkbox("Is Password Changed?");
        if (userDTO != null) passwordChangedCheckbox.setValue(userDTO.isPasswordChanged());

        HorizontalLayout checkBoxLayout = new HorizontalLayout();
        checkBoxLayout.add(accountActiveCheckbox, accountLockedCheckbox, passwordChangedCheckbox);
        checkBoxLayout.setJustifyContentMode(JustifyContentMode.EVENLY);
        checkBoxLayout.setMaxWidth("720px");
        checkBoxLayout.setPadding(true);

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            saveOrUpdateUserDTO();
            saveButton.getUI().ifPresent(ui -> ui.navigate(UserListView.class));
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> cancelButton.getUI().ifPresent(ui -> ui.navigate(UserListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMaxWidth("720px");
        buttonLayout.setPadding(true);

        userDTOFormLayout.add(employeeDTOComboBox,
                              usernameTextField,
                              roleComboBox,
                              emailField,
                              checkBoxLayout,
                              buttonLayout);
        userDTOFormLayout.setColspan(checkBoxLayout, 2);
        userDTOFormLayout.setColspan(buttonLayout, 2);
        userDTOFormLayout.setMaxWidth("720px");
    }

    private void saveOrUpdateUserDTO() {
        if (parameterId != null) {
            userDTO = userService.getById(parameterId);
        } else {
            userDTO = new UserDTO();
            userDTO.setCreatedBy("admin");
        }

        userDTO.setEmployeeDTO(employeeService.getById(employeeDTOComboBox.getValue().getId()));
        userDTO.setUsername(usernameTextField.getValue());
        userDTO.setPassword(StringUtil.generateRandomPassword());
        userDTO.setRole(roleComboBox.getValue());
        userDTO.setEmailAddress(emailField.getValue());
        userDTO.setAccountActive(accountActiveCheckbox.getValue());
        userDTO.setAccountLocked(accountLockedCheckbox.getValue());
        userDTO.setPasswordChanged(passwordChangedCheckbox.getValue());
        userDTO.setUpdatedBy("admin");

        userService.saveOrUpdate(userDTO);
    }
}