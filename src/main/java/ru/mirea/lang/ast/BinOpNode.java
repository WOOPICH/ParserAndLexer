package ru.mirea.lang.ast;

import ru.mirea.lang.Token;

public class BinOpNode extends OperNode {

    public final Token op;
    public final OperNode left;
    public final OperNode right;

    public BinOpNode(Token op, OperNode left, OperNode right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + op.text + right.toString() + ")";
    }
}
