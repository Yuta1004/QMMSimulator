/**
 * Copyright (c) 2021 Yuta Nakagami
 *
 * Released under the MIT license.
 * see https://opensource.org/licenses/MIT
 */

package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    // UI部品
    @FXML
    private AnchorPane pane;

    /**
     * 初期化
     */
    @Override
    public void initialize(URL location, ResourceBundle resource) {
        // 背景色変更
        pane.setOpacity(0.85);
        pane.setStyle("-fx-background-color: rgb(255, 255, 255, 1)");

        // 3秒待機 -> ウィンドウを閉じる
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                Stage stage = (Stage)pane.getScene().getWindow();
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            });
        }).start();
    }

}
