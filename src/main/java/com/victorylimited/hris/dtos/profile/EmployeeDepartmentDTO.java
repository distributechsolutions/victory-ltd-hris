package com.victorylimited.hris.dtos.profile;

import com.victorylimited.hris.dtos.BaseDTO;
import com.victorylimited.hris.dtos.admin.DepartmentDTO;

public class EmployeeDepartmentDTO extends BaseDTO {
    private EmployeeDTO employeeDTO;
    private DepartmentDTO departmentDTO;
    private boolean currentDepartment;

    public EmployeeDTO getEmployeeDTO() {
        return employeeDTO;
    }

    public void setEmployeeDTO(EmployeeDTO employeeDTO) {
        this.employeeDTO = employeeDTO;
    }

    public DepartmentDTO getDepartmentDTO() {
        return departmentDTO;
    }

    public void setDepartmentDTO(DepartmentDTO departmentDTO) {
        this.departmentDTO = departmentDTO;
    }

    public boolean isCurrentDepartment() {
        return currentDepartment;
    }

    public void setCurrentDepartment(boolean currentDepartment) {
        this.currentDepartment = currentDepartment;
    }
}