module com.github.zavch.gear {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;
    requires jdk.jsobject;

    opens com.github.zavch.gear to javafx.fxml;
    exports com.github.zavch.gear;
    exports com.github.zavch.gear.controller;
    opens com.github.zavch.gear.controller to javafx.fxml;
    exports com.github.zavch.gear.bridge;
    opens com.github.zavch.gear.bridge to javafx.fxml;
}