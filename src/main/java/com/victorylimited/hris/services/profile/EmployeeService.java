package com.victorylimited.hris.services.profile;

import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.BaseService;

import java.util.List;

public interface EmployeeService extends BaseService<EmployeeDTO> {
    List<EmployeeDTO> getEmployeesWhoAreApprovers();
}
