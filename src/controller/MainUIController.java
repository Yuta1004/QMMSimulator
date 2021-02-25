package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.TreeMap;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.util.Duration;

import simulator.XInitSettings;
import simulator.QMMSimulator;
import simulator.SweepData;

public class MainUIController implements Initializable {

    // UIから変更可能な値
    private int Ndim, rnum;
    private double hstep, hbar;
    private XInitSettings xInitSettings;
    private Function<Double, Double> Vpot = (x) -> 0.5*Math.pow(x, 2);
    private static TreeMap<String, Function<Double, Double>> VpotList = new TreeMap<String, Function<Double, Double>>();
    static {
        VpotList.put("Quadratic Function", (x) -> { return 0.5*Math.pow(x, 2); });
        VpotList.put("Cubic Function", (x) -> { return 0.25*Math.pow(x, 4)-2*Math.pow(x, 2)+4; });
        VpotList.put("Quartic Function", (x) -> { return Math.pow(x, 3)-3*Math.pow(x, 2)+4; });
    };

    // シミュレータ関連
    private QMMSimulator simulator;
    private Timeline tl;
    private int playSweep;
    private ArrayList<SweepData> xHistory;

    // コントロールパネルUI
    @FXML
    private AnchorPane controlPane;
    @FXML
    private Label ndimL, rnumL, hstepL, hbarL, xvalL, sweepL;
    @FXML
    private Slider ndimC, rnumC, hstepC, hbarC, xvalC;
    @FXML
    private CheckBox xvalRandomC;
    @FXML
    private Button playBtn, prevBtn, nextBtn, resetBtn;
    @FXML
    private ChoiceBox<String> vpotC;

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
        playSweep = 0;
        simulator = new QMMSimulator(rnum, Ndim, hbar, hstep, Vpot, xInitSettings);
        xHistory = new ArrayList<SweepData>();
        xHistory.add(simulator.getSweepData().clone());
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
            updateChart(0);
        });
        rnumC.valueProperty().addListener((__, oldV, newV) -> {
            rnum = newV.intValue();
            rnumL.setText(""+rnum);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        hstepC.valueProperty().addListener((__, oldV, newV) -> {
            hstep = (int)(newV.doubleValue()*10)/10.0;
            hstepL.setText(""+hstep);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        hbarC.valueProperty().addListener((__, oldV, newV) -> {
            hbar = (int)(newV.doubleValue()*10)/10.0;
            hbarL.setText(""+hbar);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        xvalC.valueProperty().addListener((__, oldV, newV) -> {
            xInitSettings = XInitSettings.fixed(newV.intValue());
            xvalL.setText(""+xInitSettings.num);
            simulator = new QMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
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
            updateChart(0);
        });

        // アニメーション操作UI
        vpotC.getItems().setAll(VpotList.keySet());
        vpotC.valueProperty().addListener((ov, old_val, new_val) -> {
            Vpot = VpotList.get(new_val);
            updateChart(0);
        });
        resetBtn.setOnAction(event -> {
            tl.stop();
            playBtn.setText("▷");
            playSweep = 0;
            simulator = new QMMSimulator(rnum, Ndim, hbar, hstep, Vpot, xInitSettings);
            xHistory = new ArrayList<SweepData>();
            updateChart(0);
            controlPane.setDisable(false);
        });
        playBtn.setOnAction(event -> {
            controlPane.setDisable(true);
            if(playBtn.getText().equals("▷")) {
                tl.play();
                playBtn.setText("□");
            } else {
                tl.stop();
                playBtn.setText("▷");
            }
        });
        prevBtn.setOnAction(event -> updateChart(-1));
        nextBtn.setOnAction(event -> updateChart(1));

        // 画面初期化
        updateChart(0);
        initTimeline();
    }

    /**
     * Timelineの初期化を行う
     */
    private void initTimeline() {
        if(tl != null && tl.getStatus().equals(Animation.Status.RUNNING)) return;
        tl = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> updateChart(1)));
        tl.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * 画面の一括更新を行う
     * @param d 表示中の情報からSweepをどれだけ更新するか
     */
    private void updateChart(int d) {
        if(Math.abs(d) > 1) return;
        if(d == 1) {
            if(playSweep == simulator.getSweepData().sweep) {
                simulator.simulate();
                xHistory.add(simulator.getSweepData().clone());
            }
            ++ playSweep;
        }
        if(d == -1) {
            if(playSweep > 0) -- playSweep;
        }
        if(d == 0) {
            if(xHistory.size() > 0) xHistory.remove(playSweep);
            xHistory.add(simulator.getSweepData().clone());
        }
        sweepL.setText("Sweep: "+playSweep);
        updateVisualizer();
    }

    /**
     * 量子の位置を表示するLineChartの更新を行う
     */
    private void updateVisualizer() {
        // 量子
        SweepData sweepData = xHistory.get(playSweep);
        ObservableList<Data<Double, Double>> quantumX = FXCollections.observableArrayList();
        for(int idx = 0; idx < Ndim; ++ idx) {
            quantumX.add(new Data<Double, Double>(sweepData.x[idx], (double)idx));
        }
        Series<Double, Double> seriesQ = new Series<Double, Double>("Quantum", quantumX);

        // ポテンシャル
        ObservableList<Data<Double, Double>> potential = FXCollections.observableArrayList();
        for(double x = -5.0; x <= 5.0; x += 0.1) {
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