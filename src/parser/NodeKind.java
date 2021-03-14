/**
 * Copyright (c) 2021 Yuta Nakagami
 *
 * Released under the MIT license.
 * see https://opensource.org/licenses/MIT
 */

package parser;

enum NodeKind {
    /* 値 */
    UNDEFINED, NUM, VAR,

    /* 演算子 */
    ADD, SUB, MUL, DIV, LT, LTE, EQ, NEQ,

    /* 関数 */
    SIN, COS, ABS, LOG, POW2, POW3, POW4;
}
