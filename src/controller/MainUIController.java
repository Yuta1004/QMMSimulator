package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.shape.Rectangle;

import simulator.XInitSettings;
import simulator.QMMSimulator;
import simulator.SweepData;

public class MainUIController implements Initializable {

    // UIから変更可能な値
    private int Ndim, rnum;
    private double hstep, hbar;
    private XInitSettings xInitSettings;
    private Function<Double, Double> Vpot = (x) -> 0.5*x*x;

    // シミュレータ関連
    private QMMSimulator simulator;

    // コントロールパネルUI
    @FXML
    private Label ndimL, rnumL, hstepL, hbarL, xvalL;
    @FXML
    private Slider ndimC, rnumC, hstepC, hbarC, xvalC;
    @FXML
    private CheckBox xvalRandomC;

    // チャートUI
    @FXML
    LineChart<Double, Double> visualizer;
    @FXML
    NumberAxis visualizerXAxis, visualizerYAxis;

    public MainUIController() {
        Ndim = 100;
        rnum = 1;
        hstep = 1.0;
        hbar = 1.0;
        xInitSettings = XInitSettings.fixed(0);
        simulator = new QMMSimulator(rnum, Ndim, hbar, hstep, Vpot, xInitSettings);
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        // チャートUI設定
        visualizer.setAnimated(false);
        visualizer.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        visualizerXAxis.setAutoRanging(false);
        visualizerXAxis.setLowerBound(-5);
        visualizerXAxis.setUpperBound(5);
        visualizerXAxis.setTickUnit(0.5);
        visualizerYAxis.setAutoRanging(false);
        visualizerYAxis.setLowerBound(0);
        visualizerYAxis.setTickUnit(10);

        // コントロールパネルUI初期化
        ndimC.valueProperty().addListener((__, oldV, newV) -> {
            Ndim = newV.intValue();
            ndimL.setText(""+Ndim);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart();
        });
        rnumC.valueProperty().addListener((__, oldV, newV) -> {
            rnum = newV.intValue();
            rnumL.setText(""+rnum);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart();
        });
        hstepC.valueProperty().addListener((__, oldV, newV) -> {
            hstep = (int)(newV.doubleValue()*10)/10.0;
            hstepL.setText(""+hstep);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart();
        });
        hbarC.valueProperty().addListener((__, oldV, newV) -> {
            hbar = (int)(newV.doubleValue()*10)/10.0;
            hbarL.setText(""+hbar);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart();
        });
        xvalC.valueProperty().addListener((__, oldV, newV) -> {
            xInitSettings = XInitSettings.fixed(newV.intValue());
            xvalL.setText(""+xInitSettings.num);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart();
        });
        xvalRandomC.selectedProperty().addListener((__, oldV, newV) -> {
            xvalL.setDisable(newV);
            xvalC.setDisable(newV);
            if(newV) {
                xInitSettings = XInitSettings.random();
            } else {
                xInitSettings = XInitSettings.fixed((int)xvalC.getValue());
            }
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart();
        });

        updateChart();
    }

    /**
     * Chartの一括更新を行う
     */
    private void updateChart() {
        updateVisualizer();
    }

    /**
     * 量子の位置を表示するLineChartの更新を行う
     */
    private void updateVisualizer() {
        // 量子
        SweepData sweepData = simulator.getSweepData();
        ObservableList<Data<Double, Double>> quantumX = FXCollections.observableArrayList();
        for(int idx = 0; idx < Ndim; ++ idx) {
            quantumX.add(new Data<Double, Double>(sweepData.x[idx], (double)idx));
        }
        Series<Double, Double> seriesQ = new Series<Double, Double>("Quantum", quantumX);

        // ポテンシャル
        ObservableList<Data<Double, Double>> potential = FXCollections.observableArrayList();
        for(double x = -5.0; x <= 5.0; x += 0.5) {
            Data<Double, Double> d = new Data<Double, Double>(x, Vpot.apply(x)*(Ndim/10));
            d.setNode(new Rectangle(0, 0));
            potential.add(d);
        }
        Series<Double, Double> seriesP = new Series<Double, Double>("Potential", potential);

        ObservableList<Series<Double, Double>> data = FXCollections.observableArrayList();
        data.add(seriesQ);
        data.add(seriesP);
        visualizer.getData().clear();;
        visualizer.getData().setAll(data);
        visualizerYAxis.setUpperBound(Ndim);
    }

}