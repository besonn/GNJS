package TestCases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import scanner.Scanner;
import token.Token;
import token.TokenType;

public class ScannerTest {
    public static void main(String[] args) throws IOException {
        String filePath = "input/scannerTest.gnjs";
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        String input = new String(data);

        Scanner scanner = new Scanner(input);

        Token t = scanner.nextToken();

        while (!Objects.equals(t.type, TokenType.EOF) && !Objects.equals(t.type, TokenType.ILLEGAL))
        {
            System.out.println(t.toString());
            t = scanner.nextToken();
        }

    }
}