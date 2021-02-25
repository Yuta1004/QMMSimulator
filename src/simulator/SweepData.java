package simulator;

/**
 * 各Sweepのデータをもつデータクラス
 */
public class SweepData {

    public int sweep, Ndim;
    public double x[];
    // TODO: ヒストグラムデータ

    /**
     * @param sweep このデータクラスが持っているデータに対応するSweep
     * @param x 各量子の位置情報
     */
    public SweepData(int sweep, double x[]) {
        this.sweep = sweep;
        this.x = x;
        this.Ndim = x.length;
    }

}