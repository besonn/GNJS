package Statements;

import Statements.interfaces.Statement;
import token.Token;

public class WhileStatement implements Statement{
    public Token token;
    public Statement condition;
    public BlockStatement whileCondition;
    // public BlockStatement elseCondition;


    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(s + "WHILE Statement\n");
        sb.append(s + "    condition:\n");
        sb.append(condition.getString(s + "        "));
        sb.append(s + "    WHILEBlock:\n");
        sb.append(whileCondition.getString(s+"        "));

        return sb.toString();
    }


    public void statementNode() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "WhileStatement";
    }

}
