package com.victorylimited.hris.services.impls.compenben;

import com.victorylimited.hris.dtos.compenben.RatesDTO;
import com.victorylimited.hris.entities.compenben.Rates;
import com.victorylimited.hris.repositories.compenben.RatesRepository;
import com.victorylimited.hris.repositories.profile.EmployeeRepository;
import com.victorylimited.hris.services.compenben.RatesService;
import com.victorylimited.hris.services.impls.profile.EmployeeServiceImpl;
import com.victorylimited.hris.services.profile.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RatesServiceImpl implements RatesService {
    private final Logger logger = LoggerFactory.getLogger(RatesServiceImpl.class);

    private final RatesRepository ratesRepository;
    private final EmployeeRepository employeeRepository;

    public RatesServiceImpl(RatesRepository ratesRepository, EmployeeRepository employeeRepository) {
        this.ratesRepository = ratesRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void saveOrUpdate(RatesDTO object) {
        Rates rates;
        String logMessage;

        if (object.getId() != null) {
            rates = ratesRepository.getReferenceById(object.getId());
            logMessage = "Employee's rates record with id ".concat(object.getId().toString()).concat(" is successfully updated.");
        } else {
            rates = new Rates();
            rates.setCreatedBy(object.getCreatedBy());
            rates.setDateAndTimeCreated(LocalDateTime.now(ZoneId.of("Asia/Manila")));
            logMessage = "Employee's rates record is successfully created.";
        }

        rates.setEmployee(employeeRepository.getReferenceById(object.getEmployeeDTO().getId()));
        rates.setMonthlyRate(object.getMonthlyRate());
        rates.setDailyRate(object.getDailyRate());
        rates.setHourlyRate(object.getHourlyRate());
        rates.setOvertimeHourlyRate(object.getOvertimeHourlyRate());
        rates.setLateHourlyRate(object.getLateHourlyRate());
        rates.setAbsentDailyRate(object.getAbsentDailyRate());
        rates.setAdditionalAllowance(object.getAdditionalAllowance());
        rates.setCurrentRates(object.isCurrentRates());
        rates.setUpdatedBy(object.getUpdatedBy());
        rates.setDateAndTimeUpdated(LocalDateTime.now(ZoneId.of("Asia/Manila")));

        ratesRepository.save(rates);
        logger.info(logMessage);
    }

    @Override
    public RatesDTO getById(UUID id) {
        logger.info("Retrieving employee's rates record with UUID ".concat(id.toString()));

        Rates rates = ratesRepository.getReferenceById(id);
        RatesDTO ratesDTO = new RatesDTO();

        ratesDTO.setId(rates.getId());
        ratesDTO.setEmployeeDTO(new EmployeeServiceImpl(employeeRepository).getById(rates.getEmployee().getId()));
        ratesDTO.setMonthlyRate(rates.getMonthlyRate());
        ratesDTO.setDailyRate(rates.getDailyRate());
        ratesDTO.setHourlyRate(rates.getHourlyRate());
        ratesDTO.setOvertimeHourlyRate(rates.getOvertimeHourlyRate());
        ratesDTO.setLateHourlyRate(rates.getLateHourlyRate());
        ratesDTO.setAbsentDailyRate(rates.getAbsentDailyRate());
        ratesDTO.setAdditionalAllowance(rates.getAdditionalAllowance());
        ratesDTO.setCurrentRates(rates.isCurrentRates());
        ratesDTO.setUpdatedBy(rates.getUpdatedBy());
        ratesDTO.setCreatedBy(rates.getCreatedBy());
        ratesDTO.setDateAndTimeCreated(rates.getDateAndTimeCreated());
        ratesDTO.setUpdatedBy(rates.getUpdatedBy());
        ratesDTO.setDateAndTimeUpdated(rates.getDateAndTimeUpdated());

        logger.info("Employee's rates record with id ".concat(id.toString()).concat(" is successfully retrieved."));

        return ratesDTO;
    }

    @Override
    public void delete(RatesDTO object) {
        if (object != null) {
            logger.warn("You are about to delete the employee's rates record permanently.");

            String id = object.getId().toString();
            Rates rates = ratesRepository.getReferenceById(object.getId());
            ratesRepository.delete(rates);

            logger.info("Employee's rates record with id ".concat(id).concat(" is successfully deleted."));
        }
    }

    @Override
    public List<RatesDTO> getAll(int page, int pageSize) {
        logger.info("Retrieving employee's rates records from the database.");
        List<Rates> ratesList = ratesRepository.findAll(PageRequest.of(page, pageSize)).stream().toList();

        logger.info("Employee's rates records successfully retrieved.");
        List<RatesDTO> ratesDTOList = new ArrayList<>();

        if (!ratesList.isEmpty()) {
            EmployeeService employeeService = new EmployeeServiceImpl(employeeRepository);

            for (Rates rates : ratesList) {
                RatesDTO ratesDTO = new RatesDTO();

                ratesDTO.setId(rates.getId());
                ratesDTO.setEmployeeDTO(employeeService.getById(rates.getEmployee().getId()));
                ratesDTO.setMonthlyRate(rates.getMonthlyRate());
                ratesDTO.setDailyRate(rates.getDailyRate());
                ratesDTO.setHourlyRate(rates.getHourlyRate());
                ratesDTO.setOvertimeHourlyRate(rates.getOvertimeHourlyRate());
                ratesDTO.setLateHourlyRate(rates.getLateHourlyRate());
                ratesDTO.setAbsentDailyRate(rates.getAbsentDailyRate());
                ratesDTO.setAdditionalAllowance(rates.getAdditionalAllowance());
                ratesDTO.setCurrentRates(rates.isCurrentRates());
                ratesDTO.setUpdatedBy(rates.getUpdatedBy());
                ratesDTO.setCreatedBy(rates.getCreatedBy());
                ratesDTO.setDateAndTimeCreated(rates.getDateAndTimeCreated());
                ratesDTO.setUpdatedBy(rates.getUpdatedBy());
                ratesDTO.setDateAndTimeUpdated(rates.getDateAndTimeUpdated());

                ratesDTOList.add(ratesDTO);
            }

            logger.info(String.valueOf(ratesList.size()).concat(" record(s) found."));
        }

        return ratesDTOList;
    }

    @Override
    public List<RatesDTO> findByParameter(String param) {
        List<RatesDTO> ratesDTOList = new ArrayList<>();
        List<Rates> ratesList = null;

        logger.info("Retrieving employee's rates records with search parameter '%".concat(param).concat("%' from the database."));

        if (param.equalsIgnoreCase("Yes") || param.equalsIgnoreCase("No")) {
            ratesList = ratesRepository.findByBooleanParameter(param.equalsIgnoreCase("Yes"));
        } else {
            ratesList = ratesRepository.findByStringParameter(param);
        }

        if (!ratesList.isEmpty()) {
            logger.info("Employee's rates records with parameter '%".concat(param).concat("%' has successfully retrieved."));

            EmployeeService employeeService = new EmployeeServiceImpl(employeeRepository);

            for (Rates rates : ratesList) {
                RatesDTO ratesDTO = new RatesDTO();

                ratesDTO.setId(rates.getId());
                ratesDTO.setEmployeeDTO(employeeService.getById(rates.getEmployee().getId()));
                ratesDTO.setMonthlyRate(rates.getMonthlyRate());
                ratesDTO.setDailyRate(rates.getDailyRate());
                ratesDTO.setHourlyRate(rates.getHourlyRate());
                ratesDTO.setOvertimeHourlyRate(rates.getOvertimeHourlyRate());
                ratesDTO.setLateHourlyRate(rates.getLateHourlyRate());
                ratesDTO.setAbsentDailyRate(rates.getAbsentDailyRate());
                ratesDTO.setAdditionalAllowance(rates.getAdditionalAllowance());
                ratesDTO.setCurrentRates(rates.isCurrentRates());
                ratesDTO.setUpdatedBy(rates.getUpdatedBy());
                ratesDTO.setCreatedBy(rates.getCreatedBy());
                ratesDTO.setDateAndTimeCreated(rates.getDateAndTimeCreated());
                ratesDTO.setUpdatedBy(rates.getUpdatedBy());
                ratesDTO.setDateAndTimeUpdated(rates.getDateAndTimeUpdated());

                ratesDTOList.add(ratesDTO);
            }

            logger.info(String.valueOf(ratesList.size()).concat(" record(s) found."));
        }

        return ratesDTOList;
    }
}
