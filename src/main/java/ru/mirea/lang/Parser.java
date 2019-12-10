package ru.mirea.lang;

import ru.mirea.lang.ast.*;

import java.util.*;

public class Parser {

    private static Map<String, Integer> identifiers = new HashMap<>();
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private void error(String message) {
        if (pos < tokens.size()) {
            Token t = tokens.get(pos);
            throw new RuntimeException(message + " в позиции " + t.pos);
        } else {
            throw new RuntimeException(message + " в конце файла");
        }
    }

    private Token match(TokenType... expected) {
        if (pos < tokens.size()) {
            Token curr = tokens.get(pos);
            if (Arrays.asList(expected).contains(curr.type)) {
                pos++;
                return curr;
            }
        }
        return null;
    }

    private Token require(TokenType... expected) {
        Token t = match(expected);
        if (t == null)
            error("Ожидается " + Arrays.toString(expected));
        return t;
    }

    public static void main(String[] args) {
        String text = "x := 3e3;\n" +                             //3e3 = 1111100011
                "y := x and not (0badf00d or 0fa1ce);\n" +        //0badf00d = 1011101011011111000000001101; 0fa1ce = 11111010000111001110; not() = 100010100000000111000110000
                "print y;\n" +                                    //y = 544 = 1000100000
                "z := y or (1 or 2);\n" +                         //or() = 11
                "print z;\n" +                                    //z = 1000100011 = 547
                "print 41c10;";

        Lexer l = new Lexer(text);
        List<Token> tokens = l.lex();
        tokens.removeIf(t -> t.type == TokenType.SPACE);

        Parser p = new Parser(tokens);
        OperNode node = p.parseOperation();

        int result = eval(node);
        System.out.println("All good!");
    }

    public OperNode parseOperation() {
        OperNode state;
        Token t;
        if ((t = match(TokenType.ID)) != null) {
            state = parseAssignment(t);
        }
        else if ((t = match(TokenType.PRINT)) != null) {
            state = parsePrint(t);
        }
        else {
            error("Ожидалось присваивание идентификатора или функция print");
            return null;
        }
        if (pos < tokens.size())
            state = new StateNode(state,parseOperation());
        else
            state = new StateNode(state);
        return state;
    }

    public OperNode parseAssignment(Token t) {
        Token temp;
        if ((temp = match(TokenType.ASSIGN)) != null) {
            OperNode expr = parseExpression();
            if (match(TokenType.END) != null) {
                expr = new AssignNode(temp, t, expr);
                return expr;
            }
            else {
                error("Ожидался знак конца строки ';'");
                return null;
            }
        }
        else {
            error("Ожидался знак присваивания ':='");
            return null;
        }
    }

    public  OperNode parseExpression() {
        OperNode e1 = parseMnozh();
        Token op;
        while ((op = match(TokenType.AND, TokenType.OR, TokenType.XOR)) != null) {
            OperNode e2 = parseMnozh();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private OperNode parseMnozh() {
        Token t;
        if ((t = match(TokenType.NOT)) != null) {
            return new SingleOpNode(t, parseExpression());
        }
        if (match(TokenType.LPAR) != null) {
            OperNode e = parseExpression();
            require(TokenType.RPAR);
            return e;
        } else {
            return parseElem();
        }
    }

    private OperNode parseElem() {
        Token num = match(TokenType.NUMBER);
        if (num != null)
            return new NumberNode(num);
        Token id = match(TokenType.ID);
        if (id != null)
            return new VarNode(id);
        error("Ожидается число или переменная");
        return null;
    }

    public OperNode parsePrint(Token t){
        OperNode elem = parseElem();
        if (match(TokenType.END) != null) {
            elem = new SingleOpNode(t, elem);
            return elem;
        }
        else {
            error("Ожидался знак конца строки ';'");
            return null;
        }
    }

    public static int eval(OperNode node) {
        if (node instanceof StateNode) {
            StateNode state = (StateNode) node;
            eval(state.left);
            if (state.right != null)
                eval(state.right);
        }
        else
            if (node instanceof AssignNode) {
                AssignNode assign = (AssignNode) node;
                identifiers.put(assign.t.text,eval(assign.left));
            }
            else
                if (node instanceof NumberNode) {
                    NumberNode number = (NumberNode) node;
                    return Integer.parseInt(number.number.text, 16);
                }
                else
                    if (node instanceof BinOpNode) {
                        BinOpNode binOp = (BinOpNode) node;
                        int l = eval(binOp.left);
                        int r = eval(binOp.right);
                        switch (binOp.op.type) {
                            case AND:
                                return l & r;
                            case OR:
                                return l | r;
                            case XOR:
                                return l ^ r;
                        }
                    }
                    else
                        if (node instanceof VarNode) {
                            VarNode var = (VarNode) node;
                            Integer test = identifiers.get(var.id.text);
                            if (test == null) {
                                throw new NullPointerException("Неопределенный идентификатор: " + var.id.text);
                            }
                            else
                                return test;
                        }
                        else
                            if (node instanceof SingleOpNode) {
                                SingleOpNode singleOp = (SingleOpNode) node;
                                switch (singleOp.op.type) {
                                    case PRINT: {
                                        System.out.println(eval(singleOp.left));
                                    }
                                    case NOT: {
                                        return ~eval(singleOp.left);
                                    }
                                }
                            }
        return 0;
    }
}
