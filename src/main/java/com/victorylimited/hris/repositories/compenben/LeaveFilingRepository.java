package com.victorylimited.hris.repositories.compenben;

import com.victorylimited.hris.entities.compenben.LeaveFiling;
import com.victorylimited.hris.entities.profile.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LeaveFilingRepository extends JpaRepository<LeaveFiling, UUID> {
    @Query("SELECT lf FROM LeaveFiling lf WHERE lf.leaveBenefits.employee = :param ORDER BY lf.leaveDateAndTimeFrom DESC")
    List<LeaveFiling> findByEmployee(@Param("param") Employee employee);

    @Query("SELECT lf FROm LeaveFiling lf WHERE lf.assignedApproverEmployee = :param ORDER BY lf.leaveDateAndTimeFrom DESC")
    List<LeaveFiling> findByAssignedApproverEmployee(@Param("param") Employee employee);

    @Query("""
           SELECT lf FROM LeaveFiling lf
           WHERE LOWER(lf.leaveBenefits.employee.firstName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(lf.leaveBenefits.employee.lastName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(lf.leaveBenefits.leaveCode) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(lf.leaveBenefits.leaveType) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(lf.leaveStatus) LIKE LOWER(CONCAT('%', :param, '%'))
           """)
    List<LeaveFiling> findByStringParameter(@Param("param") String parameter);
}
