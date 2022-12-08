package executor.object;

import java.util.LinkedList;

import Statements.BlockStatement;
import Statements.interfaces.Statement;
import executor.enviroment.Enviroment;
import executor.object.interfaces.ObjectInternal;

public class FunctionValue implements ObjectInternal {
    public Enviroment env;
    public LinkedList<Statement> parameters;
    public BlockStatement body;

    public FunctionValue(Enviroment e)
    {
        env = e;
    }


    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.FUNCTION_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return "A function.";
    }


    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
    
}
