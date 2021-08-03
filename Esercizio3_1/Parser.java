import java.io.*;
import LexerFinale.*;
import LexerFinale.Tag;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	    throw new Error("near line " + Lexer.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    public void start() {
        if(look.tag == '(' || look.tag == Tag.NUM){
            expr();
            match(Tag.EOF);
        }
        else
            error("\nPARSER: syntax error in START\nDetected: " + look.tag + "\nExpected: (, Digit");
    }

    private void expr() {
	    if(look.tag == '(' || look.tag == Tag.NUM){
            term();
            exprp();
        }else
            error("\nPARSER: syntax error in EXPR\nDetected: " + look.tag + "\nExpected: (, Digit");
    }

    private void exprp() {
	    switch (look.tag) {
            case '+':
                match('+');
                term();
                exprp();
                break;

            case '-':
                match('-');
                term();
                exprp();
                break;

            case Tag.EOF:
            case ')':
                break;

            default:
                error("\nPARSER: syntax error in EXPRP\nDetected: " + look.tag + "\nExpected: +, -, EOF, )");
                break;
        }
    }

    private void term() {
        if(look.tag == '(' || look.tag == Tag.NUM){
            fact();
            termp();
        }else
        error("\nPARSER: syntax error in TERM\nDetected: " + look.tag + "\nExpected: (, Digit");
    }

    private void termp() {
        switch (look.tag) {
            case '*':
                match('*');
                fact();
                termp();
                break;

            case '/':
                match('/');
                fact();
                termp();
                break;

            case '+':
            case '-':
            case Tag.EOF:
            case ')':
                break;

            default:
                error("\nPARSER: syntax error in TERMP\nDetected: " + look.tag + "\nExpected: *, /, +, -, EOF, )");
                break;
        }
    }

    private void fact() {
        if(look.tag == '('){
            match('(');
            expr();
            match(')');
        }else if(look.tag == Tag.NUM)
            match(Tag.NUM);
        else
            error("\nPARSER: syntax error in FACT\nDetected: " + look.tag + "\nExpected: (, Digit");
    }
        
    /*NOTAZIONE INFISSA*/
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "LexerFinale/Input.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}