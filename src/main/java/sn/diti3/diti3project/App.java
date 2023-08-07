package sn.diti3.diti3project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/pages/livre.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("CRUD Livres");
        stage.setScene(scene);
        stage.show();
    }
}
