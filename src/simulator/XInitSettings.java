package simulator;

public class XInitSettings {

    public final Tag tag;
    public final double num;

    private XInitSettings(Tag tag, double num) {
        this.tag = tag;
        this.num = num;
    }

    public enum Tag {
        FIXED,
        RANDOM
    }

    /**
     * xを乱数で初期化する
     */
    public static XInitSettings random() {
        return new XInitSettings(Tag.RANDOM, 0);
    }

    /**
     * xを固定値で初期化する
     * @param num 固定値
     */
    public static XInitSettings fixed(double num) {
        return new XInitSettings(Tag.FIXED, num);
    }

}