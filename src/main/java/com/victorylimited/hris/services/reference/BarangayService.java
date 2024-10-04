package com.victorylimited.hris.services.reference;

import com.victorylimited.hris.dtos.reference.BarangayDTO;
import com.victorylimited.hris.dtos.reference.MunicipalityDTO;

import java.util.List;

public interface BarangayService {
    BarangayDTO getById(Long id);
    List<BarangayDTO> getAll(int page, int pageSize);
    List<BarangayDTO> getBarangayByMunicipality(MunicipalityDTO municipalityDTO);
}
