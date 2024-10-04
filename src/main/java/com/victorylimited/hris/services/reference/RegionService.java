package com.victorylimited.hris.services.reference;

import com.victorylimited.hris.dtos.reference.RegionDTO;

import java.util.List;

public interface RegionService {
    RegionDTO getById(Long id);
    List<RegionDTO> findAllRegions();
}
