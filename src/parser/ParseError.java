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
