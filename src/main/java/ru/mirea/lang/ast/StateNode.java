package ru.mirea.lang.ast;

import ru.mirea.lang.Token;

public class StateNode extends OperNode {

    public final OperNode left;
    public final OperNode right;

    public StateNode(OperNode left, OperNode right) {
        this.left = left;
        this.right = right;
    }

    public StateNode(OperNode left) {
        this.left = left;
        this.right = null;
    }

    @Override
    public String toString() {
        if (right != null)
            return "(" + left.toString() + "StateNode" + right.toString() + ")";
        else
            return "(" + left.toString() + "StateNode" + ")";
    }

}