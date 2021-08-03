import java.io.*;
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' '; //memorizza il SUCCESSIVO carattere letto
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }

        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':
                peek = ' ';
                return Token.div;
            case ';':
                peek = ' ';
                return Token.semicolon;
            //case '=' --> gestito sotto, nel caso in cui distingue = o ==

            /*
            inizio gestione TOKEN LUNGHI 2 CHAR
            o CASI PARTICOLARI:
                =, ==
                <, <>, <=
                <, >=
            */
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character" + " after & : "  + peek );
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character" + " after | : "  + peek );
                    return null;
                }
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else
                    return Token.assign; 
                    /*             
                                                NOTA BENE 1) 
                    non riassegno peek = ' ' perché ho gia letto il prossimo simbolo;
                    se mettessi peek = ' ' entrerei nel primo while e di conseguenza
                    leggerei il simbolo successivo, perdendo di fatto, l'analisi di questo
                    */
            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                }
                else if(peek == '>'){
                    peek = ' ';
                    return Word.ne;
                }    
                else
                    return Word.lt; //vedi NOTA BENE 1)
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else
                    return Word.gt; //vedi NOTA BENE 1)

            //caso di gestione relativo al fine file
            case (char)-1:
                return new Token(Tag.EOF);

            //caratteri alfanumeri ed eventuali simboli non riconosciuti
            default:
                if (Character.isLetter(peek)) {
                    /*
                    IDENTIFICATORI e PAROLE CHIAVE:
                    Pattern => [a-z A-Z][a-z A-Z 0-9]*
                    */
                    String aux_string = String.valueOf(peek); //stringa ausiliaria che mi conterrà l'identificatore/parola chiave analizzata
                    readch(br); 
                    while( Character.isLetter(peek) || Character.isDigit(peek) ){
                        aux_string += String.valueOf(peek);
                        readch(br);
                    }

                    /*
                    Switch tra PAROLE CHIAVE e IDENTIFICATORI di var:
                    NB non serve porre peek = ' ' perché senno consumerei il prossimo
                    carattere da analizzare, senza averlo ancora fatto
                    */
                    switch(aux_string){
                        case "cond":
                            return Word.cond;
                        case "when":
                            return Word.when;
                        case "then":
                            return Word.then;
                        case "else":
                            return Word.elsetok;
                        case "while":
                            return Word.whiletok;
                        case "do":
                            return Word.dotok;
                        case "seq":
                            return Word.seq;
                        case "print":
                            return Word.print;
                        case "read":
                            return Word.read;
                        default:
                            return new Word(Tag.ID, aux_string);
                    }
                } else if (Character.isDigit(peek)) {                    
                    String number = Character.toString(peek);
                    readch(br); 
                    while(Character.isDigit(peek)){
                        number += String.valueOf(peek);
                        readch(br);
                    }
                    if(number.charAt(0) == '0' && number.length() > 1){
                        System.err.println("Number cannot start with digit 0.");
                        return null;
                    }else
                        return new NumberTok(Tag.NUM, Integer.valueOf(number));
                } else {
                    System.err.println("Erroneous character: " + peek);
                    return null;
                }
        }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Input.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
