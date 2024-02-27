package com.github.zavch.gear;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ToolViewController {
    private static final String PACKAGE_LOCK_JSON = "package-lock.json";
    private static final String REGEX = ".*\"resolved\": \"(.*)\",.*";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
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
    private ComboBox<String> fileType;
    @FXML
    private Button fileChooseButton;
    @FXML
    private ProgressBar progressBar;
    private File file;
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

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

    private static void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    @FXML
    public void access(ActionEvent actionEvent) throws IOException {
        if (fileType.getValue() != null && file != null) {
            switch (fileType.getValue()) {
                case PACKAGE_LOCK_JSON:
                    Task<Void> task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                List<String> urls = Files.readAllLines(Paths.get(file.getAbsolutePath()))
                                        .stream()
                                        .map(PATTERN::matcher)
                                        .filter(Matcher::matches)
                                        .map(m -> m.group(1))
                                        .collect(Collectors.toList());
                                int total = urls.size();
                                AtomicInteger atomicInteger = new AtomicInteger();
                                urls.parallelStream().forEach(u -> {
                                    try {
                                        URL url = new URL(u);
                                        URLConnection urlConnection = url.openConnection();
                                        urlConnection.connect();
                                    } catch (Exception e) {
                                        System.err.println(e.getMessage());
                                    } finally {
                                        int current = atomicInteger.incrementAndGet();
                                        updateProgress(current, total);
                                        if (current == total) {
                                            progressBar.setVisible(false);
                                            Platform.runLater(() -> {
                                                showInfoAlert("批量访问结束");
                                            });
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                            return null;
                        }
                    };
                    progressBar.progressProperty().bind(task.progressProperty());
                    progressBar.setVisible(true);
                    threadPool.submit(task);
                    break;
            }
        }
    }

    @FXML
    public void chooseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
        }
        file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fileChooseButton.setText(file.getName());
        }
    }

    public void initialize() {
        fileType.getItems().addAll(PACKAGE_LOCK_JSON);
        fileType.setValue(PACKAGE_LOCK_JSON);
    }
}
