package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ErrorController implements Initializable {

    String msg;

    @FXML
    private TextArea errorText;

    public ErrorController(String msg) {
        this.msg = msg;
    }

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        errorText.setText(msg);
    }

}
