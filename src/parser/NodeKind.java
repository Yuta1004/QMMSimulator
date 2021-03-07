package parser;

enum NodeKind {
    /* 値 */
    UNDEFINED, NUM, VAR,

    /* 演算子 */
    ADD, SUB, MUL, DIV, LT, LTE, EQ, NEQ,

    /* 関数 */
    SIN, COS, ABS, LOG;
}
