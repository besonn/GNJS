package Statements;

import Statements.interfaces.Statement;
import token.Token;

public class IfesleStatement implements Statement {

    public Token token;
    public Statement condition;
    public BlockStatement ifCondition;
    public BlockStatement elseCondition;


    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(s + "IF Statement\n");
        sb.append(s + "    condition:\n");
        sb.append(condition.getString(s + "        "));
        sb.append(s + "    IFBlock:\n");
        sb.append(ifCondition.getString(s+"        "));
        sb.append(s + "    ElseBlock:\n");
        sb.append(elseCondition.getString(s+"        "));

        return sb.toString();
    }

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "IfesleStatement";
    }
    
}
