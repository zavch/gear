module com.github.zavch.gear {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.github.zavch.gear to javafx.fxml;
    exports com.github.zavch.gear;
}