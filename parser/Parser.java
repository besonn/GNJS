package parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import Statements.*;
import Statements.interfaces.Statement;
import scanner.Scanner;
import token.Token;
import token.TokenType;

public class Parser {
    private Scanner scanner;
    private Token curToken;

    private Token peekToken;


    public Parser(Scanner l) {
        scanner = l;
        nextToken();
        nextToken();
    }

    private void nextToken() {
        curToken = peekToken;
        peekToken = scanner.nextToken();

        //去掉注释
        while (curToken != null && Objects.equals(curToken.type, TokenType.COMMENT))
            nextToken();
    }

    public boolean isEnd() {
        return scanner.isEnd();
    }


    /**
     *
     * @return Statement
     */
    public Statement getNextStatement() {
        // 从文件输入源码
        if (scanner.sourceCode) {
            if (scanner.isEnd())
                return null;
            else {
                Statement s = null;
                //System.out.println(curToken.type);
                if (checkType(curToken.type, TokenType.VAR)) {
                    s = parseVarStatement();

                    /**
                     * To check ';'
                     */
                    if (!checkSemicolon())
                        s = null;

                } else if (checkType(curToken.type, TokenType.RETURN)) {
                    s = parseReturnStatement();

                    if (!checkSemicolon())
                        s = null;

                } else if (checkType(curToken.type, TokenType.IF)) {
                    s = parseIfStatement();
                } else if (checkType(curToken.type, TokenType.WHILE)) {
                    s = parseWhileStatement();
                } else if (checkType(curToken.type, TokenType.IMPORT)){
                    s = parseImportStatement();
                    if (!checkSemicolon())
                        s = null;
                } else if (checkType(curToken.type, TokenType.PRINT)){
                    s = parsePrintStatement();
                    if (!checkSemicolon())
                        s = null;
                }
                else {

                    s = parseExpressionStatement();

                    if (!checkSemicolon())
                        s = null;
                }

                return s;
            }
        } else {
            Statement s = null;
            int cnt = 0;
            // 10个以上连续的 TokenType.ILLEGAL 就返回null
            while (checkType(curToken.type, TokenType.ILLEGAL)) {
                nextToken();

                cnt++;

                if (cnt > 10 && checkType(curToken.type, TokenType.ILLEGAL))
                    return s;
            }

            if (checkType(curToken.type, TokenType.VAR)) {
                s = parseVarStatement();

                /**
                 * To check ';'
                 */
                if (!checkSemicolon())
                    s = null;

            } else if (checkType(curToken.type, TokenType.RETURN)) {
                s = parseReturnStatement();

                if (!checkSemicolon())
                    s = null;

            } else if (checkType(curToken.type, TokenType.IF)) {
                s = parseIfStatement();
            } else if (checkType(curToken.type, TokenType.WHILE)) {
                s = parseWhileStatement();
            } else if (checkType(curToken.type, TokenType.IMPORT)){
                s = parseImportStatement();
                if (!checkSemicolon())
                    s = null;
            } else if (checkType(curToken.type, TokenType.PRINT)){
                s = parsePrintStatement();
                if (!checkSemicolon())
                    s = null;
            } else {
                s = parseExpressionStatement();

                if (!checkSemicolon())
                    s = null;
            }

            return s;
        }


    }


    private Statement getNextStatementWithoutCheck() {
        if (scanner.isEnd())
            return null;
        else {
            Statement s = null;

            if (checkType(curToken.type, TokenType.VAR)) {
                s = parseVarStatement();
            } else if (checkType(curToken.type, TokenType.RETURN)) {
                s = parseReturnStatement();
            }

            else if (checkType(curToken.type, TokenType.IF)) {
                s = parseIfStatement();
            } else if (checkType(curToken.type, TokenType.WHILE)) {
                s = parseWhileStatement();
            } else if (checkType(curToken.type, TokenType.IMPORT)){
                s = parseImportStatement();
            } else if (checkType(curToken.type, TokenType.PRINT)){
                s = parsePrintStatement();

            } else {
                s = parseExpressionStatement();
            }

            return s;
        }
    }

    /**
     * print a;
     * @return
     */
    private Statement parsePrintStatement(){
        if (!checkType(curToken.type, TokenType.PRINT)) {
            error(curToken.type, TokenType.PRINT, curToken.pos);
            return null;
        }
        PrintlnStatement printLnStatement =new PrintlnStatement();
        printLnStatement.token=curToken;
        nextToken();
        Statement value = null;
        switch (curToken.type) {
            case TokenType.VAR:
                value = parseVarStatement();
                break;
            case TokenType.IF:
                value = parseIfStatement();
                break;
            case TokenType.WHILE:
                value = parseWhileStatement();
                break;
            case TokenType.RETURN:
                value = parseReturnStatement();
                break;
            default:
                value = parseExpressionStatement();
        }
        printLnStatement.value=value;
        return printLnStatement;

    }

