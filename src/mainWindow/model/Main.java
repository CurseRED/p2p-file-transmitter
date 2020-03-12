package mainWindow.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainLayout.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        File file = new File("config.cfg");
        configInitialize(file);

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /**
     * Method initializes config file that contains filesand directories
     * that people allowed to download from user computer
     * @param file config file directory
     */
    private void configInitialize(File file) {
        try {
            boolean result = file.createNewFile();
            if (result == false) {
                // Initialize new config
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
