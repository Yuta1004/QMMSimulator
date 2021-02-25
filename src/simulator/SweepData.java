package simulator;

/**
 * 各Sweepのデータをもつデータクラス
 */
public class SweepData {

    public final int sweep, Ndim;
    public final double x[];
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