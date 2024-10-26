package com.victorylimited.hris.views.common;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import com.victorylimited.hris.configs.SecurityConfig;
import com.victorylimited.hris.dtos.admin.UserDTO;
import com.victorylimited.hris.dtos.info.AddressInfoDTO;
import com.victorylimited.hris.dtos.info.DependentInfoDTO;
import com.victorylimited.hris.dtos.info.PersonalInfoDTO;
import com.victorylimited.hris.services.admin.UserService;
import com.victorylimited.hris.services.info.AddressInfoService;
import com.victorylimited.hris.services.info.DependentInfoService;
import com.victorylimited.hris.services.info.PersonalInfoService;
import com.victorylimited.hris.utils.SecurityUtil;
import com.victorylimited.hris.utils.StringUtil;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.vaadin.lineawesome.LineAwesomeIcon;

@PermitAll
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {
    @Resource private final UserService userService;
    @Resource private final PersonalInfoService personalInfoService;
    @Resource private final AddressInfoService addressInfoService;
    @Resource private final DependentInfoService dependentInfoService;

    private UserDTO userDTO;
    private PersonalInfoDTO personalInfoDTO;
    private List<AddressInfoDTO> addressInfoDTOList;
    private List<DependentInfoDTO> dependentInfoDTOList;

    enum MessageLevel {
        INFO,
        SUCCESS,
        WARNING,
        DANGER
    }

    public DashboardView(UserService userService,
                         PersonalInfoService personalService,
                         AddressInfoService addressInfoService,
                         DependentInfoService dependentInfoService) {
        this.userService = userService;
        this.personalInfoService = personalService;
        this.addressInfoService = addressInfoService;
        this.dependentInfoService = dependentInfoService;

        // Gets the user data transfer object based from the logged in user.
        if (SecurityUtil.getAuthenticatedUser() != null) {
            userDTO = userService.getByUsername(SecurityUtil.getAuthenticatedUser().getUsername());
        }

        // Shows the change password dialog if the user has not changed its password for the first time.
        if (userDTO != null && !userDTO.isPasswordChanged()) {
            buildChangePasswordDialog().open();
        }

        Div profileMessageNotification = null;
        Div addressMessageNotification = null;
        Div dependentMessageNotification = null;

        if (userDTO != null) {
            personalInfoDTO = personalInfoService.getByEmployeeDTO(userDTO.getEmployeeDTO());
            addressInfoDTOList = addressInfoService.getByEmployeeDTO(userDTO.getEmployeeDTO());
            dependentInfoDTOList = dependentInfoService.getByEmployeeDTO(userDTO.getEmployeeDTO());

            // Shows the notification message if the user hasn't yet add its personal information.
            if (personalInfoDTO == null) {
                profileMessageNotification = buildNotification("Please add your personal information in 'My Profile' menu.",
                        MessageLevel.INFO,
                        LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
            } else {
                profileMessageNotification = new Div();
            }

            // Shows the notification message if the user hasn't yet add its address information.
            if (addressInfoDTOList.isEmpty()) {
                addressMessageNotification = buildNotification("Please add your address information in 'My Profile' menu.",
                        MessageLevel.INFO,
                        LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
            } else {
                addressMessageNotification = new Div();
            }

            // Shows the notification message if the user hasn't yet add its dependent information.
            if (dependentInfoDTOList.isEmpty()) {
                dependentMessageNotification = buildNotification("Please add your dependent information in 'My Profile' menu.",
                        MessageLevel.INFO,
                        LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
            } else {
                dependentMessageNotification = new Div();
            }
        }

        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
        this.setPadding(true);
        this.add(profileMessageNotification,
                 addressMessageNotification,
                 dependentMessageNotification);
    }

    private Dialog buildChangePasswordDialog() {
        Dialog changePasswordDialog = new Dialog("Change Password");

        PasswordField currentPasswordField = new PasswordField("Current Password");
        currentPasswordField.setRequired(true);
        currentPasswordField.setRequiredIndicatorVisible(true);

        PasswordField newPasswordField = new PasswordField("New Password");
        newPasswordField.setRequired(true);
        newPasswordField.setRequiredIndicatorVisible(true);

        PasswordField confirmNewPasswordField = new PasswordField("Confirm New Password");
        confirmNewPasswordField.setRequired(true);
        confirmNewPasswordField.setRequiredIndicatorVisible(true);

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(clickEvent -> {
            Notification successNotification, errorNotification;
            boolean isCurrentPasswordMatch = new SecurityConfig().passwordEncoder().matches(currentPasswordField.getValue(), userDTO.getPassword());

            if (isCurrentPasswordMatch) {
                if (newPasswordField.getValue().equals(confirmNewPasswordField.getValue())) {
                    userDTO.setPassword(StringUtil.encryptPassword(newPasswordField.getValue()));
                    userDTO.setPasswordChanged(true);
                    userDTO.setUpdatedBy(Objects.requireNonNull(SecurityUtil.getAuthenticatedUser()).getUsername());
                    userDTO.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

                    userService.saveOrUpdate(userDTO);

                    successNotification = new Notification("You have successfully updated your password.", 5000, Notification.Position.TOP_CENTER);
                    successNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    successNotification.open();

                    changePasswordDialog.close();
                } else {
                    errorNotification = new Notification("Your new password is not matched!", 5000, Notification.Position.TOP_CENTER);
                    errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    errorNotification.open();
                }
            } else {
                errorNotification = new Notification("Your current password is wrong!", 5000, Notification.Position.TOP_CENTER);
                errorNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                errorNotification.open();
            }
        });

        FormLayout changePasswordLayout = new FormLayout();
        changePasswordLayout.add(currentPasswordField,
                                 newPasswordField,
                                 confirmNewPasswordField);
        changePasswordLayout.setWidth("500px");

        changePasswordDialog.add(changePasswordLayout);
        changePasswordDialog.getFooter().add(saveButton);
        changePasswordDialog.setCloseOnOutsideClick(false);

        return changePasswordDialog;
    }

    public Div buildNotification(String message, MessageLevel messageLevel, SvgIcon svgIcon) {
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
