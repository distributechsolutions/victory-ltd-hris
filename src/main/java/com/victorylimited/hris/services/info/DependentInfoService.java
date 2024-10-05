package com.victorylimited.hris.services.info;

import com.victorylimited.hris.dtos.info.DependentInfoDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DependentInfoService extends BaseService<DependentInfoDTO> {
    @Transactional
    List<DependentInfoDTO> getByEmployeeDTO(EmployeeDTO employeeDTO);
}
