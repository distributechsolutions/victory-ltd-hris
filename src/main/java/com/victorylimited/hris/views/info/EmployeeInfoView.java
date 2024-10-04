package com.victorylimited.hris.views.info;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.victorylimited.hris.services.admin.UserService;
import com.victorylimited.hris.services.info.AddressInfoService;
import com.victorylimited.hris.services.info.PersonalInfoService;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.services.reference.BarangayService;
import com.victorylimited.hris.services.reference.MunicipalityService;
import com.victorylimited.hris.services.reference.ProvinceService;
import com.victorylimited.hris.services.reference.RegionService;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Employee Information")
@Route(value = "employee-information", layout = MainLayout.class)
public class EmployeeInfoView extends VerticalLayout {
    @Resource private final PersonalInfoService personalInfoService;
    @Resource private final AddressInfoService addressInfoService;
    @Resource private final UserService userService;
    @Resource private final EmployeeService employeeService;
    @Resource private final RegionService regionService;
    @Resource private final ProvinceService provinceService;
    @Resource private final MunicipalityService municipalityService;
    @Resource private final BarangayService barangayService;

    private TabSheet infoTabSheets = new TabSheet();

    public EmployeeInfoView(PersonalInfoService personalInfoService,
                            AddressInfoService addressInfoService,
                            UserService userService,
                            EmployeeService employeeService,
                            RegionService regionService,
                            ProvinceService provinceService,
                            MunicipalityService municipalityService,
                            BarangayService barangayService) {
        this.personalInfoService = personalInfoService;
        this.addressInfoService = addressInfoService;
        this.userService = userService;
        this.employeeService = employeeService;
        this.regionService = regionService;
        this.provinceService = provinceService;
        this.municipalityService = municipalityService;
        this.barangayService = barangayService;

        this.setSizeFull();
        this.createInfoTabSheets();
        this.add(infoTabSheets);
    }

    public void createInfoTabSheets() {
        PersonalInfoForm personalInfoForm = new PersonalInfoForm(personalInfoService,
                                                                 userService,
                                                                 employeeService);
        AddressInfoForm addressInfoForm = new AddressInfoForm(addressInfoService,
                                                              userService,
                                                              employeeService,
                                                              regionService,
                                                              provinceService,
                                                              municipalityService,
                                                              barangayService);

        infoTabSheets.add("Personal", personalInfoForm);
        infoTabSheets.add("Addresses", addressInfoForm);
        infoTabSheets.add("Dependents", new Div());
    }

}
