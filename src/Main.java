import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Scene scene = new Scene(loader.load());

        // Stage
        stage.setTitle("QMMSimulator");
        stage.setScene(scene);
        stage.show();
    }

}
