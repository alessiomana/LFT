public class Esercizio1_3{
    /**
    Progettare e implementare un DFA che riconosca il linguaggio di stringhe che
    contengono un numero di matricola seguito (subito) da un cognome, dove la combinazione 
    di matricola e cognome corrisponde a studenti del turno 2 o del
    turno 3 del laboratorio di LFT.
    Regole per suddivisione di studenti in turni:
    
    - Turno 1: cognomi la cui iniziale è compresa tra A e K, e il numero di matricola è
    dispari;
    - Turno 2: cognomi la cui iniziale è compresa tra A e K, e il numero di matricola è
    pari;
    - Turno 3: cognomi la cui iniziale è compresa tra L e Z, e il numero di matricola è
    dispari;
    - Turno 4: cognomi la cui iniziale è compresa tra L e Z, e il numero di matricola è
    pari.
    
    Esempio:
    123456Bianchi e 654321Rossi sono stringhe del linguaggio,
    654321Bianchi e “123456Rossi non sono stringhe del linguaggio.
    Nel contesto di questo esercizio, un numero di matricola non ha un numero
    prestabilito di cifre ma deve essere composto di almeno una ci
    */

    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0:
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 2;
                        else if((ch%2) != 0)
                            state = 1;
                    }
                    else
                        state = -1;
                    break;
                case 1: 
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 2;
                        else if((ch%2) != 0)
                            state = 1;
                    }
                    else if(ch >= 'L' && ch <= 'Z')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 2:
                    if(Character.isDigit(ch)){
                        if((ch%2) == 0)
                            state = 2;
                        else if((ch%2) != 0)
                            state = 1;
                    }
                    else if(ch >= 'A' && ch <= 'K')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 3:
                    if(ch >= 'a' && ch <= 'z')
                        state = 3;
                    else
                        state = -1;
                    break;
            }
        }

        return state == 3;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}