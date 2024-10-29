package com.victorylimited.hris.services.compenben;

import com.victorylimited.hris.dtos.compenben.LeaveBenefitsDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LeaveBenefitsService extends BaseService<LeaveBenefitsDTO> {
    @Transactional
    List<LeaveBenefitsDTO> getByEmployeeDTO(EmployeeDTO employeeDTO);
}
