package Statements;

import Statements.interfaces.Statement;

public class BreakStatement implements Statement {

    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(s + "BREAK Statement\n");


        return sb.toString();
    }

    @Override
    public void statementNode() {

    }

    @Override
    public String getType() {
        return "BreakStatement";
    }
}
