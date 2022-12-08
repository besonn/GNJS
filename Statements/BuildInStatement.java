package Statements;

import Statements.interfaces.BuildIn;
import Statements.interfaces.Statement;
import token.Token;

import java.util.ArrayList;

public class BuildInStatement implements BuildIn {
    public Token func;
    public Statement lenObj;
    public ArrayList<Statement> lenObj2;


    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append(s + "BuildIn function: \n");
        sb.append(lenObj.getString(s + "    "));

        //System.out.println(lenObj2);
        if(lenObj2 != null){
            for(Statement tmp:lenObj2){
                //System.out.println(tmp);
                sb.append(tmp.getString(s + "    "));
            }
        }


        return sb.toString();
    }

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "BuildStatement";
    }

    @Override
    public String buildIn() {
        // TODO Auto-generated method stub
        return func.value;
    }
    
}
