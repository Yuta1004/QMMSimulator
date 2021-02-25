package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;

import simulator.XInitSettings;

public class MainUIController implements Initializable {

    // UIから変更可能な値
    private int Ndim, rnum;
    private double hstep, hbar;
    private XInitSettings xInitSettings;

    // コントロールパネルUI
    @FXML
    private Label ndimL, rnumL, hstepL, hbarL, xvalL;
    @FXML
    private Slider ndimC, rnumC, hstepC, hbarC, xvalC;
    @FXML
    private CheckBox xvalRandomC;

    public MainUIController() {
        Ndim = 100;
        rnum = 1;
        hstep = 1.0;
        hbar = 1.0;
        xInitSettings = XInitSettings.fixed(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        // コントロールパネルUI初期化
        ndimC.valueProperty().addListener((__, oldV, newV) -> {
            Ndim = newV.intValue();
            ndimL.setText(""+Ndim);
        });
        rnumC.valueProperty().addListener((__, oldV, newV) -> {
            rnum = newV.intValue();
            rnumL.setText(""+rnum);
        });
        hstepC.valueProperty().addListener((__, oldV, newV) -> {
            hstep = (int)(newV.doubleValue()*10)/10.0;
            hstepL.setText(""+hstep);
        });
        hbarC.valueProperty().addListener((__, oldV, newV) -> {
            hbar = (int)(newV.doubleValue()*10)/10.0;
            hbarL.setText(""+hbar);
        });
        xvalC.valueProperty().addListener((__, oldV, newV) -> {
            xInitSettings = XInitSettings.fixed(newV.intValue());
            xvalL.setText(""+xInitSettings.num);
        });
        xvalRandomC.selectedProperty().addListener((__, oldV, newV) -> {
            xvalL.setDisable(newV);
            xvalC.setDisable(newV);
            if(newV) {
                xInitSettings = XInitSettings.random();
            } else {
                xInitSettings = XInitSettings.fixed((int)xvalC.getValue());
            }
        });
    }

}