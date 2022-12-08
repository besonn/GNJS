package executor.object;

import executor.object.interfaces.ObjectInternal;

import java.util.LinkedList;

public class ListValue implements ObjectInternal {
    public LinkedList<ObjectInternal> elements;

    public ListValue()
    {
        elements = new LinkedList<>();
    }


    public void Add(ObjectInternal ob){
        elements.add(ob);
    }

    public void DelFirst(){
        elements.removeFirst();
    }

    public void DelLast(){
        elements.removeLast();
    }

    public void Set(ObjectInternal index,ObjectInternal ob){
        String x = index.Inspect();
        //System.out.println(x);
        int Index = Integer.parseInt(x);
        elements.set(Index,ob);
    }

    @Override
    public String Type() {
        return ObjectTypes.LIST_OBJ;
    }

    @Override
    public String Inspect() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (int i = 0; i < elements.size(); i ++)
        {
            if (i == 0)
            {
                sb.append(" " + elements.get(i).Inspect());
            }
            else
            {
                sb.append(", " + elements.get(i).Inspect());
            }
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
}
