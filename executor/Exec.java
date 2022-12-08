package executor;

import java.util.*;

import executor.object.interfaces.ObjectInternal;

import Statements.*;
import Statements.interfaces.BuildIn;
import Statements.interfaces.Statement;
import executor.enviroment.Enviroment;
import executor.object.*;
import executor.object.interfaces.NumberOrBoolean;
import token.TokenType;

public class Exec {
    public static ObjectInternal Eval(Statement stmt, Enviroment env)
    {

        if (stmt instanceof BlockStatement)
        {
            return evalBlockStatement((BlockStatement)stmt, env);
        }
        else if (stmt instanceof CallFunctionStatement)
        {
            return evalCallStatement((CallFunctionStatement)stmt,env);
        }
        else if (stmt instanceof ExpressionStatement)
        {
            return evalExpressionStatement((ExpressionStatement)stmt, env);
        }
        else if (stmt instanceof FuctionDefined)
        {
            return evalFunctionStatement((FuctionDefined)stmt,env);
        }
        else if (stmt instanceof IdentifierStatement)
        {
            return evalIdentifierStatement((IdentifierStatement)stmt,env);
        }
        else if (stmt instanceof IfesleStatement)
        {
            return evalIfStatement((IfesleStatement)stmt,env);
        }
        else if (stmt instanceof WhileStatement)
        {
            return evalWhileStatement((WhileStatement)stmt,env);
        }
        else if (stmt instanceof BreakStatement)
        {
            return evalBreakStatement((BreakStatement)stmt,env);
        }
        else if (stmt instanceof VarStatement)
        {
            return evalVarStatement((VarStatement)stmt, env);
        }
        else if (stmt instanceof ReturnStatement)
        {
            return evalReturnStatement((ReturnStatement)stmt,env);
        }
        else if (stmt instanceof ListStatement)
        {
            return evalListStatement((ListStatement)stmt,env);
        }
        else if (stmt instanceof  GetElemetStatement)
        {
            return evalGetElementStatement((GetElemetStatement)stmt,env);
        }
        else if (stmt instanceof StringStatement)
        {
            return evalStringStatement((StringStatement)stmt,env);
        }
        else if (stmt instanceof DictionaryStatement)
        {
            return evalDictStatement((DictionaryStatement)stmt, env);
        }
        else if (stmt instanceof BuildIn)
        {
            return evalBuildInStatement((BuildIn)stmt,env);
        }else if(stmt instanceof PrintlnStatement){
            return evalPrintStatement((PrintlnStatement)stmt, env);
        }
        else
            return new NullValue();

    }

    private static ObjectInternal evalBlockStatement(BlockStatement stmt, Enviroment env)
    {
        ObjectInternal res = null;
        int flag = 0;
        for (Statement statement : stmt.statements) {

            if(statement instanceof ReturnStatement){
                flag = 1;
            }

            res = Eval(statement, env);
            //System.out.println(res.Inspect());


            if(flag == 0){
                if(res.Inspect()  == "BREAK" ){
                    return res;
                }
            }
            else{
                flag = 0;
                return res;
            }
        }
        return res;
    }


    private static ObjectInternal curry(FunctionValue retFun, LinkedList<LinkedList<Statement>> args, Enviroment env)
    {

        LinkedList<ObjectInternal>paras = getParameters(args.get(0), env);

        if (paras.size() != args.get(0).size())
        {
            error("Prameters are wrong!");
            return new NullValue();
        }

        LinkedList<Statement> saved = new LinkedList<>(args.get(0));

        args.removeFirst();



        Enviroment local = Enviroment.getNewEnviroment(env);

        if (paras.size() != retFun.parameters.size())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Parameters' size don't match:\n");
            sb.append(paras.size() + "\n");
            sb.append(retFun.parameters.size() + "\n");
            error(sb.toString());
        }

        for (int i = 0; i < retFun.parameters.size(); i ++)
        {
            if (!(retFun.parameters.get(i) instanceof IdentifierStatement))
            {
                error("function parameter's type is not identifier!");
                return new NullValue();
            }

            String name = ((IdentifierStatement)retFun.parameters.get(i)).name.value;

            local.Set(name, paras.get(i));
        }

        ObjectInternal res = Eval(retFun.body, local);

