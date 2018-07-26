package application.ui.mainPage;

import application.ui.handler.EditCell;
import application.ui.handler.MessagesController;
import application.ui.handler.WindowHandlers;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import main.java.beans.Appointment;
import main.java.beans.UserSignedInData;
import main.java.model.AppointmentDAO;
import main.java.model.PatientDAO;
import main.java.model.UserDAO;
import main.java.util.UiUtil;

import java.net.URL;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class MainPage implements Initializable {

    @FXML
    private JFXComboBox<String> clinicNumberChooser;

    @FXML
    private TableView<Appointment> todaySession;

    @FXML
    private TableColumn<Appointment, String> time;

    @FXML
    private TableColumn<Appointment, String> patientNumber;

    @FXML
    private TableColumn<Appointment, String> patientName;

    @FXML
    private TableColumn<Appointment, String> phoneNumber;

    @FXML
    private TableColumn<Appointment, String> money;

    @FXML
    private JFXDatePicker chooseDate;

    @FXML
    private TableView<Appointment> searchSessionsTable;

    @FXML
    private TableColumn<Appointment, String> time1;

    @FXML
    private TableColumn<Appointment, String> patientName1;

    @FXML
    private TableColumn<Appointment, String> patientNumber1;

    @FXML
    private TableColumn<Appointment, String> phoneNumber1;

    @FXML
    private TableColumn<Appointment, String> money1;

    private ObservableList<Appointment> tmpTodayTableData;

    private ObservableList<Appointment> tmpSearchTableData;

    private WindowHandlers windowHandlers;


    public MainPage(){
        this.windowHandlers = WindowHandlers.getInstance();
    }


    public void searchPatient(MouseEvent mouseEvent) {
        try {
            windowHandlers.loadWindow("/application/ui/displayPatients/displayPatient.fxml",
                    "بحث المرضى", true,true, true, mouseEvent);

        }
        catch (Exception e) {
            //TODO:: put log4j jar for error logging
            e.printStackTrace();
        }
    }

    public void searchUser(MouseEvent mouseEvent) {
        try {
            windowHandlers.loadWindow("/application/ui/displayUsers/displayUsers.fxml",
                    "بحث عن مستخدم", true, true, true, mouseEvent);
        } catch (Exception e) {
            //TODO:: put log4j jar for error logging
            e.printStackTrace();
        }
    }


    public void addPatient(MouseEvent mouseEvent) {
        try {
            windowHandlers.loadWindow("/application/ui/addPatient/addPatient.fxml", "اضافة مريض", false, true, true, mouseEvent);
        } catch (Exception e) {
            //TODO:: put log4j jar for error logging
            e.printStackTrace();
        }
    }

    public void addUser(MouseEvent mouseEvent){
        try {
            windowHandlers.loadWindow("/application/ui/addUsers/addUsers.fxml",
                    "اضافة مستخدم", false, true, true, mouseEvent);
        } catch (Exception e) {
            //TODO:: put log4j jar for error logging
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO:: we need to add the editable cells here.
        UserSignedInData.user = UserDAO.getUser("admin");
        clinicNumberChooser.setItems(FXCollections.observableArrayList(UserDAO.getClinics()));
        clinicNumberChooser.getSelectionModel().select(UserSignedInData.user.getClinic());
        todaySession.setOnKeyPressed(event -> {
            TablePosition<Appointment, ?> pos = todaySession.getFocusModel().getFocusedCell();
            if (pos != null && event.getCode().isLetterKey()) {
                todaySession.edit(pos.getRow(), pos.getTableColumn());
            }
        });

        time.setCellValueFactory(new PropertyValueFactory<>("timeOnly"));
        patientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        patientNumber.setCellValueFactory(new PropertyValueFactory<>("patientFileID"));
        patientNumber.setCellFactory(patienNumber -> EditCell.createStringEditCell());
        patientNumber.setOnEditCommit(event -> {
            //TODO:: test failed edit.
            int pos = event.getTablePosition().getRow();
            Appointment appointment = event.getTableView().getItems().get(pos);
            tmpTodayTableData.set(pos, appointment);
            final String value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            appointment.getPatient().setFile_number(value);
            //TODO:: this function must take the appointment before and after.
            AppointmentDAO.editAppointmentList(tmpTodayTableData.get(pos).getPatientFileID(), appointment.getPatientFileID(),
                    appointment.getDate(), appointment.getPaidCost());
            save(pos, appointment,true);
            todaySession.refresh();
        });
        money.setCellValueFactory(new PropertyValueFactory<>("paidCost"));
        money.setCellFactory(money -> EditCell.createStringEditCell());
        money.setOnEditCommit(event -> {
            //TODO:: test failed edit.
            int pos = event.getTablePosition().getRow();
            Appointment appointment = event.getTableView().getItems().get(pos);
            tmpTodayTableData.set(pos, appointment);
            final String value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            appointment.setPaidCost(Integer.valueOf(value));
            //TODO:: this function must take the appointment before and after.
            AppointmentDAO.editAppointmentList(tmpTodayTableData.get(pos).getPatientFileID(), appointment.getPatientFileID(),
                    appointment.getDate(), appointment.getPaidCost());
            save(pos, appointment,true);
            todaySession.refresh();
        });

        time1.setCellValueFactory(new PropertyValueFactory<>("timeOnly"));
        patientName1.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        phoneNumber1.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        patientNumber1.setCellValueFactory(new PropertyValueFactory<>("patientFileID"));
        patientNumber1.setCellFactory(patienNumber -> EditCell.createStringEditCell());
        patientNumber1.setOnEditCommit(event -> {
            //TODO:: test failed edit.
            int pos = event.getTablePosition().getRow();
            Appointment appointment = event.getTableView().getItems().get(pos);
            tmpSearchTableData.set(pos, appointment);
            final String value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            appointment.getPatient().setFile_number(value);
            //TODO:: this function must take the appointment before and after.
            AppointmentDAO.editAppointmentList(tmpSearchTableData.get(pos).getPatientFileID(), appointment.getPatientFileID(),
                    appointment.getDate(), appointment.getPaidCost());
            save(pos, appointment,false);
            searchSessionsTable.refresh();
        });
        money1.setCellValueFactory(new PropertyValueFactory<>("paidCost"));
        money1.setCellFactory(patienNumber -> EditCell.createStringEditCell());
        money1.setOnEditCommit(event -> {
            //TODO:: test failed edit.
            int pos = event.getTablePosition().getRow();
            Appointment appointment = event.getTableView().getItems().get(pos);
            tmpSearchTableData.set(pos, appointment);
            final String value = event.getNewValue() != null ?
                    event.getNewValue() : event.getOldValue();
            appointment.setPaidCost(Integer.valueOf(value));
            //TODO:: this function must take the appointment before and after.
            AppointmentDAO.editAppointmentList(tmpSearchTableData.get(pos).getPatientFileID(), appointment.getPatientFileID(),
                    appointment.getDate(), appointment.getPaidCost());
            save(pos, appointment,false);
            searchSessionsTable.refresh();
        });
        //TODO:: initialize today session here.
        todaySession.setItems(UiUtil.getAppointmentObservable(AppointmentDAO.findByDate(getToday(), Integer.valueOf(clinicNumberChooser.getValue()))));
        tmpTodayTableData = searchSessionsTable.getItems();
    }


    @FXML
    void saveInSearch(MouseEvent event) {
        ObservableList<Appointment> appointments = searchSessionsTable.getItems();
        ObservableList<Appointment> remaining = FXCollections.observableArrayList();
        ArrayList<String> msgs = new ArrayList<>();
        for(int i = 0 ;i < appointments.size(); i++){
            Appointment appointment = appointments.get(i);
            if(AppointmentDAO.updateAppointmentByID(appointment.getAppointmentID(), appointment)){
                remaining.add(appointment);
                String msg = "لا يمكن تعديل " + appointment.getPatientName() + " : " + appointment.getPatientFileID();
                msgs.add(msg);
            }else{
                remaining.add(tmpSearchTableData.get(i));
            }
        }
        searchSessionsTable.setItems(remaining);
        tmpSearchTableData.setAll(remaining);
    }

    @FXML
    void saveInToday(MouseEvent event){
        ObservableList<Appointment> appointments = todaySession.getItems();
        ObservableList<Appointment> remaining = FXCollections.observableArrayList();
        ArrayList<String> msgs = new ArrayList<>();
        for(int i = 0 ;i < appointments.size(); i++){
            Appointment appointment = appointments.get(i);
            if(AppointmentDAO.updateAppointmentByID(appointment.getAppointmentID(), appointment)){
                remaining.add(appointment);
                String msg = "لا يمكن تعديل " + appointment.getPatientName() + " : " + appointment.getPatientFileID();
                msgs.add(msg);
            }else{
                remaining.add(tmpTodayTableData.get(i));
            }
        }
        todaySession.setItems(remaining);
        tmpTodayTableData.setAll(remaining);
    }

    @FXML
    void searchAppointment(MouseEvent event) {
        if(chooseDate.getValue() != null){
            Date date = getDate();
            ArrayList<Appointment> list = AppointmentDAO.findByDate(date, Integer.valueOf(clinicNumberChooser.getValue()));
            searchSessionsTable.setItems(UiUtil.getAppointmentObservable(list));
            tmpSearchTableData.setAll(searchSessionsTable.getItems());
        }
    }

    private Date getDate() {
        LocalDate localDate = chooseDate.getValue();
        Date ret = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return ret;
    }

    private Date getToday(){
        Date today = new Date();
        return today;
    }

    private void save(int pos, Appointment appointment, boolean today){
        if(!AppointmentDAO.editAppointmentList(tmpSearchTableData.get(pos).getPatientFileID(), appointment.getPatientFileID(),
                appointment.getDate(), appointment.getPaidCost())){
            String msg = "لا يمكن تعديل " + appointment.getPatientFileID() + " : " + appointment.getPatient().getPatientName();
            MessagesController.getAlert(msg, Alert.AlertType.ERROR);
            if(today){
                todaySession.setItems(tmpTodayTableData);
            }else{
                searchSessionsTable.setItems(tmpSearchTableData);
            }
        }else{
            if(today){
                todaySession.setItems(UiUtil.getAppointmentObservable(AppointmentDAO.findByDate(getToday(), Integer.valueOf(clinicNumberChooser.getValue()))));
            }else{
                searchAppointment(null);
            }
        }
    }
}
