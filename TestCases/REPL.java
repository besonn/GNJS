package TestCases;

import java.io.IOException;

import Statements.interfaces.Statement;
import executor.Exec;
import executor.enviroment.Enviroment;
import scanner.Scanner;
import parser.Parser;



public class REPL {

    public static final String GNJS = "GNJS";
              /*
              """
              _____   _   _        _    _____\s
             / ____| | \\ | |      | |  / ____|
            | |  __  |  \\| |      | | | (___ \s
            | | |_ | | . ` |  _   | |  \\___ \\\s
            | |__| | | |\\  | | |__| |  ____) |
             \\_____| |_| \\_|  \\____/  |_____/\s
                                             \s
            """;

               */
    public static final String Prompt = "\033[32;4m" + ">> " + "\033[0m";
    public static void main(String[] args) throws IOException {


        java.util.Scanner sc = new java.util.Scanner(System.in);

        Scanner l = new Scanner();

        Parser parser = new Parser(l);

        Enviroment env = new Enviroment(null, false);
        System.out.println(GNJS);
        System.out.print(Prompt);
        while (sc.hasNext()) {
            try {
                String code = sc.nextLine();

                l.addCode(code);
                Statement stmt = parser.getNextStatement();

                System.out.println(Exec.Eval(stmt, env).Inspect());
            } catch (Exception e) {
                System.out.println(e);
            }

            System.out.print(Prompt);
        }
    }


}