    /**
     * To parse Var Statement
     *
     * @return
     */
    private Statement parseVarStatement() {
        /**
         * To make sure curToken is the keyword 'Var'.
         */
        if (!checkType(curToken.type, TokenType.VAR)) {
            error(curToken.type, TokenType.VAR, curToken.pos);
            return null;
        }

        VarStatement varStatement = new VarStatement();

        varStatement.token = new Token(curToken);

        nextToken();

        if (!checkType(curToken.type, TokenType.IDEN)) {
            error(curToken.type, TokenType.IDEN, curToken.pos);
            varStatement = null;
            return null;
        }

        /**
         * var a = b, so the name is a.
         */
        varStatement.name = new Token(curToken);

        nextToken();

        if (!checkType(curToken.type, TokenType.ASSIGN)) {
            error(curToken.type, TokenType.IDEN, curToken.pos);
            varStatement = null;
            return null;
        }

        nextToken();

        Statement value = null;

        switch (curToken.type) {
            case TokenType.VAR:
                value = parseVarStatement();
                break;
            case TokenType.IF:
                value = parseIfStatement();
                break;
            case TokenType.WHILE:
                value = parseWhileStatement();
                break;
            case TokenType.RETURN:
                value = parseReturnStatement();
                break;
            default:
                value = parseExpressionStatement();
        }


        varStatement.value = value;

        return varStatement;
    }

    /**
     * Parse identifier.
     *
     * @return
     */
    private Statement parseIdentifier() {
        if (!checkType(curToken.type, TokenType.IDEN)) {
            error(curToken.type, TokenType.IDEN, curToken.pos);
            return null;
        }
        IdentifierStatement identifierStatement = new IdentifierStatement(curToken);

        Statement res = identifierStatement;

        nextToken();

        // Identifier(
        if (checkType(curToken.type, TokenType.LPAREN)) {

            CallFunctionStatement callFunctionStatement = new CallFunctionStatement();
            callFunctionStatement.function = identifierStatement;

            while (checkType(curToken.type, TokenType.LPAREN)) {
                callFunctionStatement.arguments.add(parseArguments());
            }

            res = callFunctionStatement;

            // Identifier[
        }
        else if (checkType(curToken.type, TokenType.LSBRACE)) {

            GetElemetStatement getElemetStatement = new GetElemetStatement();
            getElemetStatement.listName = identifierStatement;

            nextToken();

            getElemetStatement.listIndex = parseExpressionStatement();

            if (!checkType(curToken.type, TokenType.RSBRACE)) {
                error(curToken.type, TokenType.RSBRACE, curToken.pos);
                getElemetStatement = null;
                return null;
            }

            nextToken();

            res = getElemetStatement;
        }
        else if(checkType(curToken.type, TokenType.SELFADD)){
            // 相当于 i=i+1
            VarStatement varStatement= new VarStatement();
            varStatement.token=new Token(TokenType.VAR,TokenType.VAR);
            varStatement.name=identifierStatement.name;
            ExpressionStatement tmp=new ExpressionStatement();
            tmp.opr=new Token(TokenType.ADD,TokenType.ADD);
            tmp.left=identifierStatement;
            // 常量1
            ExpressionStatement number_1_Statement = new ExpressionStatement();
            number_1_Statement.opr = new Token(TokenType.NUMBER,"1");
            number_1_Statement.left = number_1_Statement.right = null;
            number_1_Statement.isLeaf = true;

            tmp.right=number_1_Statement;
            tmp.isLeaf=false;
            varStatement.value=tmp;

            nextToken();
            res=varStatement;
        }
        else if(checkType(curToken.type, TokenType.SELFSUB)){
            // 相当于 i=i+(-1)
            VarStatement varStatement= new VarStatement();
            varStatement.token=new Token(TokenType.VAR,TokenType.VAR);
            varStatement.name=identifierStatement.name;
            ExpressionStatement tmp=new ExpressionStatement();
            tmp.opr=new Token(TokenType.ADD,TokenType.ADD);
            tmp.left=identifierStatement;
            // 常量1
            ExpressionStatement number_1_Statement = new ExpressionStatement();
            number_1_Statement.opr = new Token(TokenType.NUMBER,"-1");
            number_1_Statement.left = number_1_Statement.right = null;
            number_1_Statement.isLeaf = true;

            tmp.right=number_1_Statement;
            tmp.isLeaf=false;
            varStatement.value=tmp;

            nextToken();
            res=varStatement;
        }
        return res;
    }