        if(args.size() == 0)
        {
            args.add(0,saved);
            return res;
        }
        else
        {
            if (!(res instanceof FunctionValue))
            {
                error("multicall function!");
                return new NullValue();
            }

            ((FunctionValue)res).env = env;

            ObjectInternal ans = curry((FunctionValue)res, args, local);
            args.add(0,saved);
            return ans;
        }
    }


    private static ObjectInternal evalCallStatement(CallFunctionStatement stmt, Enviroment env)
    {

        LinkedList<ObjectInternal> paras = getParameters(stmt.arguments.get(0), env);
        if (paras.size() != stmt.arguments.get(0).size())
        {
            error("Prameters are wrong!");
            return new NullValue();
        }

        LinkedList<Statement> saved = new LinkedList<>(stmt.arguments.get(0));

//            删除参数
//            stmt.arguments.removeFirst();

        ObjectInternal tmp = Eval(stmt.function, env);



        if (!(tmp instanceof FunctionValue))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("function:\n");
            sb.append(stmt.function.getString("    "));
            sb.append("does not exit!\n");
            error(sb.toString());
            return new NullValue();
        }

        FunctionValue fun = (FunctionValue)tmp;

        Enviroment local = Enviroment.getNewEnviroment(fun.env);



        if (paras.size() != fun.parameters.size())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Parameters' size don't match:\n");
            sb.append(paras.size() + "\n");
            sb.append(fun.parameters.size() + "\n");
            error(sb.toString());
        }

        for (int i = 0; i < fun.parameters.size(); i ++)
        {
            if (!(fun.parameters.get(i) instanceof IdentifierStatement))
            {
                error("function parameter's type is not identifier!");
                return new NullValue();
            }

            String name = ((IdentifierStatement)fun.parameters.get(i)).name.value;

            local.Set(name, paras.get(i));
        }

        if (stmt.arguments.size() == 1){
            ObjectInternal res = Eval(fun.body, local);
            return res;
        }else{
            stmt.arguments.removeFirst();
            ObjectInternal res = Eval(fun.body, local);
            if (!(res instanceof FunctionValue))
            {
                error("multicall function!");
                return new NullValue();
            }

            ((FunctionValue)res).env = local;

            ObjectInternal ans =  curry((FunctionValue)res, stmt.arguments, local);
            stmt.arguments.add(0, saved);
            return ans;
        }

    }

    private static LinkedList<ObjectInternal> getParameters(LinkedList<Statement> parameters, Enviroment env)
    {
        LinkedList<ObjectInternal> res = new LinkedList<>();


        LinkedList<ObjectInternal> tmp = new LinkedList<>();

        for (Statement s: parameters)
        {
            ObjectInternal now = Eval(s, env);

            if (now.Type() == ObjectTypes.NULL_OBJ)
                break;

            res.add(now);
        }

        return res;
    }


    private static ObjectInternal evalExpressionStatement(ExpressionStatement stmt, Enviroment env)
    {
        ObjectInternal obj = null;

        if (stmt.isLeaf)
        {

            switch(stmt.opr.type)
            {
                case TokenType.NUMBER: obj = new IntegerValue(Integer.valueOf(stmt.opr.value));break;
                case TokenType.FLOAT: obj = new FloatValue(Float.valueOf(stmt.opr.value));break;
                case TokenType.TRUE:
                case TokenType.FALSE: obj = new BooleanValue(Boolean.valueOf(stmt.opr.value));break;
                default : obj = new NullValue();
            }
            return obj;
        }
        else
        {
            if (stmt.opr.type.equals(TokenType.OPPOSITE))
            {
                obj = Eval(stmt.left, env);

                if (!(obj instanceof NumberOrBoolean))
                {
                    error("Opposite should apply to number or boolean!");
                    obj = null;
                    return new NullValue();
                }
                else
                {
                    ((NumberOrBoolean)obj).opposite();
                }
            }
            else
            {
                ObjectInternal left = Eval(stmt.left, env);
                ObjectInternal right = Eval(stmt.right, env);
                obj = oprLeftRight(left,right,stmt.opr.type);
            }
        }

        return obj;
    }

    private static ObjectInternal oprLeftRight(ObjectInternal left, ObjectInternal right, String opr)
    {
        ObjectInternal res = null;

        if (!Objects.equals(left.Type(), right.Type()))
        {
            error("Type error: " + left.Type() + " not equals to " + right.Type());
            res = new NullValue();
        }
        else
        {
            if (Objects.equals(left.Type(), ObjectTypes.BOOLEAN_OBJ))
                res = revalBoolean(left,right,opr);
            else if (Objects.equals(left.Type(), ObjectTypes.INTEGER_OBJ))
                res = revalInteger(left,right,opr);
            else if ((Objects.equals(left.Type(), ObjectTypes.FLOAT_OBJ)))
                res = revalFloat(left,right,opr);
            else if (Objects.equals(left.Type(),ObjectTypes.LIST_OBJ))
            {
                res = revalList((ListValue)left,(ListValue)right,opr);
            }
            else if (Objects.equals(left.Type(), ObjectTypes.STRING_OBJ))
            {
                res = revalString((StringValue)left, (StringValue)right, opr);
            }
            else
                res = new NullValue();
        }

        return res;
    }

    private static ObjectInternal revalBoolean(ObjectInternal left, ObjectInternal right, String opr)
    {
        ObjectInternal res = null;

        boolean lv = Boolean.valueOf(left.Inspect());
        boolean rv = Boolean.valueOf(right.Inspect());

        switch(opr)
        {
            case TokenType.AND: res = new BooleanValue(lv && rv);break;
            case TokenType.OR: res = new BooleanValue(lv || rv);break;
            case TokenType.EQU: res = new BooleanValue(lv == rv);break;
            case TokenType.NEQU: res = new BooleanValue(lv != rv);break;
            default:
                error("Unknown: " +  lv + " " + opr + " " + rv);
                res = new NullValue();
        }

        return res;
    }

    private static ObjectInternal revalInteger(ObjectInternal left, ObjectInternal right, String opr)
    {
        ObjectInternal res = null;

        int lv = Integer.valueOf(left.Inspect());
        int rv = Integer.valueOf(right.Inspect());

        switch(opr)
        {
            case TokenType.ADD: res = new IntegerValue(lv + rv);break;
            case TokenType.SUB: res = new IntegerValue(lv - rv);break;
            case TokenType.MUL: res = new IntegerValue(lv * rv);break;
            case TokenType.DIV: res = new IntegerValue(lv / rv);break;
            case TokenType.EQU: res = new BooleanValue(lv == rv);break;
            case TokenType.NEQU: res = new BooleanValue(lv != rv);break;
            case TokenType.GT: res = new BooleanValue(lv > rv);break;
            case TokenType.LT: res = new BooleanValue(lv < rv);break;
            default:
                error("Unknown: " +  lv + " " + opr + " " + rv);
                res = new NullValue();
        }

        return res;
    }

    private static ObjectInternal revalList(ListValue left, ListValue right, String opr)
    {
        ObjectInternal res = null;

        if (Objects.equals(opr, TokenType.ADD))
        {
            for (ObjectInternal o : right.elements)
            {
                left.elements.add(o);
            }

            res = left;
        }
        else
        {
            error("[Error] Unknown operation on two lists.");
            res = new NullValue();
        }

        return res;
    }


    private static ObjectInternal revalString(StringValue left, StringValue right, String opr)
    {
        ObjectInternal res = null;

        if (!Objects.equals(opr, TokenType.ADD))
        {
            error("[Error] Unknow operation on two Strings: " + opr);
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append(left.content);
            sb.append(right.content);
            res = new StringValue(sb.toString());
        }

        return res;
    }


    private static ObjectInternal revalFloat(ObjectInternal left, ObjectInternal right, String opr)
    {
        ObjectInternal res = null;

        float lv = Float.valueOf(left.Inspect());
        float rv = Float.valueOf(right.Inspect());

        switch(opr)
        {
            case TokenType.ADD: res = new FloatValue(lv + rv);break;
            case TokenType.SUB: res = new FloatValue(lv - rv);break;
            case TokenType.MUL: res = new FloatValue(lv * rv);break;
            case TokenType.DIV: res = new FloatValue(lv / rv);break;
            case TokenType.EQU: res = new BooleanValue(lv == rv);break;
            case TokenType.NEQU: res = new BooleanValue(lv != rv);break;
            default:
                error("Unknown: " +  lv + " " + opr + " " + rv);
                res = new NullValue();
        }

        return res;
    }


    private static ObjectInternal evalFunctionStatement(FuctionDefined stmt, Enviroment env)
    {
        FunctionValue res = new FunctionValue(env);

        res.parameters = stmt.parameters;
        res.body = stmt.body;

        return res;
    }


    private static ObjectInternal evalIdentifierStatement(IdentifierStatement stmt, Enviroment env)
    {
        ObjectInternal res = null;

        res = env.Get(stmt.name.value);

        if (res instanceof NullValue)
            error("Error: " + stmt.name.value + " doesn't exit!");

        return res;
    }


    private static ObjectInternal evalIfStatement(IfesleStatement stmt, Enviroment env)
    {
        boolean result = isTrue(stmt,env);
        ObjectInternal res = null;

        if (result)
        {
            res = evalBlockStatement(stmt.ifCondition, env);
        }
        else if (stmt.elseCondition != null)
        {
            res = evalBlockStatement(stmt.elseCondition, env);
        }
        else
            res = new NullValue();

        return res;

    }

    private static ObjectInternal evalWhileStatement(WhileStatement stmt, Enviroment env)
    {
        boolean result = isTrue(stmt,env);

        ObjectInternal res = null;
        if(!result)
            res = new NullValue();
        while (result)
        {
            res = evalBlockStatement(stmt.whileCondition, env);
            if(res.Inspect().toString()  == "BREAK")
                break;
            result = isTrue(stmt,env);
            // ans.Add(res);
        }

        return res;

    }
    private static boolean isTrue(WhileStatement stmt, Enviroment env)
    {
        ObjectInternal condition = Eval(stmt.condition,env);

        if (condition == null)
            return false;
        else if (condition instanceof BooleanValue && Boolean.valueOf(((BooleanValue)condition).Inspect()) == false)
        {
            return false;
        }
        else
            return true;
    }

    private static boolean isTrue(IfesleStatement stmt, Enviroment env)
    {
        ObjectInternal condition = Eval(stmt.condition,env);

        if (condition == null)
            return false;
        else if (condition instanceof BooleanValue && Boolean.valueOf(((BooleanValue)condition).Inspect()) == false)
        {
            return false;
        }
        else
            return true;
    }

    private static ObjectInternal evalPrintStatement(PrintlnStatement stmt, Enviroment env){
        ObjectInternal value = Eval(stmt.value, env);
        if(env.sourceCode){
            System.out.println(value.Inspect());
        }
        return value;
    }
    private static ObjectInternal evalVarStatement(VarStatement stmt, Enviroment env)
    {
        ObjectInternal value = Eval(stmt.value, env);
        env.Set(stmt.name.value, value);
        return env.Get(stmt.name.value);
    }

    private static ObjectInternal evalReturnStatement(ReturnStatement stmt, Enviroment env)
    {
        ObjectInternal res = null;

        res = Eval(stmt.returnValue, env);

        return res;
    }
    private static ObjectInternal evalBreakStatement(BreakStatement stmt, Enviroment env)
    {
        ObjectInternal res = new BreakValue();

        //res = Exec(stmt.returnValue, env);

        return res;
    }

    private static ObjectInternal evalListStatement(ListStatement stmt, Enviroment env)
    {
        ListValue res = new ListValue();

        for ( Statement s : stmt.store)
        {
            res.elements.add(Eval(s,env));
        }

        return res;
    }

    private static ObjectInternal evalGetElementStatement(GetElemetStatement stmt, Enviroment env)
    {
        ObjectInternal res = new NullValue();

        if (!(stmt.listName instanceof  IdentifierStatement))
        {
            error("[Error] List name  is not correct.");
        }

        IdentifierStatement listName = (IdentifierStatement) stmt.listName;

        if (!(env.Get(listName.name.value) instanceof ListValue) && !(env.Get(listName.name.value) instanceof DictValue))
        {
            error("[Error] " + listName.name.value + " is not a list or a dict.");
            return res;
        }

        if (env.Get(listName.name.value) instanceof ListValue)
        {
            ListValue list = (ListValue) env.Get(listName.name.value);

//            if (!(stmt.listIndex instanceof ExpressionStatement))
//            {
//                error("[Error] " + "list index is not an expression.");
//                return  res;
//            }
            ObjectInternal index = null;
            if (stmt.listIndex instanceof ExpressionStatement){
                index = Eval((ExpressionStatement)stmt.listIndex,env);
            }else if (stmt.listIndex instanceof  IdentifierStatement){
                index = Eval((IdentifierStatement)stmt.listIndex, env);
            }


            if (!(index instanceof IntegerValue))
            {
                error("[Error] list index is not an integer.");
                return res;
            }

            int pos = ((IntegerValue)index).Value;

            if (pos < 0 || pos >= list.elements.size())
            {
                error("[Error] index " + pos + "  is out of range.");
                return res;
            }

            return list.elements.get(pos);
        }
        else
        {

            DictValue dict = (DictValue) env.Get(listName.name.value);

            if (!(stmt.listIndex instanceof StringStatement))
            {
                error("[Error] " + "dict inded is not a strign.");
                return res;
            }

            StringValue key = (StringValue) Eval((StringStatement)stmt.listIndex, env);

            ObjectInternal tryGet = dict.dict.get(key.content);

            return tryGet == null ? res : tryGet;
        }
    }

    private static ObjectInternal evalStringStatement(StringStatement stmt, Enviroment env)
    {
        return new StringValue(stmt.content);
    }

    private static ObjectInternal evalDictStatement(DictionaryStatement stmt, Enviroment env)
    {
        DictValue res = new DictValue();

        for(Map.Entry<StringStatement, Statement> e : stmt.dict.entrySet())
        {
            StringValue key = (StringValue) Eval(e.getKey(), env);
            ObjectInternal value = Eval(e.getValue(),env);
            res.dict.put(key.content, value);
        }

        return res;
    }

    private static ObjectInternal evalBuildInStatement(BuildIn stmt, Enviroment env)
    {
        if (stmt instanceof BuildInStatement)
        {
            ObjectInternal res = new NullValue();

            if (Objects.equals(stmt.buildIn(), "sizeof"))
            {
                BuildInStatement len = (BuildInStatement)stmt;

                ObjectInternal o = Eval(len.lenObj, env);

                if (o instanceof IntegerValue)
                    res = o;
                else if (o instanceof FloatValue)
                    res = o;
                else if (o instanceof BooleanValue)
                    res = o;
                else if (o instanceof StringValue)
                    res = new IntegerValue(((StringValue)o).content.length());
                else if (o instanceof DictValue)
                    res = new IntegerValue(((DictValue)o).dict.size());
                else if (o instanceof FunctionValue)
                    res = new IntegerValue(((FunctionValue)o).parameters.size());
                else if (o instanceof ListValue)
                    res = new IntegerValue(((ListValue)o).elements.size());
            }
            else if (Objects.equals(stmt.buildIn(), "typeof"))
            {
                BuildInStatement type = (BuildInStatement)stmt;

                ObjectInternal o = Eval(type.lenObj, env);

                res =  new StringValue(o.Type().toLowerCase());
            }
            else if (Objects.equals(stmt.buildIn(), "push"))
            {
                BuildInStatement len = (BuildInStatement)stmt;

                ObjectInternal o = Eval(len.lenObj, env);

                ArrayList<Statement> paraList = len.lenObj2;

                if (o instanceof ListValue){
                    res = o;
                    for(Statement para : paraList){
                        ObjectInternal p = Eval(para,env);
                        ((ListValue) res).Add(p);
                    }
                }
            }
            else if (Objects.equals(stmt.buildIn(), "pop"))
            {
                BuildInStatement len = (BuildInStatement)stmt;

                ObjectInternal o = Eval(len.lenObj, env);

                ArrayList<Statement> paraList = len.lenObj2;
                if(o instanceof ListValue){
                    if(len.lenObj2 == null){
                        res = o;
                        ((ListValue) res).DelFirst();
                    }
                    else {
                        res = o;
                        ((ListValue) res).DelLast();
                    }
                }

            }
            else if (Objects.equals(stmt.buildIn(), "set"))
            {
                BuildInStatement len = (BuildInStatement)stmt;

                ObjectInternal o = Eval(len.lenObj, env);

                ArrayList<Statement> paraList = len.lenObj2;
                Statement index = paraList.get(0);
                Statement newOne = paraList.get(1);
                if(o instanceof ListValue){
                    res = o;
                    ObjectInternal ind = Eval(index,env);
                    ObjectInternal newone = Eval(newOne,env);
                    ((ListValue) res).Set(ind, newone);
                }
            }


            return res;
        }
        else
        {
            return new NullValue();
        }
    }


    private static void error(String msg)
    {
        System.err.println(msg);
    }
}