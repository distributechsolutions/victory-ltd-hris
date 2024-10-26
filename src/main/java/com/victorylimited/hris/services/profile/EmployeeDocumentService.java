package com.victorylimited.hris.services.profile;

import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDocumentDTO;
import com.victorylimited.hris.services.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeDocumentService extends BaseService<EmployeeDocumentDTO> {
    @Transactional
    List<EmployeeDocumentDTO> getByEmployeeDTO(EmployeeDTO employeeDTO);
}
