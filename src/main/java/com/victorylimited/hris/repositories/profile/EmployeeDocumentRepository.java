package com.victorylimited.hris.repositories.profile;

import com.victorylimited.hris.entities.profile.Employee;
import com.victorylimited.hris.entities.profile.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, UUID> {
    @Query("SELECT ed FROM EmployeeDocument ed WHERE ed.employee = :param")
    List<EmployeeDocument> getByEmployee(@Param("param") Employee employee);
}