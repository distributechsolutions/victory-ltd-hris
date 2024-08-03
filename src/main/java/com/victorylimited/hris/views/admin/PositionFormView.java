package com.victorylimited.hris.views.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import com.victorylimited.hris.dtos.admin.PositionDTO;
import com.victorylimited.hris.services.admin.PositionService;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;

import java.util.UUID;

@PageTitle("Position Form")
@Route(value = "position-form", layout = MainLayout.class)
public class PositionFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource private final PositionService positionService;
    private PositionDTO positionDTO;
    private UUID parameterId;

    private final FormLayout positionDTOFormLayout = new FormLayout();
    private TextField codeTextField, nameTextField;

    public PositionFormView(PositionService positionService) {
        this.positionService = positionService;

        setSizeFull();
        setMargin(true);
        add(positionDTOFormLayout);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
        if (parameter != null) {
            parameterId = UUID.fromString(parameter);
            positionDTO = positionService.getById(parameterId);
        }

        buildPositionFormLayout();
    }

    private void buildPositionFormLayout() {
        codeTextField = new TextField("Code");
        codeTextField.setClearButtonVisible(true);
        codeTextField.setRequired(true);
        codeTextField.setRequiredIndicatorVisible(true);
        if (positionDTO != null) codeTextField.setValue(positionDTO.getCode());

        nameTextField = new TextField("Name");
        nameTextField.setClearButtonVisible(true);
        nameTextField.setRequired(true);
        nameTextField.setRequiredIndicatorVisible(true);
        if (positionDTO != null) nameTextField.setValue(positionDTO.getName());

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            saveOrUpdatePositionDTO();
            saveButton.getUI().ifPresent(ui -> ui.navigate(PositionListView.class));
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> cancelButton.getUI().ifPresent(ui -> ui.navigate(PositionListView.class)));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMaxWidth("720px");
        buttonLayout.setPadding(true);

        positionDTOFormLayout.add(codeTextField,
                                  nameTextField,
                                  buttonLayout);
        positionDTOFormLayout.setColspan(buttonLayout, 2);
        positionDTOFormLayout.setMaxWidth("720px");
    }

    private void saveOrUpdatePositionDTO() {
        if (parameterId != null) {
            positionDTO = positionService.getById(parameterId);
        } else {
            positionDTO = new PositionDTO();
            positionDTO.setCreatedBy("admin");
        }

        positionDTO.setCode(codeTextField.getValue());
        positionDTO.setName(nameTextField.getValue());
        positionDTO.setUpdatedBy("admin");

        positionService.saveOrUpdate(positionDTO);
    }
}
