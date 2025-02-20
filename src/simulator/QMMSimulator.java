/**
 * Copyright (c) 2021 Yuta Nakagami
 *
 * Released under the MIT license.
 * see https://opensource.org/licenses/MIT
 */

/* このプログラムは3年前に京都大学の方から譲り受けたものを参考にして新しく書き下したものです */

package simulator;

import java.util.function.Function;

import simulator.XInitSettings.Tag;

/**
 * メトロポリス法を用いたモンテカルロシミュレーションの管理・実行を行うクラス
 */
public class QMMSimulator {

    // パラメータ郡
    private FRandom frand;
    private int Ndim, sweep = 0;
    private double hbar, hstep;
    private Function<Double, Double> Vpot;

    // 状態管理用
    private int rejectCnt = 0;
    private double x[]; // 量子の位置情報
    private double S[]; // 作用 S(x)
    private double Ksum, Vsum; // 運動エネルギー, ポテンシャルエネルギー

    /**
     * @param init_rnum     乱数の初期値
     * @param Ndim          居時間軸への格子化数
     * @param hbar
     * @param hstep         ランダムステップ決定のための振れ幅 -hstep < δx < hstep
     * @param Vpot          ポテンシャルを定義するFunction<Double, Double>
     * @param xInitSettings xの初期化設定 (XInitSettingsを使用)
     */
    public QMMSimulator(int init_rnum, int Ndim, double hbar, double hstep,
                        Function<Double, Double> Vpot, XInitSettings xInitSettings) {
        frand = new FRandom(init_rnum);
        this.Ndim = Ndim;
        this.hbar = hbar;
        this.hstep = hstep;
        this.Vpot = Vpot;

        x = new double[Ndim];
        if(xInitSettings.tag == Tag.FIXED) {
            for(int idx = 0; idx < Ndim; ++ idx) x[idx] = xInitSettings.num;
        } else {
            for(int idx = 0; idx < Ndim; ++ idx) x[idx] = 2*frand.next()-1;
        }

        S = new double[Ndim];
        for(int l = 0; l < Ndim; ++ l) {
            int lp1 = (l+1)%Ndim, lm1 = (l-1+Ndim)%Ndim;
            S[l] = 0.5*(Math.pow(x[lm1]-x[l], 2) + Math.pow(x[l]-x[lp1], 2)) + Vpot.apply(x[l]);
        }

        // updateXandS();  // thermalization(?)を行う場合にコメントアウトすること
    }

    /**
     * 指定Sweep分シミュレーションを実行する
     */
    public void simulate(int skipSweep) {
        for(int c = 0; c < skipSweep; ++ c) {
	    ++ sweep;
	    updateXandS();
        }
    }

    /**
     * 現状態のSweepDataを返す
     * @return 現状態のSweepData
     */
    public SweepData getSweepData() {
        if(Vsum == 0) {
            for(int idx = 0; idx < Ndim; ++ idx) {
                Vsum += Vpot.apply(x[idx]);
            }
        }
        return new SweepData(sweep, x, Ksum+Vsum, Ksum, Vsum);
    }

    /**
     * 採用率を計算して返す
     * @return 採用率
     */
    public double getAcceptRatio() {
        int acceptCnt = sweep*Ndim;
        return (double)(acceptCnt)/(acceptCnt+rejectCnt);
    }

    /**
     * x, S(x)の更新処理を行う
     * @return 非採用のx_newが生成された回数
     */
    private void updateXandS() {
        Ksum = 0; Vsum = 0;
        for(int idx = 0; idx < Ndim; ++ idx) {
            while(true){
                double xlnew = x[idx] + hstep * (2*frand.next()-1);
                int lp1 = (idx+1)%Ndim, lm1 = (idx-1+Ndim)%Ndim;
                double Knew = 0.5*(Math.pow(x[lm1]-xlnew, 2) + Math.pow(xlnew-x[lp1], 2));
                double Vnew = Vpot.apply(xlnew);
                double Schk = Math.exp(-((Knew+Vnew) - S[idx]) / hbar);
                if(Schk > frand.next()) {   // 採用
                    x[idx] = xlnew;
                    S[idx] = Knew + Vnew;
                    Ksum += Knew;
                    Vsum += Vnew;
                    break;
                } else {                    // 非採用
                    ++ rejectCnt;
                }
            }
        }
    }
}
