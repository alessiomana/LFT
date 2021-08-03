import java.io.*;
import LexerFinale.*;

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
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
	    int expr_val;

        if(look.tag == '(' || look.tag == Tag.NUM){
            expr_val = expr();
	        match(Tag.EOF);
            System.out.println(expr_val);
        }
        else
            error("\nVALUTATORE: syntax error in START\nDetected: " + look.tag + "\nExpected: (, Digit");
    }

    private int expr() { 
        int term_val, exprp_val=-1; 
        //inizializzo a zero per permettere il (singolo) return finale:
        /*
            il programma non sa che error() blocca l'esecuzione lanciando un Error(java standard error),
            quindi se non inizializzassi l'exprp a -1 (decisione arbitraria) il compilatore darebbe errore
            riferendosi al fatto che nel caso di passaggio per il ramo else, una volta uscito non saprebbe che
            valore associare alla variabile di ritorno exprp_val.
            NB: il return exprp_val non restituirà mai -1 in realtà (se lo farà sarà perché
            è il valore vero e proprio della cifra analizzata).
        */

        if(look.tag == '(' || look.tag == Tag.NUM){
            term_val = term();
            exprp_val = exprp(term_val);
        }else
            error("\nVALUTATORE: syntax error in EXPR\nDetected: " + look.tag + "\nExpected: (, Digit");

	    return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val = -1; 
        //inizializzo a zero per permettere il (singolo) return finale: vedi riga 46
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;
                
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;

            case Tag.EOF:
            case ')':
                exprp_val = exprp_i;
                break;

            default:
                error("\nVALUTATORE: syntax error in EXPRP\nDetected: " + look.tag + "\nExpected: +, -, EOF, )");
                break;
        }
        return exprp_val;
    }

    private int term() { 
        int fact_val, term_val = -1;
        //inizializzo a zero per permettere il (singolo) return finale: vedi riga 46
	    if(look.tag == '(' || look.tag == Tag.NUM){
            fact_val = fact();
            term_val = termp(fact_val);
        }else
            error("\nVALUTATORE: syntax error in TERM\nDetected: " + look.tag + "\nExpected: (, Digit");
    
        return term_val;
    }
    
    private int termp(int termp_i) { 
        int fact_val, termp_val = -1;
        //inizializzo a zero per permettere il (singolo) return finale: vedi riga 46
        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;

            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;

            case '+':
            case '-':
            case Tag.EOF:
            case ')':
                termp_val = termp_i;
                break;

            default:
                error("\nVALUTATORE: syntax error in TERMP\nDetected: " + look.tag + "\nExpected: *, /, +, -, EOF, )");
                break;
        }
        return termp_val;
    }
    
    private int fact() { 
        int fact_val = -1;

	    if(look.tag == '('){
            match('(');
            fact_val = expr();
            match(')');
        }else if(look.tag == Tag.NUM){
            fact_val = ((NumberTok)look).lexeme;
            match(Tag.NUM);
        }   
        else
            error("\nVALUTATORE: syntax error in FACT\nDetected: " + look.tag + "\nExpected: (, Digit");
 
        return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Input.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}