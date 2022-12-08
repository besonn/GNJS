package executor.object;

import executor.object.interfaces.NumberOrBoolean;
import executor.object.interfaces.ObjectInternal;

public class BooleanValue implements ObjectInternal,NumberOrBoolean {
    public boolean Value;

    public BooleanValue(boolean b)
    {
        Value = b;
    }

    @Override
    public void opposite()
    {
        Value = !Value;
    }

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.BOOLEAN_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return String.valueOf(Value);
    }

    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
    
}
