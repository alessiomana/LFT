package LexerFinale;
import java.io.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' '; //memorizza il carattere letto
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r' || peek == '/') {
            if (peek == '\n') line++;
               
            /*
             * GESTIONE COMMENTI
            */
            if(peek == '/'){
                readch(br);
                if (peek == '/'){ 
                    //CASO //: leggo fino a che non trovo un 'a capo' oppure 'EOF'
                    while((peek != (char)-1) && (peek != '\n')) 
                        readch(br);
                }else if(peek == '*'){
                    //CASO /*: leggo fino a che non trovo la chiusura del commento: */. ERRORE in caso si trovi prima EOF
                    char old_peek = peek;
                    readch(br);
                    while( (old_peek != '*' || peek != '/') && peek != (char)-1){
                        old_peek = peek;
                        readch(br);
                    }
    
                    if(peek == (char)-1){ //ERRORE: ho trovato EOF prima della chiusura del commento
                        System.err.println("LEXER: Missing closure of a Toggle Block Comment before EOF");
                        return null;
                    }
                }
                else{
                    //CASO /: divisone, restituisco il relativo token
                    readch(br);
                    return Token.div;
                }    
            }

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
            case ';':
                peek = ' ';
                return Token.semicolon;
            /*                         NOTA BENE 1)
                case '=' --> gestito sotto, nel caso in cui distingue = o ==
                case '/' --> gestito sotto, nel caso in cui distingue / o /* o //

            */

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
                    System.err.println("LEXER: Erroneous character" + " after & : "  + peek );
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("LEXER: Erroneous character" + " after | : "  + peek );
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
                                                NOTA BENE 2) 
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
                    return Word.lt; //vedi NOTA BENE 2)
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else
                    return Word.gt; //vedi NOTA BENE 2)

            //caso di gestione relativo al fine file
            case (char)-1:
                return new Token(Tag.EOF);

            //caratteri alfanumeri ed eventuali simboli non riconosciuti
            default:
                if (Character.isLetter(peek) || peek == '_') {
                    /*
                    IDENTIFICATORI e PAROLE CHIAVE:
                    Pattern => ( [a-zA-Z] + _(_)*[a-zA-Z0-9] )( [a-zA-Z0-9] + _ )*
                    */
                    String o = "";
                    do{
                        o += String.valueOf(peek);

                        peek = ' ';
                        readch(br);
                    }while((Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') && peek != (char)-1);

                    /*
                    Switch tra PAROLE CHIAVE e IDENTIFICATORI di variabili:
                    NB non serve porre peek = ' ' perché senno consumerei il prossimo
                    carattere da analizzare, senza averlo ancora fatto (avendo richiamato, per
                    costruzione, il readch(br))
                    */
                    switch(o){
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
                            int s = 0;
                            for(int i = 0; i < o.length(); i++){
                                char c = o.charAt(i);

                                switch(s){
                                    case 0:
                                        if((c >= 'a' && c <= 'z') || (c>= 'A' && c<= 'Z'))
                                            s = 1;
                                        else if(c == '_')
                                            s = 2;
                                        else
                                            s = -1;
                                        break;
                                    case 1:
                                        if((c >= 'a' && c <= 'z') || (c>= 'A' && c<= 'Z') || (c>='0' && c<='9') || c=='_')
                                            s = 1;
                                        else
                                            s = -1;
                                        break;
                                    case 2:
                                        if((c >= 'a' && c <= 'z') || (c>= 'A' && c<= 'Z') || (c>='0' && c<='9'))
                                            s = 1;
                                        else if(c == '_')
                                            s = 2;
                                        else
                                            s = -1;
                                        break;
                                }
                            }
                            if(s == 1)
                                return new Word(Tag.ID, o);
                            else{
                                System.err.println("LEXER: Erroneous identificator or keyword: " + o);
                                return null;
                            }
                    }    
                }
                else if (Character.isDigit(peek)) {
                    String number = Character.toString(peek);
                    readch(br); 
                    while(Character.isDigit(peek)){
                        number += String.valueOf(peek);
                        readch(br);
                    }
                    if(number.charAt(0) == '0' && number.length() > 1){
                        System.err.println("LEXER: Number cannot start with digit 0.");
                        return null;
                    }else
                        return new NumberTok(Tag.NUM, Integer.valueOf(number));
                } else {
                    System.err.println("LEXER: Erroneous character: " + peek);
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
