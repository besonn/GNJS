package Statements;

import Statements.interfaces.Statement;

public class GetElemetStatement implements Statement {
    public Statement listName;
    public Statement listIndex;

    @Override
    public String getString(String s) {
        StringBuilder sb = new StringBuilder();

        sb.append(s + "List(Dict) Element\n");
        sb.append(s + "    List(Dict):\n");
        sb.append(listName.getString(s + "    " + "    "));
        sb.append(s + "    Index:\n");
        sb.append(listIndex.getString(s + "    " + "    "));
        return sb.toString();
    }


    @Override
    public void statementNode() {

    }

    @Override
    public String getType() {
        return "GetElementStatement";
    }
}
