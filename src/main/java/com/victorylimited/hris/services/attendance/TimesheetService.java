package com.victorylimited.hris.services.attendance;

import com.victorylimited.hris.dtos.attendance.TimesheetDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.BaseService;

import java.util.List;

public interface TimesheetService extends BaseService<TimesheetDTO> {
    List<TimesheetDTO> findByEmployeeDTO(EmployeeDTO employeeDTO);
}
