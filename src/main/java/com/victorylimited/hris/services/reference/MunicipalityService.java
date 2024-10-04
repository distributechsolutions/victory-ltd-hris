package com.victorylimited.hris.services.reference;

import com.victorylimited.hris.dtos.reference.MunicipalityDTO;
import com.victorylimited.hris.dtos.reference.ProvinceDTO;

import java.util.List;

public interface MunicipalityService {
    MunicipalityDTO getById(Long id);
    List<MunicipalityDTO> getAll(int page, int pageSize);
    List<MunicipalityDTO> getMunicipalityByProvince(ProvinceDTO provinceDTO);
}
