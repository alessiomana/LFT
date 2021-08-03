public class Esercizio1_5{
    /*
    Esercizio 1.5. 
    Progettare e implementare un DFA che, come in
    Esercizio 1.3, riconosca il linguaggio di stringhe che contengono
    matricola e cognome di studenti del turno 2 o del turno 3 del
    laboratorio, ma in cui il cognome precede il numero di matricola.

    Esempio:
    Bianchi123456 e Rossi654321 sono stringhe del linguaggio,
    Bianchi654321 e Rossi123456 NON sono stringhe del linguaggio.
    */

    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0: 
                    if(ch >= 'A' && ch <= 'K') 
                        state = 1;
                    else if(ch >= 'L' && ch <= 'Z')  
                        state = 2;
                    else
                        state = -1;
                    break;
                case 1: 
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 3;
                        else if((ch%2) != 0)
                            state = 5;
                    }
                    else if(ch >= 'a' && ch <= 'z')
                        state = 1;
                    else
                        state = -1;
                    break;
                case 2: 
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 6;
                        else if((ch%2) != 0)
                            state = 4;
                    }
                    else if(ch >= 'a' && ch <= 'z')
                        state = 2;
                    else
                        state = -1;
                    break;
                case 3: 
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 3;
                        else if((ch%2) != 0)
                            state = 5;
                    }
                    else
                        state = -1;
                    break;
                case 4: 
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 6;
                        else if((ch%2) != 0)
                            state = 4;
                    }
                    else
                        state = -1;
                    break;
                case 5: 
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 3;
                        else if((ch%2) != 0)
                            state = 5;
                    }
                    else
                        state = -1;
                    break;
                case 6: 
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 6;
                        else if((ch%2) != 0)
                            state = 4;
                    }
                    else
                        state = -1;
                    break;
            }
        }

        return (state == 3 || state == 4);
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}