package com.victorylimited.hris.entities.profile;

import com.victorylimited.hris.entities.BaseEntity;
import com.victorylimited.hris.entities.admin.Position;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vlh_employee_position")
public class EmployeePosition extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", referencedColumnName = "id", nullable = false)
    private Position position;

    @Column(name = "is_active_position", nullable = false)
    private boolean activePosition;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isActivePosition() {
        return activePosition;
    }

    public void setActivePosition(boolean activePosition) {
        this.activePosition = activePosition;
    }
}
