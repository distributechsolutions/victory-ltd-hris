package com.victorylimited.hris.views.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import com.victorylimited.hris.configs.SecurityConfig;
import com.victorylimited.hris.dtos.admin.UserDTO;
import com.victorylimited.hris.dtos.compenben.LeaveBenefitsDTO;
import com.victorylimited.hris.dtos.compenben.LeaveFilingDTO;
import com.victorylimited.hris.dtos.info.AddressInfoDTO;
import com.victorylimited.hris.dtos.info.DependentInfoDTO;
import com.victorylimited.hris.dtos.info.PersonalInfoDTO;
import com.victorylimited.hris.services.admin.UserService;
import com.victorylimited.hris.services.compenben.LeaveBenefitsService;
import com.victorylimited.hris.services.compenben.LeaveFilingService;
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
    @Resource private final LeaveFilingService leaveFilingService;
    @Resource private final LeaveBenefitsService leaveBenefitsService;

    private UserDTO userDTO;
    private PersonalInfoDTO personalInfoDTO;
    private List<AddressInfoDTO> addressInfoDTOList;
    private List<DependentInfoDTO> dependentInfoDTOList;
    private List<LeaveFilingDTO> pendingLeaveFilingDTOList;

    private Grid<LeaveFilingDTO> pendingLeaveFilingGrid = new Grid<>(LeaveFilingDTO.class, false);

    String loggedInUser;

    enum MessageLevel {
        INFO,
        SUCCESS,
        WARNING,
        DANGER
    }

    public DashboardView(UserService userService,
                         PersonalInfoService personalService,
                         AddressInfoService addressInfoService,
                         DependentInfoService dependentInfoService,
                         LeaveFilingService leaveFilingService,
                         LeaveBenefitsService leaveBenefitsService) {
        this.userService = userService;
        this.personalInfoService = personalService;
        this.addressInfoService = addressInfoService;
        this.dependentInfoService = dependentInfoService;
        this.leaveFilingService = leaveFilingService;
        this.leaveBenefitsService = leaveBenefitsService;

        // Gets the user data transfer object based from the logged in user.
        loggedInUser = SecurityUtil.getAuthenticatedUser().getUsername();

        if (SecurityUtil.getAuthenticatedUser() != null) {
            userDTO = userService.getByUsername(loggedInUser);
        }

        // Shows the change password dialog if the user has not changed its password for the first time.
        if (userDTO != null && !userDTO.isPasswordChanged()) {
            buildChangePasswordDialog().open();
        }

        // This will show the notification messages for the incomplete employee profiles.
        Div profileMessageNotification = null;
        Div addressMessageNotification = null;
        Div dependentMessageNotification = null;
        Div notificationSectionDiv = null;
        Div approvalSectionDiv = null;

        H3 headerNotification = new H3("Notifications");
        headerNotification.getStyle().set("padding-bottom", "10px");

        if (userDTO != null) {
            notificationSectionDiv = new Div();
            notificationSectionDiv.add(headerNotification);

            personalInfoDTO = personalInfoService.getByEmployeeDTO(userDTO.getEmployeeDTO());
            addressInfoDTOList = addressInfoService.getByEmployeeDTO(userDTO.getEmployeeDTO());
            dependentInfoDTOList = dependentInfoService.getByEmployeeDTO(userDTO.getEmployeeDTO());

            // Shows the notification message if the user hasn't yet add its personal information.
            if (personalInfoDTO == null) {
                profileMessageNotification = buildNotification("Please add your personal information in 'My Profile' menu.",
                                                               MessageLevel.INFO,
                                                               LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
                notificationSectionDiv.add(profileMessageNotification);
            }

            // Shows the notification message if the user hasn't yet add its address information.
            if (addressInfoDTOList.isEmpty()) {
                addressMessageNotification = buildNotification("Please add your address information in 'My Profile' menu.",
                                                               MessageLevel.INFO,
                                                               LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
                notificationSectionDiv.add(addressMessageNotification);
            }

            // Shows the notification message if the user hasn't yet add its dependent information.
            if (dependentInfoDTOList.isEmpty()) {
                dependentMessageNotification = buildNotification("Please add your dependent information in 'My Profile' menu.",
                                                                 MessageLevel.INFO,
                                                                 LineAwesomeIcon.INFO_CIRCLE_SOLID.create());
                notificationSectionDiv.add(dependentMessageNotification);
            }

            if (personalInfoDTO != null && !addressInfoDTOList.isEmpty() && !dependentInfoDTOList.isEmpty()) {
                Span messageSpan = new Span("No notifications found. You are all caught up.");
                notificationSectionDiv.add(messageSpan);
            }

            this.add(notificationSectionDiv, this.buildLeaveBenefitsSection());
        }

        // Shows the list of leave approvals only for users whose roles are supervisor or manager.
        if (userDTO != null && (userDTO.getRole().equals("ROLE_HR_MANAGER") ||
                                userDTO.getRole().equals("ROLE_MANAGER") ||
                                userDTO.getRole().equals("ROLE_HR_SUPERVISOR") ||
                                userDTO.getRole().equals("ROLE_SUPERVISOR"))) {
            approvalSectionDiv = this.buildApprovalSection();

            this.add(approvalSectionDiv);
        }

        this.setSizeFull();
        this.setAlignItems(Alignment.STRETCH);
        this.setPadding(true);
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

    private Div buildNotification(String message, MessageLevel messageLevel, SvgIcon svgIcon) {
        Div text = new Div(new Text(message));

        HorizontalLayout layout = new HorizontalLayout(svgIcon, text);
        layout.setAlignItems(Alignment.CENTER);

        Div notificationDiv = new Div();
        notificationDiv.getStyle().set("padding", "20px")
                                  .set("border-radius", "5px")
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

    private Div buildApprovalSection() {
        pendingLeaveFilingDTOList = leaveFilingService.getByLeaveStatusAndAssignedApproverEmployeeDTO("PENDING", userDTO.getEmployeeDTO());

        pendingLeaveFilingGrid.addColumn(leaveFilingDTO -> leaveFilingDTO.getLeaveBenefitsDTO().getEmployeeDTO().getFirstName() +
                                                           " " +
                                                           leaveFilingDTO.getLeaveBenefitsDTO().getEmployeeDTO().getLastName())
                              .setHeader("Filed by");
        pendingLeaveFilingGrid.addColumn(leaveFilingDTO -> leaveFilingDTO.getLeaveBenefitsDTO().getLeaveType())
                              .setHeader("Leave Type");
        pendingLeaveFilingGrid.addColumn(new LocalDateTimeRenderer<>(LeaveFilingDTO::getLeaveDateAndTimeFrom, "MMM dd, yyyy HH:mm"))
                              .setHeader("Leave From");
        pendingLeaveFilingGrid.addColumn(new LocalDateTimeRenderer<>(LeaveFilingDTO::getLeaveDateAndTimeTo, "MMM dd, yyyy HH:mm"))
                              .setHeader("Leave To");
        pendingLeaveFilingGrid.addColumn(LeaveFilingDTO::getLeaveCount)
                              .setHeader("No. of Leaves Filed");
        pendingLeaveFilingGrid.addColumn(LeaveFilingDTO::getRemarks)
                              .setHeader("Remarks");
        pendingLeaveFilingGrid.addComponentColumn(addressDTO -> this.buildRowToolbar())
                              .setHeader("Action");
        pendingLeaveFilingGrid.setItems(pendingLeaveFilingDTOList);
        pendingLeaveFilingGrid.setAllRowsVisible(true);
        pendingLeaveFilingGrid.setEmptyStateText("No pending leave approvals found.");
        pendingLeaveFilingGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                                                GridVariant.LUMO_COLUMN_BORDERS,
                                                GridVariant.LUMO_WRAP_CELL_CONTENT);

        H3 header3 = new H3("Leave Approvals");
        header3.getStyle().set("padding-bottom", "10px");

        Div approvalSection = new Div();
        approvalSection.add(header3, pendingLeaveFilingGrid);

        return approvalSection;
    }

    private Component buildRowToolbar() {
        HorizontalLayout rowToolbarLayout = new HorizontalLayout();

        Button approveButton = new Button();
        approveButton.setTooltipText("Approve Leave");
        approveButton.setIcon(LineAwesomeIcon.THUMBS_UP.create());
        approveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        approveButton.addClickListener(buttonClickEvent -> approveButton.getUI().ifPresent(ui -> {
            if (pendingLeaveFilingGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                if (pendingLeaveFilingGrid.getSelectionModel().getFirstSelectedItem().get().getLeaveStatus().equals("PENDING")) {
                    LeaveFilingDTO employeeLeaveFilingDTO = pendingLeaveFilingGrid.getSelectionModel().getFirstSelectedItem().get();
                    LeaveBenefitsDTO leaveBenefitsDTO = employeeLeaveFilingDTO.getLeaveBenefitsDTO();

                    if (leaveBenefitsDTO.getLeaveType().contains("Unpaid")) {
                        // Set the status of leave filing to "APPROVED" and save.
                        employeeLeaveFilingDTO.setLeaveStatus("APPROVED");
                        employeeLeaveFilingDTO.setUpdatedBy(loggedInUser);

                        leaveFilingService.saveOrUpdate(employeeLeaveFilingDTO);

                        // Update the data grid.
                        pendingLeaveFilingGrid.setItems(leaveFilingService.getByLeaveStatusAndAssignedApproverEmployeeDTO("PENDING", userDTO.getEmployeeDTO()));

                        // Show notification message.
                        Notification notification = Notification.show("You have successfully approved the leave request.",  5000, Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    } else {
                        // Check first if the remaining leave count balance is greater than the applied leave.
                        if (leaveBenefitsDTO.getLeaveCount() > employeeLeaveFilingDTO.getLeaveCount()) {
                            // Set the status of leave filing to "APPROVED" and save.
                            employeeLeaveFilingDTO.setLeaveStatus("APPROVED");
                            employeeLeaveFilingDTO.setUpdatedBy(loggedInUser);

                            leaveFilingService.saveOrUpdate(employeeLeaveFilingDTO);

                            // Deduct the leave count of the selected leave benefit.
                            leaveBenefitsDTO.setLeaveCount(leaveBenefitsDTO.getLeaveCount() - employeeLeaveFilingDTO.getLeaveCount());
                            leaveBenefitsDTO.setUpdatedBy(loggedInUser);

                            leaveBenefitsService.saveOrUpdate(leaveBenefitsDTO);

                            // Update the data grid.
                            pendingLeaveFilingGrid.setItems(leaveFilingService.getByLeaveStatusAndAssignedApproverEmployeeDTO("PENDING", userDTO.getEmployeeDTO()));

                            // Show notification message.
                            Notification notification = Notification.show("You have successfully approved the leave request.",  5000, Notification.Position.TOP_CENTER);
                            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        } else {
                            Notification notification = Notification.show("You cannot approve the leave request.\n The requested leave days is greater than the remaining leave days.",  5000, Notification.Position.TOP_CENTER);
                            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    }
                }
            }
        }));

        Button rejectButton = new Button();
        rejectButton.setTooltipText("Reject Leave");
        rejectButton.setIcon(LineAwesomeIcon.THUMBS_DOWN.create());
        rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        rejectButton.addClickListener(buttonClickEvent -> rejectButton.getUI().ifPresent(ui -> {
            if (pendingLeaveFilingGrid.getSelectionModel().getFirstSelectedItem().isPresent()) {
                if (pendingLeaveFilingGrid.getSelectionModel().getFirstSelectedItem().get().getLeaveStatus().equals("PENDING")) {
                    // Show the confirmation dialog.
                    ConfirmDialog confirmDialog = new ConfirmDialog();
                    confirmDialog.setHeader("Reject Leave Filing");
                    confirmDialog.setText(new Html("<p>Are you sure you want to reject the filed leave?</p>"));
                    confirmDialog.setConfirmText("Yes");
                    confirmDialog.addConfirmListener(confirmEvent -> {
                        // Set the status of leave filing to "REJECTED" and save.
                        LeaveFilingDTO employeeLeaveFilingDTO = pendingLeaveFilingGrid.getSelectionModel().getFirstSelectedItem().get();
                        employeeLeaveFilingDTO.setLeaveStatus("REJECTED");
                        employeeLeaveFilingDTO.setUpdatedBy(loggedInUser);

                        leaveFilingService.saveOrUpdate(employeeLeaveFilingDTO);

                        // Show notification message.
                        Notification notification = Notification.show("You have successfully rejected the leave request.",  5000, Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                        // Close the confirmation dialog.
                        confirmDialog.close();

                        // Update the data grid.
                        pendingLeaveFilingDTOList = leaveFilingService.getByLeaveStatusAndAssignedApproverEmployeeDTO("PENDING", employeeLeaveFilingDTO.getLeaveBenefitsDTO().getEmployeeDTO());
                    });
                    confirmDialog.setCancelable(true);
                    confirmDialog.setCancelText("No");
                    confirmDialog.open();
                }
            }
        }));

        rowToolbarLayout.add(approveButton, rejectButton);
        rowToolbarLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        rowToolbarLayout.getStyle().set("flex-wrap", "wrap");

        return rowToolbarLayout;
    }

    private Div buildLeaveBenefitsSection() {
        Div leaveBenefitsSection = new Div();
        HorizontalLayout leaveBenefitsLayout = new HorizontalLayout();
        List<LeaveBenefitsDTO> leaveBenefitsDTOList = leaveBenefitsService.getByEmployeeDTO(userDTO.getEmployeeDTO());
        List<LeaveBenefitsDTO> filteredLeaveBenefitsDTOList = leaveBenefitsDTOList.stream().filter(leaveBenefitsDTO -> leaveBenefitsDTO.getLeaveForYear() == LocalDateTime.now(ZoneId.of("Asia/Manila")).getYear() && !leaveBenefitsDTO.getLeaveType().contains("Unpaid")).toList();

        leaveBenefitsSection.add(new H3("Leave Benefits"));

        if (!leaveBenefitsDTOList.isEmpty()) {
            for (LeaveBenefitsDTO leaveBenefitsDTO : filteredLeaveBenefitsDTOList) {
                H1 leaveCountHeader = new H1(String.valueOf(leaveBenefitsDTO.getLeaveCount()));
                Span leaveTypeSpan = new Span(leaveBenefitsDTO.getLeaveType());

                Div leaveDiv = new Div(leaveCountHeader, leaveTypeSpan);
                leaveDiv.getStyle().set("text-align", "center");

                // Change the colors of leave count headers.
                if (leaveBenefitsDTO.getLeaveCount() == 3 && leaveBenefitsDTO.getLeaveCount() == 2) {
                    leaveCountHeader.getStyle().set("color", "#ffc107");
                } else if (leaveBenefitsDTO.getLeaveCount() == 1) {
                    leaveCountHeader.getStyle().set("color", "#dc3545");
                } else if (leaveBenefitsDTO.getLeaveCount() > 3) {
                    leaveCountHeader.getStyle().set("color", "#198754");
                } else {
                    leaveCountHeader.getStyle().set("color", "#adb5bd");
                }

                leaveBenefitsLayout.add(leaveDiv);
            }

            leaveBenefitsSection.add(leaveBenefitsLayout);
        } else {
            Span messageSpan = new Span("No leave benefits currently assigned  to you.");
            leaveBenefitsSection.add(messageSpan);
        }

        return leaveBenefitsSection;
    }
}
