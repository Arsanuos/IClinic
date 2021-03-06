package main.java.beans;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import main.java.model.PatientDAO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Appointment {
    //TODO:: add patient name to appointment object
    private int appointmentID;
    private int patientID;
    private Date date;
    private SimpleBooleanProperty finished;
    private Image image;
    private int paidCost;
    private String comment;
    private boolean confirmed_paid;

    private String patientName;
    private String patientFileID;
    private String clinicNumber;
    private Date dateOnly;
    private Date timeOnly;
    private String phoneNumber;

    public Appointment(){
        finished = new SimpleBooleanProperty(false);
        image = null;
        paidCost = 0;
        comment = "";
        confirmed_paid = false;
        patientID = -1;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public Date getDate() {
        return date;
    }


    public String getDateString() {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = dt.format(date);
        return formattedDate;
    }

    public void setDate(Date date) {
        if (date == null) {
            this.date = null;
            return;
        }
        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            this.date = dt.parse(dt.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDate(String date) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date formattedDate = null;
        try {
            formattedDate = dt.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.date = formattedDate;
    }


    public boolean isConfirmedPaid() {
        return confirmed_paid;
    }

    public void setConfirmedPaid(boolean confirmed_paid) {
        this.confirmed_paid = confirmed_paid;
    }

    public Boolean isFinished() {
        return finished.getValue();
    }

    public BooleanProperty finishedProperty() { return finished; }

    public void setFinished(Boolean finished) {
        this.finished.set(finished);
    }

    public ImageView getImage() {
        if(image == null)return null;
        javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage( adjustBufferedImage((BufferedImage) image, 100, 100), null);
        ImageView imageView = new ImageView();
        imageView.setImage(fxImage);
        return imageView;
    }

    public BufferedImage getBufferdImage(){
        return (BufferedImage) image;
    }

    public byte[] getImageBytes() {
        if(image == null)return null;
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write((BufferedImage)image, "jpg", baos);
            baos.flush();
            bytes = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void setImage(ImageView imageView){
        if(imageView == null){
            this.image = null;
            return;
        }
        this.image = SwingFXUtils.fromFXImage(imageView.getImage(), null);
    }

    public void setImage(byte[] bytes) {
        if(bytes == null){
            image = null;
            return;
        }
        InputStream in = new ByteArrayInputStream(bytes);
        try {
            this.image = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPaidCost() {
        return String.valueOf(paidCost);
    }

    public void setPaidCost(int paidCost) {
        this.paidCost = paidCost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDateOnly(){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dt.format(date);
        return formattedDate;
    }

    public String getTimeOnly(){
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
        String formattedDate = dt.format(date);
        return formattedDate;
    }

    public Patient getPatient(){
        ArrayList<Patient> arr = PatientDAO.findByID(patientID);
        if(arr.isEmpty()){
            return null;
        }
        return arr.get(0);
    }

    public String getPatientName(){
        if(patientID == -1) return "";
        return getPatient().getPatientName();
    }

    public String getPatientFileID() {
        if(patientID == -1) return "";
        return getPatient().getFile_number();
    }

    public String getPhoneNumber(){
        if(patientID == -1) return "";
        return getPatient().getPhoneNumber();
    }

    public String getClinicNumber() {
        if(patientID == -1) return "";
        return getPatient().getClinic_number();
    }

    public void setClinicNumber(String clinicNumber){
        this.clinicNumber = clinicNumber;
    }

    public Appointment clone() {
        Appointment app = new Appointment();
        if(getClinicNumber() != "") app.setClinicNumber(getClinicNumber());
        app.setPaidCost(Integer.parseInt(this.getPaidCost()));
        app.setConfirmedPaid(this.isConfirmedPaid());
        app.setDate(this.getDate());
        app.setPatientID(this.getPatientID());
        app.setAppointmentID(this.getAppointmentID());
        app.setComment(this.getComment());
        app.setImage(this.getImage());
        app.setFinished(this.isFinished());
        return app;
    }

    private BufferedImage adjustBufferedImage(BufferedImage img, int newW, int newH){
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }
}
