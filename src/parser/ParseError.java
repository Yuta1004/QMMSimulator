/**
 * Copyright (c) 2021 Yuta Nakagami
 *
 * Released under the MIT license.
 * see https://opensource.org/licenses/MIT
 */

package parser;

import java.util.Collections;

public class ParseError extends Exception {

    public ParseError(int line, int cnum, String script, String msg) {
        super(
            "Error at " + line + ":" + cnum + " => " + msg + "\n" + 
            "    " + script + "\n" +
            String.join("", Collections.nCopies(4+cnum, " ")) + "^"
        );
    }

}
