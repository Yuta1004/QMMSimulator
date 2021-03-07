package parser;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ExprParser {

    private Node root;
    private String expr;
    private HashMap<String, Double> var;
    private Pattern constCheck;

    /* コンストラクタ */
    public ExprParser(String expr) {
        this.expr = expr;
        constCheck = Pattern.compile("PI|E");
        var = new HashMap<String, Double>();
        var.put("PI", Math.PI);
        var.put("E", Math.E);
        var.put("x", 0.0);
    }

    /* parse :式のパースを行う */
    public void parse() {
        root = expr();
    }

    /* calc : 式の計算を行う */
    public double calc() {
        return calcChild(root);
    }

    /* calc(double x) : 式の計算を行う(変数xを指定) */
    public double calc(double x) {
        setVar("x", x);
        return calc();
    }

    /* setVar : 変数の値を設定する */
    public void setVar(String name, double value) {
        if(constCheck.matcher(name).matches()) return;
        var.put(name, value);
    }

    /* calcChild : 構文木を辿りつつ計算を行う */
    private double calcChild(Node node) {
        if(node == null) return 0;
        if(node.kind == NodeKind.NUM) return node.value;

        double leftVal = calcChild(node.left);
        double rightVal = calcChild(node.right);

        switch(node.kind){
        case ADD:
            return leftVal + rightVal;

        case SUB:
            return leftVal - rightVal;

        case MUL:
            return leftVal * rightVal;

        case DIV:
            return leftVal / rightVal;

        case LT:
            return leftVal > rightVal ? 1 : 0;

        case LTE:
            return leftVal >= rightVal ? 1 : 0;

        case EQ:
            return leftVal == rightVal ? 1 : 0;

        case NEQ:
            return leftVal != rightVal ? 1 : 0;

        case SIN:
            return Math.sin(leftVal);

        case COS:
            return Math.cos(leftVal);

        case ABS:
            return Math.abs(leftVal);

        case LOG:
            return Math.log(leftVal);

        case POW2:
            return Math.pow(leftVal, 2);

        case POW3:
            return Math.pow(leftVal, 3);

        case POW4:
            return Math.pow(leftVal, 4);

        case VAR:
            if(var.containsKey(node.varName)) {
                return var.get(node.varName);
            }
            return 0;

        default:
            return 0;
        }
    }

    /* expr = add */
    private Node expr() {
        return eval();
    }

    /* eval = add (">" add | ">=" add | "==" add | "!=" add)? */
    private Node eval() {
        Node node = add();

        if(checkPrefix(">=")) {
            return new Node(node, add(), NodeKind.LTE);
        }
        else if(checkPrefix("<=")) {
            return new Node(add(), node, NodeKind.LTE);
        }
        else if(checkPrefix(">")) {
            return new Node(node, add(), NodeKind.LT);
        }
        else if(checkPrefix("<")) {
            return new Node(add(), node, NodeKind.LT);
        }
        else if(checkPrefix("==")) {
            return new Node(node, add(), NodeKind.EQ);
        }
        else if(checkPrefix("!=")) {
            return new Node(node, add(), NodeKind.NEQ);
        }
        return node;
    }

    /* add = mul ("+" mul | "-" mul)* */
    private Node add() {
        Node node = mul();

        while(true) {
            if(checkPrefix("+")) {
                node = new Node(node, mul(), NodeKind.ADD);
                continue;
            }
            else if(checkPrefix("-")) {
                node = new Node(node, mul(), NodeKind.SUB);
                continue;
            }
            break;
        }
        return node;
    }

    /* mul = unary ("*" unary | "-" unary)* */
    private Node mul() {
        Node node = unary();

        while(true) {
            if(checkPrefix("*")) {
                node = new Node(node, unary(), NodeKind.MUL);
                continue;
            }
            else if(checkPrefix("/")) {
                node = new Node(node, unary(), NodeKind.DIV);
                continue;
            }
            break;
        }
        return node;
    }

    /* unary = ("+" | "-")* func */
    private Node unary() {
        checkPrefix("+");
        if(checkPrefix("-")) {
            return new Node(new Node(0), func(), NodeKind.SUB);
        }
        return func();
    }

    /* func = ("sin" | "cos")? num */
    private Node func() {
        if(checkPrefix("sin")) {
            return new Node(num(), null, NodeKind.SIN);
        }
        else if(checkPrefix("cos")) {
            return new Node(num(), null, NodeKind.COS);
        }
        else if(checkPrefix("abs")) {
            return new Node(num(), null, NodeKind.ABS);
        }
        else if(checkPrefix("log")) {
            return new Node(num(), null, NodeKind.LOG);
        }
        else if(checkPrefix("pow2")) {
            return new Node(num(), null, NodeKind.POW2);
        }
        else if(checkPrefix("pow3")) {
            return new Node(num(), null, NodeKind.POW3);
        }
        else if(checkPrefix("pow4")) {
            return new Node(num(), null, NodeKind.POW4);
        }

        return num();
    }

    /* num = 数 | "(" expr ")" */
    private Node num() {
        if(checkPrefix("(")) {
            Node node = expr();
            checkPrefix(")");
            return node;
        }

        String varName = getVarName();
        if(varName.length() > 0) {
            return new Node(varName);
        }

        return new Node(getNum());
    }

    /* getNum : 式の先頭から字を読み取る */
    private double getNum() {
        // 有効な数字が続く長さを調べる
        skipSpace();
        int idx = 0;
        for(; idx < expr.length(); ++ idx) {
            char c = expr.charAt(idx);
            if(!('0' <= c && c <= '9') && c != '.')
                break;
        }

        // 対象範囲を数字に変換
        double num = Double.parseDouble(expr.substring(0, idx));
        expr = expr.substring(idx, expr.length());
        return num;
    }

    /* getVarName ; 式の先頭から変数名を取り出す */
    private String getVarName() {
        skipSpace();
        int idx = 0;
        for(; idx < expr.length(); ++ idx) {
            char ch = expr.charAt(idx);
            if(('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'z') || ch == '_'){}
            else break;
        }

        String varName = expr.substring(0, idx);
        expr = expr.substring(idx, expr.length());
        return varName;
    }

    /* skipSpace : 式先頭にある空白を読み飛ばす*/
    private void skipSpace() {
        while(expr.length() > 0 && (expr.charAt(0) == ' ' || expr.charAt(0) == '\t')) {
            expr = expr.substring(1, expr.length());
        }
    }

    /* checkPrefix : 式の先頭の文字列をチェックする */
    private boolean checkPrefix(String prefix) {
        skipSpace();
        if(expr.startsWith(prefix)) {
            expr = expr.substring(prefix.length(), expr.length());
            return true;
        }
        return false;
    }
}
