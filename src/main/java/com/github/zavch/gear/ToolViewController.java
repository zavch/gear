package com.github.zavch.gear;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.*;
import java.util.Base64;

public class ToolViewController {
    @FXML
    private TextArea jwt;
    @FXML
    private TextArea payload;
    @FXML
    private TextField time;
    @FXML
    private DatePicker date;
    @FXML
    private Spinner<Integer> hour;
    @FXML
    private Spinner<Integer> min;
    @FXML
    private Spinner<Integer> second;
    @FXML
    private Spinner<Integer> millisecond;

    @FXML
    public void parseJwt(ActionEvent actionEvent) {
        String text = jwt.getText();
        String[] parts = text.split("\\.");
        if (parts.length != 3) {
            showErrorAlert("JWT格式错误");
            return;
        }
        try {
            byte[] decode = Base64.getDecoder().decode(parts[1]);
            payload.setText(new String(decode));
        } catch (Exception e) {
            showErrorAlert("JWT格式错误");
        }
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    public void date2time(ActionEvent actionEvent) {
        LocalDate localDate = date.getValue();
        if (localDate == null) {
            showErrorAlert("日期不能为空");
            return;
        }
        Integer hourValue = hour.getValue();
        Integer minValue = min.getValue();
        Integer secondValue = second.getValue();
        Integer millisecondValue = millisecond.getValue();
        LocalTime localTime = LocalTime.of(hourValue, minValue,
                secondValue, millisecondValue * 1_000_000);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        time.setText(String.valueOf(localDateTime.atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli()));
    }

    @FXML
    public void time2date(ActionEvent actionEvent) {
        String text = time.getText();
        try {
            long timestamp = Long.parseLong(text);
            Instant instant = Instant.ofEpochMilli(timestamp);
            LocalDateTime offsetDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            date.setValue(offsetDateTime.toLocalDate());
            hour.setValueFactory(new SpinnerValueFactory
                    .IntegerSpinnerValueFactory(0, 59, offsetDateTime.getHour()));
            min.setValueFactory(new SpinnerValueFactory
                    .IntegerSpinnerValueFactory(0, 59, offsetDateTime.getMinute()));
            second.setValueFactory(new SpinnerValueFactory
                    .IntegerSpinnerValueFactory(0, 59, offsetDateTime.getSecond()));
            millisecond.setValueFactory(new SpinnerValueFactory
                    .IntegerSpinnerValueFactory(0, 999, offsetDateTime.getNano() / 1_000_000));
        } catch (Exception e) {
            showErrorAlert("时间戳格式错误");
        }
    }
}
