package Statements;

import java.util.LinkedList;

import Statements.interfaces.Statement;

public class CallFunctionStatement implements Statement {
    public Statement function;

    // 两层参数list，实现柯里化
    public LinkedList<LinkedList<Statement>> arguments;
    public boolean posOrNeg = false;

    
    public CallFunctionStatement()
    {
        arguments = new LinkedList<>();
    }

    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(s + "Function Invocation\n");
        sb.append(s + "    function:\n");
        sb.append(function.getString(s + "        "));
        
        for (int j = 0; j < arguments.size(); j ++)
        {
            int len = arguments.get(j).size();
            sb.append(s + "        Layer" + (j+1) + "\n");
            for (int i = 0; i < len; i ++)
            {
                sb.append(s + "            Argument" + (i+1) + "\n");
                sb.append(arguments.get(j).get(i).getString(s + "                "));
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
        return "CallFunctionStatement";
    }
    
    
}
