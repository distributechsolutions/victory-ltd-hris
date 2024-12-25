package com.victorylimited.hris.services.attendance;

import com.victorylimited.hris.dtos.attendance.LeaveFilingDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LeaveFilingService extends BaseService<LeaveFilingDTO> {
    @Transactional
    List<LeaveFilingDTO> getByEmployeeDTO(EmployeeDTO employeeDTO);

    @Transactional
    List<LeaveFilingDTO> getByLeaveStatusAndAssignedApproverEmployeeDTO(String leaveStatus, EmployeeDTO assignedApproverEmployeeDTO);
}
