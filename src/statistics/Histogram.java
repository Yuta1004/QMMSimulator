package statistics;

/**
 * ヒストグラム作成を行うクラス
 */
public class Histogram {

    private int length;
    private double diff;
    private int hist[];
    private double edges[];

    /**
     * @param range 集計範囲(-range ~ range)
     * @param diff 集計幅
     */
    public Histogram(double range, double diff) {
        this.diff = diff;
        length = (int)(2*range/diff);
        hist = new int[length];
        edges = new double[length];
        for(int idx = 0; idx < length; ++ idx) {
            hist[idx] = 0;
            edges[idx] = -range + diff*idx;
        }
    }

    /**
     * 集計対象を追加する
     * @param x データの配列
     */
    public void addSeries(double x[]) {
        for(int idx = 0; idx < x.length; ++ idx) {
            for(int hi = 0; hi < length; ++ hi) {
                if(x[idx] >= edges[hi] && x[idx] < edges[hi]+diff) ++ hist[hi];
            }
        }
    }

    /**
     * @return 階級を表す配列
     */
    public double[] getEdges() {
        for(int idx = 0; idx < edges.length; ++ idx) edges[idx] += diff/2;
        return edges;
    }

    /**
     * @return 各階級の数をもつ配列
     */
    public int[] getHist() {
        return hist;
    }

}