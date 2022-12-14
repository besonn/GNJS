package executor.object;

import executor.object.interfaces.ObjectInternal;

public class StringValue implements ObjectInternal {
    public String content;

    public StringValue(String s)
    {
        content = s;
    }

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.STRING_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return content;
    }

    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
    
}
