package com.victorylimited.hris.views.profile;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.*;
import com.victorylimited.hris.dtos.profile.EmployeeDTO;
import com.victorylimited.hris.dtos.profile.EmployeeDocumentDTO;
import com.victorylimited.hris.services.profile.EmployeeDocumentService;
import com.victorylimited.hris.services.profile.EmployeeService;
import com.victorylimited.hris.views.MainLayout;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@RolesAllowed({"ROLE_ADMIN",
        "ROLE_HR_MANAGER",
        "ROLE_HR_SUPERVISOR",
        "ROLE_HR_EMPLOYEE"})
@PageTitle("Employee Document Form")
@Route(value = "employee-document-form", layout = MainLayout.class)
public class EmployeeDocumentFormView extends VerticalLayout implements HasUrlParameter<String> {
    @Resource private final EmployeeDocumentService employeeDocumentService;
    @Resource private final EmployeeService employeeService;

    private EmployeeDocumentDTO employeeDocumentDTO;
    private EmployeeDTO employeeDTO;
    private UUID parameterId;

    // This will hold the value from the uploaded image in the upload component.
    private String fileName, fileType;
    private byte[] imageBytes;

    private final FormLayout employeeDocumentDTOFormLayout = new FormLayout();
    private final Grid<EmployeeDocumentDTO> employeeDocumentDTOGrid = new Grid<>(EmployeeDocumentDTO.class, false);

    public EmployeeDocumentFormView(EmployeeDocumentService employeeDocumentService,
                                    EmployeeService employeeService) {
        this.employeeDocumentService = employeeDocumentService;
        this.employeeService = employeeService;

        this.setMargin(true);
        this.add(employeeDocumentDTOFormLayout, employeeDocumentDTOGrid);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
        if (parameter != null) {
            parameterId = UUID.fromString(parameter);
            employeeDTO = employeeService.getById(parameterId);
        }

        buildEmployeeDocumentFormLayout();
        buildEmployeeDocumentGrid();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    private void buildEmployeeDocumentFormLayout() {
        ComboBox<String> documentTypeComboBox = new ComboBox<>("Document Type");
        documentTypeComboBox.setItems("Birth Certificate",
                                      "Police Clearance",
                                      "NBI Clearance",
                                      "Medical Certificate",
                                      "Transcript of Records",
                                      "Diploma",
                                      "Passport",
                                      "Scanned Government ID",
                                      "Others");
        documentTypeComboBox.setRequired(true);
        documentTypeComboBox.setRequiredIndicatorVisible(true);

        // START - Single file upload section.
        Button uploadEmployeeFilesButton = new Button("Upload");
        uploadEmployeeFilesButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        MemoryBuffer memoryBuffer = new MemoryBuffer();

        Upload employeeDocumentUpload = new Upload(memoryBuffer);
        employeeDocumentUpload.setId("upload-employee-file");
        employeeDocumentUpload.setDropAllowed(true);
        employeeDocumentUpload.setAcceptedFileTypes(".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx");
        employeeDocumentUpload.setUploadButton(uploadEmployeeFilesButton);
        employeeDocumentUpload.addSucceededListener(succeededEvent -> {
            try {
                this.setFileName(memoryBuffer.getFileName());
                this.setFileType(memoryBuffer.getFileData().getMimeType());
                this.setImageBytes(memoryBuffer.getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        NativeLabel dropLabel = new NativeLabel("Upload the file here.");
        dropLabel.setFor(employeeDocumentUpload.getId().isPresent() ? employeeDocumentUpload.getId().get() : "");

        Div uploadDiv = new Div(dropLabel, employeeDocumentUpload);
        uploadDiv.getStyle().set("padding-top", "10px");
        // END - Single file upload section.

        TextField remarksTextField = new TextField("Remarks");
        remarksTextField.setRequired(true);
        remarksTextField.setRequiredIndicatorVisible(true);

        DatePicker documentExpirationDatePicker = new DatePicker("Expiration Date");
        documentExpirationDatePicker.setMin(LocalDate.of(LocalDate.now().getYear(),
                                                         LocalDate.now().getMonth(),
                                                         LocalDate.now().getDayOfMonth()));
        documentExpirationDatePicker.setRequired(true);
        documentExpirationDatePicker.setRequiredIndicatorVisible(true);

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(buttonClickEvent -> {
            // TODO: Save employee document and update the grid.
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(buttonClickEvent -> {
            // TODO: Clear the fields.
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(cancelButton, saveButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setMaxWidth("768px");
        buttonLayout.setPadding(true);

        employeeDocumentDTOFormLayout.add(documentTypeComboBox,
                                          documentExpirationDatePicker,
                                          uploadDiv,
                                          remarksTextField,
                                          buttonLayout);
        employeeDocumentDTOFormLayout.setColspan(uploadDiv, 2);
        employeeDocumentDTOFormLayout.setColspan(remarksTextField, 2);
        employeeDocumentDTOFormLayout.setColspan(buttonLayout, 2);
        employeeDocumentDTOFormLayout.setWidth("768px");
    }

    private void buildEmployeeDocumentGrid() {
        employeeDocumentDTOGrid.addColumn(EmployeeDocumentDTO::getDocumentType).setHeader("Document Type");
        employeeDocumentDTOGrid.addColumn(EmployeeDocumentDTO::getFileName).setHeader("File Name");
        employeeDocumentDTOGrid.addColumn(new LocalDateRenderer<>(EmployeeDocumentDTO::getExpirationDate, "MMM dd, yyyy")).setHeader("Expiration Date");
        employeeDocumentDTOGrid.addColumn(EmployeeDocumentDTO::getRemarks).setHeader("Remarks");
        employeeDocumentDTOGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,
                                                 GridVariant.LUMO_COLUMN_BORDERS,
                                                 GridVariant.LUMO_WRAP_CELL_CONTENT);
        employeeDocumentDTOGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        employeeDocumentDTOGrid.setWidth("98%");
    }
}