    /**
     * Parse arguments like '(1,2,3,a,b,c)'.
     *
     * @return
     */
    private LinkedList<Statement> parseArguments() {
        // (
        if (!checkType(curToken.type, TokenType.LPAREN)) {
            error(curToken.type, TokenType.LPAREN, curToken.pos);
            return null;
        }

        LinkedList<Statement> res = new LinkedList<>();

        nextToken();

        while (!checkType(curToken.type, TokenType.RPAREN)) {
            Statement s = parseExpressionStatement();
            //System.out.println(s.getType());
            if (!(s instanceof IdentifierStatement || s instanceof ExpressionStatement || s instanceof CallFunctionStatement || s instanceof FuctionDefined)) {
                error(s.getType(), "Identifier, Expression, Function or Function Invocation", curToken.pos);
                res = null;
                return null;
            }

            res.add(s);

            if (checkType(curToken.type, TokenType.RPAREN))
                break;

            if (!checkType(curToken.type, TokenType.COMMA)) {
                error(curToken.type, TokenType.SEMICOLON, curToken.pos);
                res = null;
                return null;
            }

            nextToken();

        }

        nextToken();

        return res;
    }



    /**
     * Parse IfesleStatement like:
     * if (a > b)
     * {
     * ......
     * }
     * else
     * {
     * .......
     * }
     *
     * @return
     */
    private Statement parseIfStatement() {
        if (!checkType(curToken.type, TokenType.IF)) {
            error(curToken.type, TokenType.IF, curToken.pos);
            return null;
        }

        IfesleStatement ifesleStatement = new IfesleStatement();

        ifesleStatement.token = new Token(curToken);

        nextToken();

        if (!checkType(curToken.type, TokenType.LPAREN)) {
            error(curToken.type, TokenType.LPAREN, curToken.pos);
            ifesleStatement = null;
            return null;
        }

        ifesleStatement.condition = parseExpressionStatement();

        ifesleStatement.ifCondition = parseBlockStatement();

        if (checkType(curToken.type, TokenType.ELSE)) {
            nextToken();
            ifesleStatement.elseCondition = parseBlockStatement();
        } else {
            ifesleStatement.elseCondition = null;
        }

        return ifesleStatement;

    }

    private Statement parseWhileStatement() {
        if (!checkType(curToken.type, TokenType.WHILE)) {
            error(curToken.type, TokenType.WHILE, curToken.pos);
            return null;
        }

        WhileStatement whileStatement = new WhileStatement();

        whileStatement.token = new Token(curToken);

        nextToken();

        if (!checkType(curToken.type, TokenType.LPAREN)) {
            error(curToken.type, TokenType.LPAREN, curToken.pos);
            whileStatement = null;
            return null;
        }
        nextToken();

        whileStatement.condition = parseExpressionStatement();


        if (!checkType(curToken.type, TokenType.RPAREN)) {
            error(curToken.type, TokenType.RPAREN, curToken.pos);
            whileStatement = null;
            return null;
        }
        nextToken();
        whileStatement.whileCondition = parseBlockStatement();

//        if (checkType(curToken.type, TokenType.ELSE)) {
//            nextToken();
//            whileStatement.elseCondition = parseBlockStatement();
//        } else {
//            whileStatement.elseCondition = null;
//        }

        return whileStatement;

    }


//    private BreakStatement parseBreakStatement(){
//        if(checkType(curToken.type,TokenType.BREAK)){
//            return getType()
//        }
//    }

