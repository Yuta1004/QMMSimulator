/**
 * Copyright (c) 2021 Yuta Nakagami
 *
 * Released under the MIT license.
 * see https://opensource.org/licenses/MIT
 */

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import controller.MainUIController;

public class Main extends Application {

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        loader.setController(new MainUIController());
        Scene scene = new Scene(loader.load());

        // Stage
        stage.setTitle("Quantum");
        stage.setScene(scene);
        stage.show();
    }

}
