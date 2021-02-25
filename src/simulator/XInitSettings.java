package simulator;

public class XInitSettings {

    public final Tag tag;
    public final int num;

    private XInitSettings(Tag tag, int num) {
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
    public static XInitSettings fixed(int num) {
        return new XInitSettings(Tag.FIXED, num);
    }

}