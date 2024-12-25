package com.victorylimited.hris.views.attendance;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.victorylimited.hris.dtos.attendance.TimesheetDTO;
import com.victorylimited.hris.services.attendance.TimesheetService;
import com.victorylimited.hris.views.MainLayout;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
               "ROLE_HR_MANAGER",
               "ROLE_HR_SUPERVISOR",
               "ROLE_HR_EMPLOYEE"})
@PageTitle("Timesheet Form")
@Route(value = "timesheet-form", layout = MainLayout.class)
public class TimesheetFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource
    private final TimesheetService timesheetService;
    private TimesheetDTO timesheetDTO;
    private UUID parameterId;

    public TimesheetFormView(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        if (s != null) {
            parameterId = UUID.fromString(s);
            timesheetDTO = timesheetService.getById(parameterId);
        }

        buildTimesheetFormLayout();
    }

    private void buildTimesheetFormLayout() {
        TimePicker logTimeInTimePicker = new TimePicker();
        TimePicker logTimeOutTimePicker = new TimePicker();
    }
}
