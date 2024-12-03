module com.example.project_csc171 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.example.project_csc171 to javafx.fxml;
    exports com.example.project_csc171;
}