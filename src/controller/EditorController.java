package controller;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class EditorController implements Initializable {

    public String script;
    public TreeMap<String, String> presets;

    @FXML
    private TextArea scriptTArea;
    @FXML
    private ChoiceBox<String> presetChoice;
    @FXML
    private Button loadBtn, saveBtn, doneBtn;

    public EditorController() {
        presets = new TreeMap<String, String>();
        presets.put("Default A", "var a\na = 0.5\nplot << a*pow2(x)");
        presets.put("Default B", "var a, b, c\na = 0.5\nb = -2\nc = 2\nplot << a*pow4(x) + b*pow2(x) + c");
        presets.put("Default C", "var a, b, c\na = 1\nb = -3\nc = 4\nplot << a*pow3(x) + b*pow2(x) + c");
        presets.put("Script Syntax", "// A variable \"x\" will be automatically assigned at runtime.\n// A token \"plot\" is is used to define functions. (Last one will be enable!)\nplot << sin(x)\n\n//You can define arbitrary variables by using a token \"var\".\nvar a\n\n// You can also make assignments to variables.\n// Uninitialized or undefined variables will be treated as 0.\na = 10\n\n// A variety of functions can be used.\nplot << sin(x)\nplot << cos(x)\nplot << abs(x)\nplot << log(x)\nplot << pow2(x)\nplot << pow3(x)\nplot << pow4(x)\n\n// By using \"loop\", you can use the iterate function for your calculations.\nvar c, sum\nc = 0\nsum = 0\nloop: c < 5\n    sum = sum + c\n    c = c + 1\nend");
    }

    @Override
    public void initialize(URL url, ResourceBundle resource) {
        loadBtn.setOnAction(event -> loadScript(getFilePath(false)) );
        saveBtn.setOnAction(event -> saveScript(getFilePath(true)) );
        doneBtn.setOnAction(event -> doneBtn.getScene().getWindow().hide() );
        presetChoice.getItems().addAll(presets.keySet());
        presetChoice.valueProperty().addListener((ov, old_val, new_val) -> {
            script = presets.get(new_val);
            scriptTArea.setText(script);
        });
        scriptTArea.setText(script);
        scriptTArea.textProperty().addListener((ov, old_val, new_val) -> script = new_val );
    }

    private URL getFilePath(boolean doSave) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select script file (*.es or *.txt)");
        chooser.getExtensionFilters().add(new ExtensionFilter("DataFile", "*.es", "*.txt"));

        File file = doSave ? chooser.showSaveDialog((Stage)loadBtn.getScene().getWindow()) : chooser.showOpenDialog((Stage)loadBtn.getScene().getWindow());
        try {
            return file == null ? new URL("file:///null") : file.toURI().toURL();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadScript(URL scriptURL) {
        String line;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(scriptURL.openStream()));
            script = "";
            while((line = br.readLine()) != null)
                script += line+"\n";
            scriptTArea.setText(script);
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void saveScript(URL saveURL) {
        // パス変換
        File saveFile = null;
        try {
            saveFile = new File(saveURL.toURI());
        } catch (Exception e) { return; }

        // 保存
        try {
            FileWriter fw = new FileWriter(saveFile.getAbsolutePath(), false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            pw.println(script);
            pw.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

}
