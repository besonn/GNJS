package executor.object;

import executor.object.interfaces.ObjectInternal;

public class BreakValue implements ObjectInternal {

    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.BREAK_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return "BREAK";
    }

    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }

}
