package com.victorylimited.hris.services.info;

import com.victorylimited.hris.dtos.info.AddressInfoDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.services.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressInfoService extends BaseService<AddressInfoDTO> {
    @Transactional
    List<AddressInfoDTO> getByEmployeeDTO(EmployeeDTO employeeDTO);
}
