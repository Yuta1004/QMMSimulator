package controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.TreeMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.util.Duration;

import simulator.XInitSettings;
import simulator.PMMSimulator;
import simulator.SweepData;
import statistics.Histogram;

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
    private PMMSimulator simulator;
    private Timeline tl;
    private int playSweep, skipSweep = 50;
    private ArrayList<SweepData> xHistory;

    // コントロールパネルUI
    @FXML
    private AnchorPane controlPane;
    @FXML
    private HBox controlHBox;
    @FXML
    private Label ndimL, rnumL, hstepL, hbarL, xvalL, sweepL;
    @FXML
    private Slider ndimC, rnumC, hstepC, hbarC, xvalC;
    @FXML
    private CheckBox xvalRandomC;
    @FXML
    private Button playBtn, prevBtn, nextBtn, resetBtn, scriptEditBtn;
    @FXML
    private TextField skipSweepC;

    // チャートUI
    @FXML
    BarChart<String, Integer> histogramChart;
    @FXML
    LineChart<Double, Double> visualizer;
    @FXML
    NumberAxis visualizerXAxis, visualizerYAxis, histogramChartYAxis;

    public MainUIController() {
        Ndim = 100;
        rnum = 1;
        hstep = 1.0;
        hbar = 1.0;
        xInitSettings = XInitSettings.fixed(0);
        playSweep = 0;
        simulator = new PMMSimulator(rnum, Ndim, hbar, hstep, Vpot, xInitSettings);
        xHistory = new ArrayList<SweepData>();
        xHistory.add(simulator.getSweepData().clone());
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        // チャートUI設定
        histogramChart.setAnimated(false);
        histogramChartYAxis.setAutoRanging(false);
        histogramChartYAxis.setLowerBound(0);
        histogramChartYAxis.setTickUnit(5);
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
            simulator = new PMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        rnumC.valueProperty().addListener((__, oldV, newV) -> {
            rnum = newV.intValue();
            rnumL.setText(""+rnum);
            simulator = new PMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        hstepC.valueProperty().addListener((__, oldV, newV) -> {
            hstep = (int)(newV.doubleValue()*10)/10.0;
            hstepL.setText(""+hstep);
            simulator = new PMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        hbarC.valueProperty().addListener((__, oldV, newV) -> {
            hbar = (int)(newV.doubleValue()*10)/10.0;
            hbarL.setText(""+hbar);
            simulator = new PMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        xvalC.valueProperty().addListener((__, oldV, newV) -> {
            xInitSettings = XInitSettings.fixed((int)(newV.doubleValue()*10)/10.0);
            xvalL.setText(""+xInitSettings.num);
            simulator = new PMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        xvalRandomC.selectedProperty().addListener((__, oldV, newV) -> {
            xvalL.setDisable(newV);
            xvalC.setDisable(newV);
            rnumL.setDisable(!newV);
            rnumC.setDisable(!newV);
            if(newV) {
                xInitSettings = XInitSettings.random();
            } else {
                xInitSettings = XInitSettings.fixed((int)xvalC.getValue());
            }
            simulator = new PMMSimulator(rnum, Ndim, hstep, hbar, Vpot, xInitSettings);
            updateChart(0);
        });
        scriptEditBtn.setOnAction(event -> {
            EditorController controller = new EditorController();
            genStage("Script Editor", "/fxml/Editor.fxml", controller).showAndWait();
            System.out.println(controller.script);
        });

        // アニメーション操作UI
        skipSweepC.textProperty().addListener((ov, old_val, new_val) -> {
            try {
                skipSweep = Integer.parseInt(new_val);
            } catch (Exception e){}
        });
        resetBtn.setOnAction(event -> {
            tl.stop();
            playBtn.setText("START");
            playSweep = 0;
            simulator = new PMMSimulator(rnum, Ndim, hbar, hstep, Vpot, xInitSettings);
            xHistory = new ArrayList<SweepData>();
            updateChart(0);
            controlPane.setDisable(false);
            controlHBox.setDisable(false);
        });
        playBtn.setOnAction(event -> {
            controlPane.setDisable(true);
            controlHBox.setDisable(true);
            if(playBtn.getText().equals("START")) {
                tl.play();
                playBtn.setText("STOP");
            } else {
                tl.stop();
                playBtn.setText("START");
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
                simulator.simulate(skipSweep);
                xHistory.add(simulator.getSweepData().clone());
            }
            playSweep += skipSweep;
        }
        if(d == -1) {
            if(playSweep > 0) -- playSweep;
        }
        if(d == 0) {
            if(xHistory.size() > 0) xHistory.remove(playSweep/skipSweep);
            xHistory.add(simulator.getSweepData().clone());
        }
        sweepL.setText("Sweep: "+playSweep);
        updateVisualizer();
        updateHistogramChart();
    }

    /**
     * 量子の位置を表示するLineChartの更新を行う
     */
    private void updateVisualizer() {
        // 量子
        SweepData sweepData = xHistory.get(playSweep/skipSweep);
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

    /**
     * ヒストグラムを描画する
     */
    private void updateHistogramChart() {
        Histogram histogram = new Histogram(5.0, 0.1);
        histogram.addSeries(xHistory.get(playSweep/skipSweep).x);
        int hist[] = histogram.getHist();
        double edges[] = histogram.getEdges();

        int max = 0;
        ObservableList<Data<String, Integer>> histList = FXCollections.observableArrayList();
        for(int idx = 0; idx < edges.length; ++ idx) {
            double edge = (int)(edges[idx]*100)/100.0;
            histList.add(new Data<String, Integer>(""+edge, hist[idx]));
            max = Math.max(max, hist[idx]);
        }
        Series<String, Integer> seriesH = new Series<String, Integer>("Histogram", histList);

        ObservableList<Series<String, Integer>> data = FXCollections.observableArrayList();
        data.add(seriesH);
        histogramChart.getData().clear();
        histogramChart.getData().setAll(data);
        histogramChartYAxis.setUpperBound((max+5)/5*5);
    }

    /**
     * 指定FXMLファイルでStageを生成して返す
     * @param title ウィンドウタイトル
     * @param fxmlPath FXMLファイルのパス
     * @param controller コントローラ
     */
    private <T> Stage genStage(String title, String fxmlPath, T controller) {
        // FXML読み込み
        Scene scene = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if(controller != null)
                loader.setController(controller);
            scene = new Scene(loader.load());
        } catch (Exception e){ e.printStackTrace(); return null; }

        // ダイアログ立ち上げ
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle(title);
        return stage;
    }

}
