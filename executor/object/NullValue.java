package executor.object;

import executor.object.interfaces.ObjectInternal;

public class NullValue implements ObjectInternal {

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return "null";
    }

    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
    
}
