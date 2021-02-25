package simulator;

/**
 * オリジナルプログラムで定義されていた擬似乱数生成機
 */
public class FRandom {

    private int r_num;

    /**
     * FRandomのデフォルトコンストラクタ
     */
    public FRandom() {
        this(1);
    }

    /**
     * @param r_num 乱数生成の初期値
     */
    public FRandom(int r_num) {
        this.r_num = r_num;
    }

    /**
     * @return 生成された乱数
     */
    public double next() {
        r_num *= 48828125;
        if(r_num < 0) {
            r_num += 2147483647;
            r_num += 1;
        }
        return r_num / 2147483648.0;
    }
}