    /**
     * Parse ImportStatement like:
     * import "a.NumbLang"
     * @return
     */
    private BlockStatement parseImportStatement(){
        nextToken();
        if(!checkType(curToken.type, TokenType.STRING)){
            error(curToken.type, TokenType.STRING, curToken.pos);
            return null;
        }
        String filePath= curToken.value;
        //System.out.println(filePath);
        nextToken();
        try{
            Path path = Paths.get("input/"+filePath);
            byte[] data = Files.readAllBytes(path);
            String stats_import = new String(data);
            Scanner scanner_import = new Scanner(stats_import);
            Parser parser_import = new parser.Parser(scanner_import);
            BlockStatement bls = new BlockStatement();
            while(!parser_import.isEnd()){
                Statement s = parser_import.getNextStatement();
                bls.statements.add(s);
            }
            return bls;
        }catch (Exception e){
            noFileError(filePath, curToken.pos);
            return null;
        }

    }
    /**
     * Parse BlockStatements like:
     * {
     * var a = b;
     * var c = 5;
     * ......
     * }
     *
     * @return
     */
    private BlockStatement parseBlockStatement(){
        if (!checkType(curToken.type, TokenType.LBRACE)) {
            error(curToken.type, TokenType.LBRACE, curToken.pos);
            return null;
        }

        nextToken();

        BlockStatement bls = new BlockStatement();

        while (!checkType(curToken.type, TokenType.RBRACE)) {
            Statement s = getNextStatement();
            bls.statements.add(s);
        }


        // Make sure that curToken is '}'
        if (!checkType(curToken.type, TokenType.RBRACE)) {
            error(curToken.type, TokenType.RBRACE, curToken.pos);
            bls = null;
            return null;
        }

        nextToken();

        return bls;
    }

    /**
     * Parse FuctionDefined like:
     * fn(x,y)
     * {
     * ......
     * }
     *
     * @return
     */
    private Statement parseFunctionStatement() {
        if (!checkType(curToken.type, TokenType.FUNCTION)) {
            error(curToken.type, TokenType.FUNCTION, curToken.pos);
            return null;
        }

        Statement res = null;
        FuctionDefined fuctionDefined = new FuctionDefined();

        nextToken();

        fuctionDefined.parameters = parseArguments();

        // According to the gramma.
        if (!checkType(curToken.type, TokenType.LBRACE)) {
            error(curToken.type, TokenType.LBRACE, curToken.pos);
            fuctionDefined = null;
            return null;
        }

        fuctionDefined.body = parseBlockStatement();

        res = fuctionDefined;

        /**
         * To check whether it is a function invocation statement.
         */
        if (checkType(curToken.type, TokenType.LPAREN)) {
            CallFunctionStatement callFunctionStatement = new CallFunctionStatement();
            callFunctionStatement.function = fuctionDefined;

            while (checkType(curToken.type, TokenType.LPAREN)) {
                callFunctionStatement.arguments.add(parseArguments());
            }

            res = callFunctionStatement;
        }

        return res;
    }

    /**
     * Parse ReturnStatement like:
     * return a + b * c;
     *
     * @return
     */
    private Statement parseReturnStatement() {
        if (!checkType(curToken.type, TokenType.RETURN)) {
            error(curToken.type, TokenType.RETURN, curToken.pos);
            return null;
        }

        ReturnStatement returnStatement = new ReturnStatement();
        returnStatement.token = new Token(curToken);
        nextToken();

        returnStatement.returnValue = parseExpressionStatement();

        return returnStatement;
    }

    /**
     * Parse Expressions.
     * In the following patern:
     * X  := A X'
     * X' :=  && AX' | || AX' | null
     * A  := B A'
     * A' := == B A' | != BA' | null
     * B  := C B'
     * B' := > C B' | < C B' | null
     * C  := D C'
     * C' := +DC' | -DC' | null
     * D  := ED'
     * D' := *ED' | /ED' | null
     * E  := (A) | num | iden | ~E  | iden++ | iden--
     *
     * E还处理了FUNCTION, LSBRACE,STRING,LBRACE,
     *
     * @return
     */

    private Statement parseExpressionStatement() {
        return X();
    }

    /**
     * new-Top layer.
     *
     * @return
     */
    private Statement X() {
        ExpressionStatement root = new ExpressionStatement();
        root.left = A();

        Statement tmp = X_1(root);

        return tmp == null ? root.left : tmp;

    }

    /**
     * Second new-top.
     *
     * @param root
     * @return
     */
    private Statement X_1(ExpressionStatement root) {
        if (checkType(curToken.type, TokenType.AND) || checkType(curToken.type, TokenType.OR)) {
            root.opr = new Token(curToken);
            nextToken();
            root.right = A();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp = X_1(root1);

            return tmp == null ? root : tmp;
        } else
            return null;
    }

    /**
     * Top layer.
     *
     * @return
     */
    private Statement A() {
        ExpressionStatement root = new ExpressionStatement();
        root.left = B();

        Statement tmp = A_1(root);

        return tmp == null ? root.left : tmp;

    }

