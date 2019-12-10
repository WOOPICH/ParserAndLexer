package ru.mirea.lang;

import java.util.regex.Pattern;

public enum TokenType {
    OR("or"),
    NOT("not"),
    AND("and"),
    XOR("xor"),
    PRINT("print"),
    NUMBER("[0-9][a-f0-9]*"),
    ID("[a-zA-Z][a-zA-Z_0-9]*"),
    ASSIGN(":="),
    LPAR("\\("),
    RPAR("\\)"),
    END(";"),
    SPACE("[ \t\r\n]+");

    final Pattern pattern;

    TokenType(String regexp) {
        pattern = Pattern.compile(regexp);
    }
}
