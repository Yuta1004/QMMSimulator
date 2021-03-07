package parser;

import java.util.HashMap;
import java.util.ArrayList;

public class ScriptParser {

    private String script;
    private HashMap<String, Double> var;
    private ArrayList<ExprParser> expr;
    private ArrayList<Boolean> vdSetting;

    /* コンストラクタ */
    public ScriptParser(String script) {
        this.script = script;
        this.var = new HashMap<String, Double>();
        this.expr = new ArrayList<ExprParser>();
        this.vdSetting = new ArrayList<Boolean>();
    }

    /* getGraphNum : 登録されているグラフの数を取得する */
    public int getGraphNum() {
        return expr.size();
    }

    /* getVDSetting : 数値積分の可視化設定を返す */
    public boolean getVDSetting(int gID) {
        if(gID >= vdSetting.size())
            return false;
        return vdSetting.get(gID);
    }

    /* calcGraph : 計算を行う */
    public double[] calcGraph(int gID, double xArray[]) {
        // 値チェック
        if(gID >= this.expr.size()) return new double[0];

        // 計算
        double yArray[] = new double[xArray.length];
        for(int idx = 0; idx < xArray.length; ++ idx) {
            yArray[idx] = this.expr.get(gID).calc(xArray[idx]);
        }
        return yArray;
    }

    /* parse : スクリプト雑パース */
    public void parse() {
        ArrayList<Integer> loopStack = new ArrayList<Integer>();
        String splitedScript[] = script.split("\n");

        for(int idx = 0; idx < splitedScript.length; ++ idx) {
            String line = splitedScript[idx];
            line = skipSpace(line);

            // 変数定義
            if(line.startsWith("var")) {
                line = line.substring(3, line.length());
                for(String varName: line.split(",")) {
                    varName = varName.replace(" ", "");
                    if(checkVarName(varName)) {
                        var.put(varName, 0.0);
                    }
                }
                continue;
            }

            // グラフ追加
            if(line.startsWith("plot")) {
                ExprParser ep = new ExprParser(line.split("<<")[1]);
                ep = setVar(ep);
                ep.parse();
                expr.add(ep);
                vdSetting.add(line.startsWith("plotd"));
                continue;
            }

            // 値代入
            if(line.contains("=")) {
                String left = line.split("=")[0].replace(" ", "");
                String right = line.split("=")[1].replace(" ", "");
                if(checkVarName(left) && var.containsKey(left)) {
                    ExprParser ep = new ExprParser(right);
                    ep.parse();
                    ep = setVar(ep);
                    var.put(left, ep.calc());
                }
                continue;
            }

            // loop
            if(line.startsWith("loop")) {
                // 条件式
                String expr = line.split(":")[1].replace(" ", "");
                ExprParser ep = new ExprParser(expr);
                ep = setVar(ep);
                ep.parse();

                // 条件が真 -> 継続, 偽 -> endまでジャンプ
                if(ep.calc() != 1.0) {
                    for(; idx < splitedScript.length; ++ idx) {
                        if(splitedScript[idx].replace(" ", "").equals("end")) break;
                    }
                } else {
                    loopStack.add(idx);
                }
                continue;
            }

            // end
            if(line.replace(" ", "").equals("end") && loopStack.size() > 0) {
                // スタックに積まれているループ始点へジャンプ
                idx = loopStack.get(loopStack.size()-1) - 1;
                loopStack.remove(loopStack.size()-1);
                continue;
            }
        }
    }

    /* skipSpace : 式先頭にある空白を読み飛ばす*/
    private String skipSpace(String target) {
        while(target.length() > 0 && (target.charAt(0) == ' ' || target.charAt(0) == '\t')) {
            target = target.substring(1, target.length());
        }
        return target;
    }

    /* setVar : 宣言されている変数全てをExprParserに渡す */
    private ExprParser setVar(ExprParser ep) {
        for(String varName: var.keySet()) {
            ep.setVar(varName, var.get(varName));
        }
        return ep;
    }

    /* checkVarName : 変数名として正しいものになっているか検証する */
    private boolean checkVarName(String varName) {
        boolean result = true;
        for(char ch: varName.toCharArray()) {
            result = result && (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_');
        }
        return result;
    }
}