    /**
     * Second top.
     *
     * @param root
     * @return
     */
    private Statement A_1(ExpressionStatement root) {
        if (checkType(curToken.type, TokenType.EQU) || checkType(curToken.type, TokenType.NEQU)) {
            root.opr = new Token(curToken);
            nextToken();
            root.right = B();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp = A_1(root1);

            return tmp == null ? root : tmp;
        } else
            return null;
    }

    private Statement B() {
        ExpressionStatement root = new ExpressionStatement();
        root.left = C();
        Statement tmp = B_1(root);

        return tmp == null ? root.left : tmp;

    }

    private Statement B_1(ExpressionStatement root) {
        if (checkType(curToken.type, TokenType.GT) || checkType(curToken.type, TokenType.LT)) {
            root.opr = new Token(curToken);
            nextToken();
            root.right = C();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp = B_1(root1);

            return tmp == null ? root : tmp;

        } else
            return null;
    }

    private Statement C() {
        ExpressionStatement root = new ExpressionStatement();
        root.left = D();
        Statement tmp = C_1(root);

        return tmp == null ? root.left : tmp;
    }

    private Statement C_1(ExpressionStatement root) {
        if (checkType(curToken.type, TokenType.ADD) || checkType(curToken.type, TokenType.SUB)) {
            root.opr = new Token(curToken);
            nextToken();
            root.right = D();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp = C_1(root1);

            return tmp == null ? root : tmp;
        } else
            return null;
    }

    private Statement D() {
        ExpressionStatement root = new ExpressionStatement();
        root.left = E();
        Statement tmp = D_1(root);

        return tmp == null ? root.left : tmp;
    }

    private Statement D_1(ExpressionStatement root) {
        if (checkType(curToken.type, TokenType.MUL) || checkType(curToken.type, TokenType.DIV)) {
            root.opr = new Token(curToken);
            nextToken();
            root.right = E();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp = D_1(root1);

            return tmp == null ? root : tmp;
        } else
            return null;
    }


    private Statement E() {
        Statement res = null;
        // ~
        if (checkType(curToken.type, TokenType.OPPOSITE)) {
            ExpressionStatement ops = new ExpressionStatement();
            ops.opr = new Token(curToken);
            nextToken();
            ops.left = E();
            ops.right = null;
            res = ops;
        }  else{
            if (checkType(curToken.type, TokenType.NUMBER) || checkType(curToken.type, TokenType.FLOAT) ||
                    checkType(curToken.type, TokenType.TRUE) || checkType(curToken.type, TokenType.FALSE)) {
                ExpressionStatement expressionStatement = new ExpressionStatement();
                expressionStatement.opr = new Token(curToken);
                nextToken();
                expressionStatement.left = expressionStatement.right = null;
                expressionStatement.isLeaf = true;
                res = expressionStatement;
            } else if (checkType(curToken.type, TokenType.IDEN)) {
                res = parseIdentifier();
            } else if (checkType(curToken.type, TokenType.FUNCTION)) {
                res = parseFunctionStatement();
            } else if (checkType(curToken.type, TokenType.BREAK)) {
                res = new BreakStatement();
                nextToken();

            } else if (checkType(curToken.type, TokenType.LPAREN)) {
                nextToken();

                res = X();

                if (!checkType(curToken.type, TokenType.RPAREN)) {
                    error(curToken.type, TokenType.RPAREN, curToken.pos);
                    res = null;
                }

                nextToken();
            } else if (checkType(curToken.type, TokenType.LSBRACE)) {
                res = parseListStatement();
            } else if (checkType(curToken.type, TokenType.STRING)) {
                res = new StringStatement(curToken.value);
                nextToken();
            } else if (checkType(curToken.type, TokenType.LBRACE)) {
                res = parseDictStatement();
            } else if (checkType(curToken.type, TokenType.BUILDIN)) {
                res = parseBuildIn();
            } else {
                error(curToken.type, "Number, Identifier or '('", curToken.pos);
                nextToken();
            }

        }


        return res;
    }

    /**
     * To parse ListStatement like '[element 1, element 2, ... ,element n]'
     *
     * @return ListStatement
     */

