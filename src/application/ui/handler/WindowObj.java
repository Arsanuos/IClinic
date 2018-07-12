package application.ui.handler;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class WindowObj {
    private Scene scene;
    private String title;
    private Boolean maximized;

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMaximized(Boolean maximized) {
        this.maximized = maximized;
    }

    public Scene getScene() {
        return scene;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getMaximized() {
        return maximized;
    }




}
