package ru.mirea.lang.ast;

import ru.mirea.lang.Token;

public class SingleOpNode extends OperNode{

    public final Token op;
    public final OperNode left;

    public SingleOpNode(Token op, OperNode left) {
        this.op = op;
        this.left = left;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + op.text + ")";
    }
}