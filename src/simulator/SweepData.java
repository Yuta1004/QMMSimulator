package simulator;

/**
 * 各Sweepのデータをもつデータクラス
 */
public class SweepData {

    public final int sweep, Ndim;
    public final double xave, xave2, pave, pave2;
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

        double xave = 0, xave2 = 0, pave = 0, pave2 = 0;
        for(int idx = 0; idx < Ndim; ++ idx) {
            int lp1 = (idx+1)%Ndim;
            xave += x[idx];
            xave2 += Math.pow(x[idx], 2);
            pave += x[lp1]-x[idx];
            pave2 += Math.pow(x[lp1]-x[idx], 2);
        }
        this.xave = xave/Ndim;
        this.xave2 = xave2/Ndim;
        this.pave = pave/Ndim;
        this.pave2 = pave2/Ndim;
    }

    /**
     * データの出力を行う
     */
    public void print() {
        System.out.println(
            String.format("Nswp= %d %.8e %.8e %.8e %.8e", sweep, xave, xave2, pave, pave2)
        );
    }

    /**
     * データの出力を行う(xも同時に出力)
     */
    public void printVerbose() {
        print();
        for(int idx = 0; idx < Ndim; ++ idx) {
            System.out.println(
                String.format("x_%d %d %.8e", sweep, idx, x[idx])
            );
        }
    }

}