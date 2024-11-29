package com.example.project_csc171;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

// for images
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Group root = new Group();
          Scene scene = new Scene(fxmlLoader.load());

        // Load root pane from FXML
//        AnchorPane root = fxmlLoader.load();

//        Scene scene = new Scene(root, 600, 864);
        scene.getRoot().requestFocus();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        //yoo
//        // Load the image from resources folder
//        Image image = new Image(getClass().getResource("/forest2.0.jpg").toString());
//
//        // Create ImageView and set it as the background
//        ImageView imageView = new ImageView(image);
//        imageView.setFitWidth(scene.getWidth()); // Adjust to your scene width
//        imageView.setFitHeight(scene.getHeight()); // Adjust to your scene height
//        imageView.setPreserveRatio(false);
//
//        // Add the image as the first child of the root pane
//        root.getChildren().add(0, imageView);
    }

    public static void main(String[] args) {
        launch();
    }
}