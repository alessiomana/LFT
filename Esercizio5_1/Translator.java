import java.io.*;
import LexerFinale.*; 

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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

    public void prog() {
        switch(look.tag){
            case '=':
            case '{':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
                int statlist_next = code.newLabel();
                statlist(statlist_next);
                code.emitLabel(statlist_next);
                match(Tag.EOF);
                try {
                    code.toJasmin();
                }
                catch(java.io.IOException e) {
                    System.out.println("IO error\n");
                };
                break;
            
            default:
                error("\nTRANSLATOR: syntax error in PROG\nDetected: " + String.valueOf(look.tag) + "\nExpected: =, {, print, read, cond, while");
                break;
        }    
    }

    public void statlist(int statlist_next){
        switch(look.tag){
            case '=':
            case '{':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
                int stat_next = code.newLabel();
                stat(stat_next);
                int statlistp_next = statlist_next;
                code.emitLabel(stat_next);
                statlistp(statlistp_next);
                break;
            
            default:
                error("\nTRANSLATOR: syntax error in STATLIST\nDetected: " + String.valueOf(look.tag) + "\nExpected: =, {, print, read, cond, while");
                break;
        }
    }

    public void statlistp(int father_statilistp_next){
        switch(look.tag){
            case ';':
                match(';');
                int stat_next = code.newLabel();
                stat(stat_next);
                int statlistp_next = father_statilistp_next;
                code.emitLabel(stat_next);
                statlistp(statlistp_next);
                break;
            
            case Tag.EOF:
            case '}':
                code.emit(OpCode.GOto, father_statilistp_next);
                break;
        
            default:
                error("\nTRANSLATOR: syntax error in STATLISTP\nDetected: " + String.valueOf(look.tag) + "\nExpected: ;, EOF, }");
                break;
        }
    }

    public void stat(int father_stat_next) {
        int stat_next;
        switch(look.tag){
            case '=':
                match('=');
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }        
                    match(Tag.ID);            
                    expr();  
                    code.emit(OpCode.istore, id_addr);
                    code.emit(OpCode.GOto, father_stat_next); 
                }
                else
                    error("Error in grammar (stat) after read( with " + look);
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');       
                int stat_cont = exprlist();
                match(')');
                for(int i=0; i<stat_cont; i++)
                    code.emit(OpCode.invokestatic, 1);
                code.emit(OpCode.GOto, father_stat_next);   
                break;
            
            case Tag.READ:
                match(Tag.READ);
                match('(');
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    match(')');
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,id_addr);  
                    code.emit(OpCode.GOto, father_stat_next); 
                }
                else
                    error("Error in grammar (stat) after read( with " + look);
                break;

            case Tag.COND:
                match(Tag.COND);
                int whenlist_true = father_stat_next;
                int whenlist_false = code.newLabel();
                whenlist(whenlist_true, whenlist_false);
                match(Tag.ELSE);
                stat_next = father_stat_next;
                code.emitLabel(whenlist_false);
                stat(stat_next);
                break;

            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                int bexpr_true = code.newLabel();
                int bexpr_false = father_stat_next;
                stat_next = code.newLabel();
                code.emitLabel(stat_next);
                bexpr(bexpr_true, bexpr_false);
                match(')');
                code.emitLabel(bexpr_true);
                stat(stat_next);
                break;

            case '{':
                match('{');
                int statlist_next = father_stat_next;
                statlist(statlist_next);
                match('}');
                break;

            default:
                error("\nTRANSLATOR: syntax error in STAT\nDetected: " + String.valueOf(look.tag) + "\nExpected: =, {, print, read, cond, while");
                break;  
        }
    }

    public void whenlist(int whenlist_true, int whenlist_false){
        if(look.tag == Tag.WHEN){
            int whenitem_true = whenlist_true;
            int whenitem_false = code.newLabel();
            whenitem(whenitem_true, whenitem_false);
            int whenlistp_true = whenlist_true;
            int whenlistp_false = whenlist_false;
            code.emitLabel(whenitem_false);
            whenlistp(whenlistp_true, whenlistp_false);
        }else
            error("\nTRANSLATOR: syntax error in WHENLIST\nDetected: " + String.valueOf(look.tag) + "\nExpected: when");
    }

    public void whenlistp(int father_whenlistp_true, int father_whenlistp_false){
        switch(look.tag){
            case Tag.WHEN:
                int whenitem_true = father_whenlistp_true;
                int whenitem_false = code.newLabel();
                whenitem(whenitem_true, whenitem_false);
                int whenlistp_true = father_whenlistp_true;
                int whenlistp_false = father_whenlistp_false;
                code.emitLabel(whenitem_false);
                whenlistp(whenlistp_true, whenlistp_false);
                break;

            case Tag.ELSE:
                code.emit(OpCode.GOto, father_whenlistp_false);
                break;

            default:
                error("\nTRANSLATOR: syntax error in WHENLISTP\nDetected: " + String.valueOf(look.tag) + "\nExpected: when, else");
                break;
        }
    }

    public void whenitem(int whenitem_true, int whenitem_false){
        if(look.tag == Tag.WHEN){
            match(Tag.WHEN);
            match('(');
            int bexpr_true = code.newLabel();
            int bexpr_false = whenitem_false;
            bexpr(bexpr_true, bexpr_false);
            match(')');
            match(Tag.DO);
            int stat_next = whenitem_true;
            code.emitLabel(bexpr_true);
            stat(stat_next);
        }else
            error("\nTRANSLATOR: syntax error in WHENITEM\nDetected: " + String.valueOf(look.tag) + "\nExpected: when");
    }   

    public void bexpr(int bexpr_true, int bexpr_false){
        if(look.tag == Tag.RELOP){
            String relop_sign = ((Word)look).lexeme;
            match(Tag.RELOP);
            expr();
            expr();
            switch(relop_sign){
                case "==" : code.emit(OpCode.if_icmpeq, bexpr_true); break;
                case "<=" : code.emit(OpCode.if_icmple, bexpr_true); break;
                case "<" : code.emit(OpCode.if_icmplt, bexpr_true);break;
                case ">=" : code.emit(OpCode.if_icmpge, bexpr_true); break;
                case ">" : code.emit(OpCode.if_icmpgt, bexpr_true); break;
                case "<>" : code.emit(OpCode.if_icmpne, bexpr_true); break;
                default: error("\nTRANSLATOR: segno " + relop_sign + " non riconosciuto. Impossibile trovare l'op-code corrispondente.");
            }
            code.emit(OpCode.GOto, bexpr_false);
        }else
            error("\nTRANSLATOR: syntax error in BEXPR\nDetected: " + String.valueOf(look.tag) + "\nExpected: RELOP");
    }

    private void expr() {
        int expr_counter=0;
        switch(look.tag){
            case '+':
                match('+');
                match('(');
                expr_counter = exprlist() - 1;
                match(')');
                for(int i = 0; i< expr_counter; i++)
                    code.emit(OpCode.iadd);
                break;

            case '*':
                match('*');
                match('(');
                expr_counter = exprlist() - 1;
                match(')');
                for(int i = 0; i< expr_counter; i++)
                    code.emit(OpCode.imul);
                break;

            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;  
                
            case Tag.NUM:
                int number_value = ((NumberTok)look).lexeme;
                match(Tag.NUM);
                code.emit(OpCode.ldc, number_value);
                break;

            case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1){
                    id_addr = count;
                    st.insert(((Word)look).lexeme, count++);
                }                    
                match(Tag.ID);
                code.emit(OpCode.iload, id_addr);   
                
                break;

            default:
                error("\nTRANSLATOR: syntax error in EXPR\nDetected: " + String.valueOf(look.tag) + "\nExpected: +, *, -, /, NUM, ID");
                break;
        }
    }

    public int exprlist(){
        int ret_value = 0;
        switch(look.tag){
            case '+':
            case '*':
            case '-':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                ret_value = 1 + exprlistp(); 
                break;

            default:
                error("\nTRANSLATOR: syntax error in EXPRLIST\nDetected: " + String.valueOf(look.tag) + "\nExpected: +, *, -, /, NUM, ID");
                break;
        }
        return ret_value;
    }

    public int exprlistp(){
        int ret_value = 0;
        switch(look.tag){
            case '+':
            case '*':
            case '-':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                ret_value = 1 + exprlistp();
                break;
            
            case ')':
                ret_value = 0;
                break;

            default:
                error("\nTRANSLATOR: syntax error in EXPRLISTP\nDetected: " + String.valueOf(look.tag) + "\nExpected: +, *, -, /, NUM, ID, )");
                break;
        }
        return ret_value;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "program.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
/*compilare e eseguire Translator*/
/*chiamare jasmin sull'output.j prodotto dal translator: java -jar jasmin.jar Output.j*/
/*chiamare java Output sul file class prodotto da jasmin*/

