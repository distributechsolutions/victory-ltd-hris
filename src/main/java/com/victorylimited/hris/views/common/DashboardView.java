package com.victorylimited.hris.views.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
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
import com.victorylimited.hris.dtos.info.PersonalInfoDTO;
import com.victorylimited.hris.services.admin.UserService;
import com.victorylimited.hris.services.info.PersonalInfoService;
import com.victorylimited.hris.utils.SecurityUtil;
import com.victorylimited.hris.utils.StringUtil;
import com.victorylimited.hris.views.MainLayout;
import com.victorylimited.hris.views.info.EmployeeInfoView;

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

    private UserDTO userDTO;
    private PersonalInfoDTO personalInfoDTO;
    private Div messageNotification;

    enum MessageLevel {
        INFO,
        SUCCESS,
        WARNING,
        DANGER
    }

    public DashboardView(UserService userService,
                         PersonalInfoService personalService) {
        this.userService = userService;
        this.personalInfoService = personalService;

        // Gets the user data transfer object based from the logged in user.
        if (SecurityUtil.getAuthenticatedUser() != null) {
            userDTO = userService.findByParameter(SecurityUtil.getAuthenticatedUser().getUsername()).get(0);
        }

        // Shows the change password dialog if the user has not changed its password for the first time.
        if (userDTO != null && !userDTO.isPasswordChanged()) {
            buildChangePasswordDialog().open();
        }

        if (userDTO != null) {
            personalInfoDTO = personalInfoService.getByEmployeeDTO(userDTO.getEmployeeDTO());

            // Shows the notification message if the user hasn't yet add its personal information.
            if (personalInfoDTO == null) {
                messageNotification = buildNotification("Please add your personal information in 'My Profile' menu.",
                                                        MessageLevel.INFO,
                                                        LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
            } else {
                messageNotification = new Div();
            }
        }

        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
        this.setPadding(true);
        this.add(messageNotification, buildDashboardMenu());
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

    private HorizontalLayout buildDashboardMenu() {
        Button myProfileButton = new Button("My Profile");
        myProfileButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        myProfileButton.addClickListener(buttonClickEvent -> buttonClickEvent.getSource().getUI().ifPresent(ui -> ui.navigate(EmployeeInfoView.class)));

        Image myProfileImage = new Image("icons/user.png", "user.png") ;
        myProfileImage.setWidth("64px");
        myProfileImage.setHeight("64px");

        Button myTimesheetButton = new Button("My Timesheet");
        myTimesheetButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        Image myTimesheetImage = new Image("icons/calendar.png", "calendar.png");
        myTimesheetImage.setWidth("64px");
        myTimesheetImage.setHeight("64px");

        Button myLeavesButton = new Button("My Leaves");
        myLeavesButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        Image myLeavesImage = new Image("icons/absentism.png", "absentism.png");
        myLeavesImage.setWidth("64px");
        myLeavesImage.setHeight("64px");

        Button myPayslipButton = new Button("My Payslip");
        myLeavesButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        Image myPayslipImage = new Image("icons/payslip.png", "payslip.png");
        myPayslipImage.setWidth("64px");
        myPayslipImage.setHeight("64px");

        Component myProfileCard = createCardComponent(myProfileImage, myProfileButton);
        Component myTimesheetCard = createCardComponent(myTimesheetImage, myTimesheetButton);
        Component myLeavesCard = createCardComponent(myLeavesImage, myLeavesButton);
        Component myPayslipCard = createCardComponent(myPayslipImage, myPayslipButton);

        HorizontalLayout menuLayout = new HorizontalLayout();
        menuLayout.add(myProfileCard, myTimesheetCard, myLeavesCard, myPayslipCard);

        return menuLayout;
    }

    private Component createCardComponent(Image image, Button button) {
        Div content = new Div();
        content.getStyle().set("margin", "10px")
                          .set("text-align", "center");
        content.add(image, button);

        Div card = new Div();
        card.getStyle().set("width", "150px")
                       .set("height", "128px")
                       .set("border-radius", "5px")
                       .set("box-shadow", "0 4px 8px 0 rgba(0,0,0,0.2)");
        card.add(content);

        return card;
    }

    public Div buildNotification(String message, MessageLevel messageLevel, SvgIcon svgIcon) {
        Div text = new Div(new Text(message));

        HorizontalLayout layout = new HorizontalLayout(svgIcon, text);
        layout.setAlignItems(Alignment.CENTER);

        Div notificationDiv = new Div();
        notificationDiv.getStyle().set("padding", "20px")
                                  .set("border-radius", "3px")
                                  .set("color", "#fdfefe")
                                  .set("margin-bottom", "15px");

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
