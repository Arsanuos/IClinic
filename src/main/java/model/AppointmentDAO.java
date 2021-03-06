package main.java.model;

import main.java.beans.Appointment;
import main.java.beans.Patient;
import main.java.beans.UserSignedInData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AppointmentDAO {

    public static boolean addAppointment(Appointment app){
        try{
            PreparedStatement pst = ModelManager.getInstance().getConnection().prepareStatement(
                    "INSERT INTO Appointment (patientId, date, paidCost, finished, image, comment, confirmed_paid, clinic_number) VALUES "
                            + "( ?, ?, ?, ?, ?, ?, ?, ?);");
            pst.setInt(1, app.getPatientID());
            pst.setString(2, app.getDateString());
            pst.setInt(3, Integer.parseInt(app.getPaidCost()));
            pst.setInt(4, app.isFinished() ? 1 : 0);
            pst.setBytes(5, app.getImageBytes());
            pst.setString(6, app.getComment());
            pst.setInt(7, app.isConfirmedPaid() ? 1 : 0);
            pst.setString(8, UserSignedInData.user.getClinicNumber());
            return pst.executeUpdate() == 1;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteAppointmentByID(int id){
        String query = "DELETE FROM Appointment WHERE id = " + id + " ;";
        return ModelManager.getInstance().executeUpdateQuery(query);
    }

    public static boolean deleteAllAppointmentByPatientID(int patientID){
        String query = "DELETE FROM Appointment WHERE patientId = " + patientID + " AND clinic_number = '" + UserSignedInData.user.getClinicNumber() + "' ;";
        return ModelManager.getInstance().executeUpdateQuery(query);
    }

    public static ArrayList<Appointment> findByPatientID(int patientID){
        String query = "SELECT * FROM Appointment WHERE patientId = " + patientID + " AND clinic_number = '" + UserSignedInData.user.getClinicNumber() + "' ;";
        ArrayList<Appointment> matched = new ArrayList<>();
        try{
            ResultSet resultSet = ModelManager.getInstance().executeQuery(query);
            while (resultSet.next()) {
                matched.add(buildAppointment(resultSet));
            }
            resultSet.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return matched;
    }

    public static ArrayList<Appointment> findByDate(Date date, String clinicNumber){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dt.format(date);
        String query = "SELECT * FROM Appointment WHERE CAST(Appointment.date as date) = '" + formattedDate + "'"
                        + " AND clinic_number = '" + clinicNumber + "' ORDER BY date ;";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        dt.applyPattern("yyyy-MM-dd HH:mm:ss");
        formattedDate = dt.format(cal.getTime());
        String query2 = "SELECT * FROM Appointment WHERE Appointment.date = '" + formattedDate + "'"
                + " AND clinic_number = '" + clinicNumber + "' ORDER BY date ;";
        ArrayList<Appointment> matched = new ArrayList<>();
        try{
            ResultSet resultSet = ModelManager.getInstance().executeQuery(query);
            while (resultSet.next()) {
                matched.add(buildAppointment(resultSet));
            }
            resultSet = ModelManager.getInstance().executeQuery(query2);
            while (resultSet.next()) {
                matched.add(buildAppointment(resultSet));
            }
            resultSet.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        int index = 0;
        ArrayList<Appointment> result = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 12, 0);
        for(int i = 0; i <= 24; i++){
            Appointment app = new Appointment();
            app.setDate(calendar.getTime());
            calendar.add(Calendar.MINUTE, 30);
            if(index < matched.size() && matched.get(index).getDateString().equals(app.getDateString())){
                result.add(matched.get(index));
                index++;
            }
            else{
                result.add(app);
            }
        }
        return result;
    }

    //TODO:: id can not be changed by user (read only ==> fix it in gui)
    public static boolean updateAppointmentByID(int id, Appointment newAppointment){
        boolean delete = deleteAppointmentByID(id);
        newAppointment.setAppointmentID(id);
        boolean add = addAppointment(newAppointment);
        return delete && add;
    }

    public static boolean editAppointmentList(String patientFileIDBefore, String patientFileIDAfter, Date appDate, int paidCost, Appointment app){
        if(patientFileIDBefore.isEmpty() && patientFileIDAfter.isEmpty()){
            return true;
        }

        if(patientFileIDBefore.isEmpty()){
            //insert
            Patient patient = PatientDAO.findByFileNumber(patientFileIDAfter);
            if(patient == null) return false;
            Appointment appp = new Appointment();
            appp.setPatientID(patient.getPatientID());
            appp.setDate(appDate);
            appp.setPaidCost(paidCost);
            appp.setClinicNumber(UserSignedInData.user.getClinicNumber());
            boolean status = AppointmentDAO.addAppointment(appp);
            if(status){
                app.setPatientID(patient.getPatientID());
                app.setDate(appDate);
                app.setPaidCost(paidCost);
                app.setClinicNumber(UserSignedInData.user.getClinicNumber());
            }
            return status;
        }
        else if(patientFileIDAfter.isEmpty()){
            //delete
            Patient patient = PatientDAO.findByFileNumber(patientFileIDBefore);
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedDate = dt.format(appDate);
            String query = "SELECT * FROM Appointment WHERE patientId = " + patient.getPatientID() + " AND date = '" + formattedDate
                            + "' AND clinic_number = '" + UserSignedInData.user.getClinicNumber() + "' ;";
            ArrayList<Appointment> matched = new ArrayList<>();
            try{
                ResultSet resultSet = ModelManager.getInstance().executeQuery(query);
                while (resultSet.next()) {
                    matched.add(buildAppointment(resultSet));
                }
                resultSet.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
            boolean status = deleteAppointmentByID(matched.get(0).getAppointmentID());
            if(status){
                app.setDate(appDate);
                app.setPatientID(-1);
                app.setFinished(false);
                app.setComment("");
                app.setAppointmentID(-1);
                app.setPaidCost(0);
                app.setClinicNumber("");
            }
            return status;
        }
        else{
            //update
            Patient patient1 = PatientDAO.findByFileNumber(patientFileIDBefore);
            Patient patient2 = PatientDAO.findByFileNumber(patientFileIDAfter);
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedDate = dt.format(appDate);
            String query = "SELECT * FROM Appointment WHERE patientId = " + patient1.getPatientID() + " AND date = '" + formattedDate
                            + "' AND clinic_number = '" + UserSignedInData.user.getClinicNumber() + "' ;";
            ArrayList<Appointment> matched = new ArrayList<>();
            try{
                ResultSet resultSet = ModelManager.getInstance().executeQuery(query);
                while (resultSet.next()) {
                    matched.add(buildAppointment(resultSet));
                }
                resultSet.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
            Appointment appp = matched.get(0).clone();
            appp.setPatientID(patient2.getPatientID());
            appp.setDate(appDate);
            appp.setPaidCost(paidCost);
            boolean status = updateAppointmentByID(matched.get(0).getAppointmentID(), appp);
            if(status){
                app.setPatientID(patient2.getPatientID());
                app.setDate(appDate);
                app.setPaidCost(paidCost);
                app.setAppointmentID(appp.getAppointmentID());
                app.setComment(appp.getComment());
                app.setFinished(appp.isFinished());
                app.setImage(appp.getImage());
                app.setConfirmedPaid(appp.isConfirmedPaid());
                app.setClinicNumber(appp.getClinicNumber());
            }
            return status;
        }
    }

    public static boolean confirmPaidCost(String patientFileID, Date appDate){
        Patient patient = PatientDAO.findByFileNumber(patientFileID);
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = dt.format(appDate);
        String query = "SELECT * FROM Appointment WHERE patientId = " + patient.getPatientID() + " AND date = '" + formattedDate
                + "' AND clinic_number = '" + UserSignedInData.user.getClinicNumber() + "' ;";
        ArrayList<Appointment> matched = new ArrayList<>();
        try{
            ResultSet resultSet = ModelManager.getInstance().executeQuery(query);
            while (resultSet.next()) {
                matched.add(buildAppointment(resultSet));
            }
            resultSet.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        if(matched.isEmpty())return false;
        if(!matched.get(0).isConfirmedPaid()){
            matched.get(0).setConfirmedPaid(true);
            patient.setRemainingCost(Math.max(Integer.parseInt(patient.getRemainingCost()) - Integer.parseInt(matched.get(0).getPaidCost()), 0));
            AppointmentDAO.updateAppointmentByID(matched.get(0).getAppointmentID(), matched.get(0));
            PatientDAO.updatePatient(patient.getFile_number(), patient);
        }
        return true;
    }


    public static boolean confirmFinished(String patientFileID, Date appDate){
        Patient patient = PatientDAO.findByFileNumber(patientFileID);
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = dt.format(appDate);
        String query = "SELECT * FROM Appointment WHERE patientId = " + patient.getPatientID() + " AND date = '" + formattedDate
                + "' AND clinic_number = '" + UserSignedInData.user.getClinicNumber() + "' ;";
        ArrayList<Appointment> matched = new ArrayList<>();
        try{
            ResultSet resultSet = ModelManager.getInstance().executeQuery(query);
            while (resultSet.next()) {
                matched.add(buildAppointment(resultSet));
            }
            resultSet.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        if(matched.isEmpty())return false;
        if(!matched.get(0).isFinished()){
            matched.get(0).setFinished(true);
        }
        else{
            matched.get(0).setFinished(false);
        }
        return AppointmentDAO.updateAppointmentByID(matched.get(0).getAppointmentID(), matched.get(0));
    }

    private static Appointment buildAppointment(ResultSet rs){
        Appointment app = new Appointment();
        try {
            app.setAppointmentID(rs.getInt("id"));
            app.setPatientID(rs.getInt("patientId"));

            app.setDate(rs.getString("date"));

            app.setPaidCost(rs.getInt("paidCost"));
            app.setFinished(rs.getBoolean("finished"));

            app.setImage(rs.getBytes("image"));

            app.setComment(rs.getString("comment"));

            app.setConfirmedPaid(rs.getBoolean("confirmed_paid"));
            app.setClinicNumber(rs.getString("clinic_number"));
            return app;
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}