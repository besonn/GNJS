package executor.object;

import executor.object.interfaces.NumberOrBoolean;
import executor.object.interfaces.ObjectInternal;

public class IntegerValue implements ObjectInternal,NumberOrBoolean {

    public int Value;

    public IntegerValue(int a)
    {
        Value = a;
    }

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.INTEGER_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return String.valueOf(Value);
    }

    @Override
    public void opposite() {
        // TODO Auto-generated method stub
        Value = -Value;
    }

    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
    
}