    private Statement parseListStatement() {
        ListStatement res = new ListStatement();

        // check curToken is '['
        if (!checkType(curToken.type, TokenType.LSBRACE)) {
            res = null;
            error(curToken.type, TokenType.LSBRACE, curToken.pos);
            return null;
        }

        nextToken();

        while (!checkType(curToken.type, TokenType.RSBRACE)) {
            Statement element = parseExpressionStatement();

            res.store.add(element);

            if (checkType(curToken.type, TokenType.RSBRACE))
                break;

            // check curToken is ',' or not.
            if (!checkType(curToken.type, TokenType.COMMA)) {

                error(curToken.type, TokenType.COMMA, curToken.pos);
                res = null;
                return null;
            }

            nextToken();
        }


        // check curToken is ']' or not again.
        if (!checkType(curToken.type, TokenType.RSBRACE)) {
            res = null;
            error(curToken.type, TokenType.COMMA, curToken.pos);
            return null;
        }

        nextToken();

        return res;
    }


    private Statement parseDictStatement() {
        DictionaryStatement res = null;

        if (!checkType(curToken.type, TokenType.LBRACE)) {
            error(curToken.type, TokenType.LBRACE, curToken.pos);
            return res;
        }

        nextToken();

        res = new DictionaryStatement();

        while (!checkType(curToken.type, TokenType.RBRACE)) {
            if (!checkType(curToken.type, TokenType.STRING)) {
                error(curToken.type, "Dict's key should be STRING", curToken.pos);
                return res;
            }

            StringStatement key = (StringStatement) parseExpressionStatement();

            if (!checkType(curToken.type, TokenType.COLONS)) {
                error(curToken.type, TokenType.COLONS, curToken.pos);
                res = null;
                return res;
            }

            nextToken();

            Statement value = parseExpressionStatement();

            res.dict.put(key, value);

            if (checkType(curToken.type, TokenType.RBRACE))
                break;

            if (!checkType(curToken.type, TokenType.COMMA)) {
                error(curToken.type, TokenType.COMMA, curToken.pos);
                res = null;
                return res;
            }

            nextToken();
        }

        if (!checkType(curToken.type, TokenType.RBRACE)) {
            error(curToken.type, TokenType.RBRACE, curToken.pos);
            res = null;
            return res;
        }

        nextToken();

        return res;
    }

    private Statement parseBuildIn() {
        Statement res = null;

        ArrayList<Statement> paraList = new ArrayList<Statement>();

        if (!checkType(curToken.type, TokenType.BUILDIN)) {
            error(curToken.type, TokenType.BUILDIN, curToken.pos);
            return null;
        }

        BuildInStatement len = new BuildInStatement();

        len.func = new Token(curToken);

        // (
        nextToken();

        if (!checkType(curToken.type, TokenType.LPAREN)) {
            error(curToken.type, TokenType.LPAREN, curToken.pos);
            len = null;
            return null;
        }

        nextToken();

        while(!checkType(curToken.type, TokenType.RPAREN)){
            Statement element = getNextStatementWithoutCheck();
            paraList.add(element);
            if (checkType(curToken.type, TokenType.RPAREN))
                break;
            if (!checkType(curToken.type, TokenType.COMMA)) {
                error(curToken.type, TokenType.COMMA, curToken.pos);
                res = null;
                return null;
            }
            nextToken();
        }

        if(paraList.size() == 0){
            len = null;
            res = null;
            return null;
        }
        else if(paraList.size() == 1){
            len.lenObj = paraList.get(0);
            len.lenObj2 = null;
        }
        else{
            ArrayList<Statement> tempList = new ArrayList<Statement>();
            len.lenObj = paraList.get(0);
            for (Statement sta : paraList) {
                if(sta == paraList.get(0))
                    continue;
                tempList.add(sta);
            }
            len.lenObj2 = tempList;
        }

        //nextToken();
        // )
        if (!checkType(curToken.type, TokenType.RPAREN)) {
            error(curToken.type, TokenType.RPAREN, curToken.pos);
            len = null;
            return null;
        }

        // ;
        nextToken();

        res = len;

        return res;
    }


    /**
     * To check wether one type equals to another.
     *
     * @param typeA Type one.
     * @param typeB Type two.
     * @return
     */
    private boolean checkType(String typeA, String typeB) {
        return Objects.equals(typeA, typeB);
    }

    private void error(String curType, String expectType, int pos) {

        System.out.println("[ERROR] Line " + pos + ": " + "Current type:" + curType + " Expect type:" + expectType);
    }
    private void noFileError(String file, int pos){
        System.out.println("[ERROR] Line " + pos + ": "+ file + " can not be found.");
    }

    private boolean checkSemicolon() {
        if (!checkType(curToken.type, TokenType.SEMICOLON)) {
            error(curToken.type, TokenType.SEMICOLON, curToken.pos);
            return false;
        }
        nextToken();
        return true;
    }

}
