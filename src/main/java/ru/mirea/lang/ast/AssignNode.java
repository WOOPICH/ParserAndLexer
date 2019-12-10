package ru.mirea.lang.ast;

import ru.mirea.lang.Token;

public class AssignNode extends OperNode {

    public final Token op;
    public final Token t;
    public final OperNode left;

    public AssignNode(Token op, Token t, OperNode left) {
        this.op = op;
        this.t = t;
        this.left = left;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + op.text + " " + t.text + ")";
    }
}