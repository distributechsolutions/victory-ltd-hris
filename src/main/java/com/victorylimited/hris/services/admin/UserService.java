package com.victorylimited.hris.services.admin;

import com.victorylimited.hris.dtos.admin.UserDTO;
import com.victorylimited.hris.services.BaseService;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends BaseService<UserDTO> {
    @Transactional
    UserDTO getByUsername(String username);
}
