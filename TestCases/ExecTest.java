package TestCases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import executor.Exec;
import executor.enviroment.Enviroment;
import scanner.Scanner;
import parser.Parser;

public class ExecTest {
    public static void main(String[] args) throws IOException {
        //  连通图
        //  String filePath = "input/connection.gnjs";
        //  二分
         // String filePath = "input/binarySearch.gnjs";
        // 入门手册
        String filePath = "input/quickGuide.gnjs";

        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        String input = new String(data);

        Scanner scanner = new Scanner(input);

        Parser parser = new parser.Parser(scanner);

        Enviroment env = new Enviroment(null);

        while (!parser.isEnd())
        {
            Exec.Eval(parser.getNextStatement(),env).Inspect();
        }

    }
}
