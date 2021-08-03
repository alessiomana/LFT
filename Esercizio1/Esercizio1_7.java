public class Esercizio1_7{
    /*
    Progettare e implementare un DFA con alfabeto {a, b} che riconosca il linguaggio delle stringhe tali che a occorre
    almeno una volta in una delle ultime tre posizioni della stringa.
    Come nell’esercizio 1.6, il DFA deve accettare anche stringhe che contengono meno di tre simboli (ma almeno uno dei
    simboli deve essere a).
    Ad esempio, il DFA
    Deve accettare le stringhe: abb, bbaba, baaaaaaa, aaaaaaa, a, ba, bba, aa e bbbababab
    NON deve accettare le stringhe: abbbbbb, bbabbbbbbbb oppure b
    */

    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0: 
                    if(ch == 'a') 
                        state = 1;
                    else if(ch == 'b')  
                        state = 0;
                    else
                        state = -1;
                    break;
                case 1: 
                    if(ch == 'a')
                        state = 1;
                    else if(ch == 'b')
                        state = 2;
                    else
                        state = -1;
                    break;
                case 2: 
                    if(ch == 'a')
                        state = 1;
                    else if(ch == 'b')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 3: 
                    if(ch == 'a')
                        state = 1;
                    else if(ch == 'b')
                        state = 0;
                    else
                        state = -1;
                    break;
            }
        }

        return state != 0;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}