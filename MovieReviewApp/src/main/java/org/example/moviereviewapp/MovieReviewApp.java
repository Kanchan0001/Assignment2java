package org.example.moviereviewapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MovieReviewApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML file and set scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/moviereviewapp/main_page.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Movie Review App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
