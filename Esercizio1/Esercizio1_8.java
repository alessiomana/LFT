public class Esercizio1_8{
    /*
    Progettare e implementare un DFA che riconosca il linguaggio di stringhe che contengono il tuo nome e tutte le
    stringhe ottenute dopo la sostituzione di un carattere del nome con un altro qualsiasi.
    Ad esempio, nel caso di uno studente che si chiama Paolo, il DFA
    
    Stringa: JACK
    
    Deve accettare la stringa Paolo (cioè il nome scritto correttamente), ma anche le stringhe Pjolo, caolo,
        Pa%lo, Paola e Parlo (il nome dopo la sostituzione di un carattere)
    NON deve accettare:
        Eva, Peola, Pietro oppure P*o*
    */

    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0: 
                    if(ch == 'J') 
                        state = 1;
                    else
                        state = 2;
                    break;
                case 1: 
                    if(ch == 'a')
                        state = 3;
                    else
                        state = 4;
                    break;
                case 2: 
                    if(ch == 'a')
                        state = 4;
                    else
                        state = -1;
                    break;
                case 3: 
                    if(ch == 'c')
                        state = 5;
                    else
                        state = 6;
                    break;
                case 4: 
                    if(ch == 'c')
                        state = 6;
                    else
                        state = -1;
                    break;
                case 5: 
                    state = 7;
                    break;
                case 6: 
                    if(ch == 'k')
                        state = 7;
                    else
                        state = -1;
                    break;
                case 7: 
                    state = -1;
                    break;
            }
        }

        return state == 7;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}