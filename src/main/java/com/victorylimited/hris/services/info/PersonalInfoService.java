package com.victorylimited.hris.services.info;

import com.victorylimited.hris.dtos.info.PersonalInfoDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.BaseService;
import org.springframework.transaction.annotation.Transactional;

public interface PersonalInfoService extends BaseService<PersonalInfoDTO> {
    @Transactional
    PersonalInfoDTO getByEmployeeDTO(EmployeeDTO employeeDTO);
}
