package application.ui.handler;

import application.ui.mainPage.MainPage;
import application.ui.patientProfile.PatientProfile;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowHandlers extends Application {

    private WindowObj prevWindow;
    private static WindowHandlers windowHandlers;
    private Stage stage;
    private FXMLLoader loader;
    private MainPage mainPageController;


    private WindowHandlers(){
        this.prevWindow = new WindowObj();
    }

    public void loadWindow(String windowPath, String title, boolean maximized, boolean hide, boolean onClose, MouseEvent event) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(windowPath));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/application/ui/mainPage/imgs/mainIcon.png"));
        stage.setTitle(title);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(maximized);
        stage.setResizable(maximized);
        stage.setResizable(false);
        root.styleProperty().bind(Bindings.concat("-fx-font-size: ", Configuration.FONT_SIZE));
        stage.show();
        this.stage = stage;
        this.loader = loader;
        if(hide){
            ((Node)(event.getSource())).getScene().getWindow().hide();
            this.prevWindow.setMaximized(true);
            this.prevWindow.setScene(((Node)(event.getSource())).getScene());
            this.prevWindow.setTitle("الصفحة الرئيسة");
        }
        if(onClose){
            stage.setOnHiding(e -> closeWindow());
        }
        //if mainPage store the controller.
        if(windowPath.equals("/application/ui/mainPage/mainPage.fxml")){
            this.mainPageController = loader.getController();
        }

    }

    @Override
    public void start(Stage stage){

    }

    public void closeWindow() {
        try{
            Stage stage = new Stage();
            stage.setTitle(prevWindow.getTitle());
            stage.setScene(this.prevWindow.getScene());
            stage.setMaximized(prevWindow.getMaximized());
            stage.show();
            this.mainPageController.reloadClinics();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public FXMLLoader getLoader() {return this.loader;}
    public Stage getStage(){
        return this.stage;
    }

    public static WindowHandlers getInstance(){
        if(windowHandlers == null){
            windowHandlers = new WindowHandlers();
            return windowHandlers;
        }
        return windowHandlers;
    }

}
