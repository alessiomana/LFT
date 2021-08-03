import java.io.*;
import LexerFinale.*;

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

    public void prog(){
        switch(look.tag){
            case '=':
            case '{':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
                statlist();
                match(Tag.EOF);
                break;
            
            default:
                error("\nPARSER: syntax error in PROG\nDetected: " + String.valueOf(look.tag) + "\nExpected: =, {, print, read, cond, while");
                break;
        }
    }

    public void statlist(){
        switch(look.tag){
            case '=':
            case '{':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
                stat();
                statlistp();
                break;
            
            default:
                error("\nPARSER: syntax error in STATLIST\nDetected: " + String.valueOf(look.tag) + "\nExpected: =, {, print, read, cond, while");
                break;
        }
    }

    public void statlistp(){
        switch(look.tag){
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            
            case Tag.EOF:
            case '}':
                break;
        
            default:
                error("\nPARSER: syntax error in STATLISTP\nDetected: " + String.valueOf(look.tag) + "\nExpected: ;, EOF, }");
                break;
        }
    }

    public void stat(){
        switch(look.tag){
            case '=':
                match('=');
                match(Tag.ID);
                expr();
                break;

            case '{':
                match('{');
                statlist();
                match('}');
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist();
                match(')');
                break;

            case Tag.READ:
                match(Tag.READ);
                match('(');
                match(Tag.ID);
                match(')');
                break;

            case Tag.COND:
                match(Tag.COND);
                whenlist();
                match(Tag.ELSE);
                stat();
                break;

            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                bexpr();
                match(')');
                stat();
                break;

            default:
                error("\nPARSER: syntax error in STAT\nDetected: " + String.valueOf(look.tag) + "\nExpected: =, {, print, read, cond, while");
                break;  
        }
    }

    public void whenlist(){
        if(look.tag == Tag.WHEN){
            whenitem();
            whenlistp();
        }else
            error("\nPARSER: syntax error in WHENLIST\nDetected: " + String.valueOf(look.tag) + "\nExpected: when");
    }

    public void whenlistp(){
        switch(look.tag){
            case Tag.WHEN:
                whenitem();
                whenlistp();
                break;

            case Tag.ELSE:
                break;

            default:
                error("\nPARSER: syntax error in WHENLISTP\nDetected: " + String.valueOf(look.tag) + "\nExpected: when, else");
                break;
        }
    }

    public void whenitem(){
        if(look.tag == Tag.WHEN){
            match(Tag.WHEN);
            match('(');
            bexpr();
            match(')');
            match(Tag.DO);
            stat();
        }else
            error("\nPARSER: syntax error in WHENITEM\nDetected: " + String.valueOf(look.tag) + "\nExpected: when");
    }   

    public void bexpr(){
        if(look.tag == Tag.RELOP){
            match(Tag.RELOP);
            expr();
            expr();
        }else
            error("\nPARSER: syntax error in BEXPR\nDetected: " + String.valueOf(look.tag) + "\nExpected: RELOP");
    }

    public void expr(){
        switch(look.tag){
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                break;

            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                break;

            case '-':
                match('-');
                expr();
                expr();
                break;
            
            case '/':
                match('/');
                expr();
                expr();
                break;  
                
            case Tag.NUM:
                match(Tag.NUM);
                break;

            case Tag.ID:
                match(Tag.ID);
                break;

            default:
                error("\nPARSER: syntax error in EXPR\nDetected: " + String.valueOf(look.tag) + "\nExpected: +, *, -, /, NUM, ID");
                break;
        }
    }

    public void exprlist(){
        switch(look.tag){
            case '+':
            case '*':
            case '-':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;

            default:
                error("\nPARSER: syntax error in EXPRLIST\nDetected: " + String.valueOf(look.tag) + "\nExpected: +, *, -, /, NUM, ID");
                break;
        }
    }

    public void exprlistp(){
        switch(look.tag){
            case '+':
            case '*':
            case '-':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            
            case ')':
                break;

            default:
                error("\nPARSER: syntax error in EXPRLISTP\nDetected: " + String.valueOf(look.tag) + "\nExpected: +, *, -, /, NUM, ID, )");
                break;
        }
    }
        
    /*
    OPERAZIONI PREFISSE.
        +, * -> prefisse con parentesi obbligate
        -, / -> prefisse binarie senza parentesi

        ;    -> SEMPRE tranne per ultimissima istruzione del blocco di codice
    */
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "LexerFinale/Input.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}