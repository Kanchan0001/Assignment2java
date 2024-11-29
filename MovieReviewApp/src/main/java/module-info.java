module org.example.moviereviewapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.json;

    opens org.example.moviereviewapp to javafx.fxml;
    exports org.example.moviereviewapp;
}