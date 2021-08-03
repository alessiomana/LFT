import java.util.Scanner;

public class Esercizio1_10{
    // Modificare l’automa dell’esercizio precedente in modo che riconosca il
    // linguaggio di stringhe (sull’alfabeto {/, *, a} ) che contengono commenti
    // ma con la possibilità di avere stringhe prima e dopo come specificato qui di seguito.
    // L’idea è che sia possibile avere eventualmente commenti (anche multipli) in una
    // sequenza di simboli dell’alfabeto. Quindi l’unico vincolo è che l’automa deve
    // accettare le stringhe in cui un’occorrenza della stringa /* deve essere seguita
    // (anche non immediatamente) da un’occorrenza della stringa */
    // Le stringhe del linguaggio possono non avere nessuna occorrenza della stringa /*
    // (caso della sequenza di simboli senza commenti).
    // Ad esempio, il DFA deve accettare le stringhe:
    //      aaa/****/aa, aa/*a*a*/,
    //      aaaa, ///
    //      /****/, /*aa*/, */a, a/**/***a, a/**/***/a e a/**/aa/***/a
    // ma NON deve accettare
    //      aaa/*/aa oppure aa/*aa. 

    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0: 
                    if(ch == '/') 
                        state = 1;
                    else if(ch == 'a' | ch == '*')
                        state = 0;
                    else
                        state = -1;
                    break;
                case 1: 
                    if(ch == '/')
                        state = 1;
                    else if(ch == 'a')
                        state = 0;
                    else if(ch == '*')
                        state = 2;
                    else
                        state = -1;
                    break;
                case 2: 
                    if(ch == '/' || ch == 'a')
                        state = 2;
                    else if(ch == '*')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 3: 
                    if(ch == '*')
                        state = 3;
                    else if(ch == '/')
                        state = 0;
                    else if(ch == 'a')
                        state = 2;
                    else
                        state = -1;
                    break;
            }
        }

        return (state == 0 || state == 1);
    }

    public static void main(String[] args) {
        String myInput;
        Scanner sc = new Scanner(System.in);
        System.out.print("Inserisci la stringa: ");
        myInput = sc.nextLine();
        System.out.println(scan(myInput) ? "OK" : "NOPE");
        sc.close();
    }
}