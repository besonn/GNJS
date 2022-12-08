package TestCases;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import scanner.Scanner;
import parser.Parser;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        String filePath = "input/parserTest.gnjs";
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        String input = new String(data);

        Scanner scanner = new Scanner(input);

        Parser parser = new parser.Parser(scanner);

        while (!parser.isEnd())
        {
            System.out.println(parser.getNextStatement().getString(""));
        }

    }

}

