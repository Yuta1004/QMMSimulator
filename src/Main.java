import java.util.function.Function;

import simulator.SweepData;
import simulator.QMMSimulator;
import simulator.XInitSettings;
import statistics.Histogram;

public class Main {

    public static void main(String args[]) {
        // 初期設定
        int Ndim = 100, Nsweep = 100;
        Histogram histogram = new Histogram(3, 0.1);
        Function<Double, Double> Vpot = (x) -> { return 0.5*x*x; };

        // シミュレーション実行
        QMMSimulator simulator = new QMMSimulator(1, Ndim, 1.0, 1.0, Vpot, XInitSettings.fixed(0));
        for(int sweep = 1; sweep <= Nsweep; ++ sweep) {
            SweepData data = simulator.simulate();
            histogram.addSeries(data.x);
            data.print();
        }

        // Histogram出力
        double edges[] = histogram.getEdges();
        int hist[] = histogram.getHist();
        for(int idx = 0; idx < hist.length; ++ idx) {
            System.out.println(((int)(edges[idx]*100)/100.0)+"\t"+hist[idx]);
        }
    }

}
