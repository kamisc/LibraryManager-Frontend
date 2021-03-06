package com.sewerynkamil.librarymanager.ui.view.form;

import com.sewerynkamil.librarymanager.client.LibraryManagerRentsClient;
import com.sewerynkamil.librarymanager.client.LibraryManagerSpecimensClient;
import com.sewerynkamil.librarymanager.dto.SpecimenDto;
import com.sewerynkamil.librarymanager.dto.enumerated.Status;
import com.sewerynkamil.librarymanager.ui.components.ButtonFactory;
import com.sewerynkamil.librarymanager.ui.components.ButtonType;
import com.sewerynkamil.librarymanager.ui.components.ComponentDesigner;
import com.sewerynkamil.librarymanager.ui.utils.StringIntegerConverter;
import com.sewerynkamil.librarymanager.ui.utils.StringLongConverter;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDate;

/**
 * Author Kamil Seweryn
 */

@SpringComponent
@UIScope
@Secured("ROLE_Admin")
public class SpecimenForm extends FormLayout implements KeyNotifier, FormActions {
    private ButtonFactory buttonFactory = new ButtonFactory();
    private ComponentDesigner componentDesigner = new ComponentDesigner();
    private LibraryManagerSpecimensClient specimensClient;
    private LibraryManagerRentsClient rentsClient;
    private SpecimenDto specimenDto;

    private ChangeHandler changeHandler;

    private TextField bookTitle = new TextField("Book title");
    private TextField publisher = new TextField("Publisher");
    private TextField yearOfPublication = new TextField("Year of publication");
    private TextField isbn = new TextField("ISBN");
    private ComboBox<String> status = new ComboBox<>("Status");

    private Button save = buttonFactory.createButton(ButtonType.SAVE, "Save", "225px");
    private Button update = buttonFactory.createButton(ButtonType.UPDATE, "Update", "225px");
    private Button reset = buttonFactory.createButton(ButtonType.RESET, "Reset", "225px");
    private Button delete = buttonFactory.createButton(ButtonType.DELETE, "Delete", "225px");
    private Button close = buttonFactory.createButton(ButtonType.CLOSE, "Close", "225px");

    private Notification specimenSaveSuccessful = new Notification("The specimen has been added succesfully!", 3000);
    private Notification specimenUpdateSuccessful = new Notification("The specimen has been updated succesfully!", 3000);
    private Notification specimenDeleteSuccessful = new Notification("The specimen has been deleted succesfully!", 3000);
    private Notification specimenCantDelete = new Notification("You can't delete this specimen, beacause it is on rent!", 3000);
    private Dialog dialog = new Dialog();

    private Binder<SpecimenDto> binder = new Binder<>(SpecimenDto.class);

    @Autowired
    public SpecimenForm(
            LibraryManagerSpecimensClient specimensClient,
            LibraryManagerRentsClient rentsClient) {
        this.specimensClient = specimensClient;
        this.rentsClient = rentsClient;

        setSizeUndefined();
        setWidth("260px");
        add(bookTitle, publisher, yearOfPublication, isbn, status, save, update, reset, delete, close);
        setVisible(false);

        specimenSaveSuccessful.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        specimenUpdateSuccessful.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        specimenDeleteSuccessful.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        specimenCantDelete.addThemeVariants(NotificationVariant.LUMO_ERROR);

        bookTitle.setReadOnly(true);

        componentDesigner.setComboboxOptions(Status.statusList(), "Select status", status);
        componentDesigner.setTextFieldsOptions(publisher, yearOfPublication, isbn);

        binder.forField(bookTitle)
                .asRequired("Required field")
                .bind(SpecimenDto::getBookTitle, SpecimenDto::setBookTitle);
        binder.forField(publisher)
                .asRequired("Required field")
                .bind(SpecimenDto::getPublisher, SpecimenDto::setPublisher);
        binder.forField(yearOfPublication)
                .asRequired("Required field")
                .withConverter(new StringIntegerConverter())
                .withValidator(year -> year >= 1600 && year <= LocalDate.now().getYear(), "Dosen't look like a year")
                .bind(SpecimenDto::getYearOfPublication, SpecimenDto::setYearOfPublication);
        binder.forField(isbn)
                .asRequired("Required field")
                .withValidator(isbn -> isbn.length() >= 10 && isbn.length() <= 13, "Invalid ISBN")
                .withConverter(new StringLongConverter())
                .bind(SpecimenDto::getIsbn, SpecimenDto::setIsbn);
        binder.forField(status)
                .asRequired("Required field")
                .bind(SpecimenDto::getStatus, SpecimenDto::setStatus);

        save.addClickListener(e -> save());
        update.addClickListener(e -> update());
        delete.addClickListener(e -> delete());
        reset.addClickListener(e -> editSpecimen(specimenDto));
        close.addClickListener(e -> dialog.close());
    }

    @Override
    public void save() {
        specimensClient.saveNewSpecimen(specimenDto);
        componentDesigner.setActions(specimenSaveSuccessful, changeHandler, dialog);
    }

    @Override
    public void update() {
        specimensClient.updateSpecimen(specimenDto);
        componentDesigner.setActions(specimenUpdateSuccessful, changeHandler, dialog);
    }

    @Override
    public void delete() {
        if(rentsClient.isRentExistBySpecimenId(specimenDto.getId())) {
            specimenCantDelete.open();
        } else {
            specimensClient.deleteSpecimen(specimenDto.getId());
            componentDesigner.setActions(specimenDeleteSuccessful, changeHandler, dialog);
        }
    }

    public final void editSpecimen(SpecimenDto s) {
        dialog.setCloseOnOutsideClick(false);
        if (s == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = s.getId() != null;
        if(persisted) {
            specimenDto = specimensClient.getOneSpecimen(s.getId());

            dialog.add(this);
            dialog.open();
        } else {
            specimenDto = s;
            dialog.add(this);
            dialog.open();
        }

        save.setVisible(!persisted);
        update.setVisible(persisted);
        reset.setVisible(persisted);
        delete.setVisible(persisted);
        binder.setBean(specimenDto);
        setVisible(true);
        publisher.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        this.changeHandler = h;
    }
}