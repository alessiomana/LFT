import java.util.Scanner;

public class Esercizio1_9{
    // Progettare e implementare un DFA definito sull’alfabeto
    // {/, *, a} che riconosca il linguaggio di commenti delimitati da /* (all’inizio) e (alla fine) */
    // L’automa deve accettare stringhe sull’alfabeto che contengono almeno 4 caratteri che iniziano con /*, che finiscono con */
    // e che contengono una sola occorrenza della stringa */, quella
    // finale (dove l’asterisco della stringa */ non deve essere in comune
    // con quello della stringa /* all’inizio, )
    // Esempio: l’automa deve accettare le stringhe:
    //      - /****/, /*a*a*/, /*a/**/, /**a///a/a**/, /**/ e /*/*/
    // NON deve accettare le stringhe:
    //      - /*/, oppure /**/***/.

    public static boolean scan(String s){
        int state = 0;
        int i = 0;

        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);

            switch(state){
                case 0: 
                    if(ch == '/') 
                        state = 1;
                    else
                        state = -1;
                    break;
                case 1: 
                    if(ch == '*')
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
                    if(ch == '/')
                        state = 4;
                    else if(ch == '*')
                        state = 3;
                    else if(ch == 'a')
                        state = 2;
                    else
                        state = -1;
                    break;
                case 4: 
                    state = -1;
                    break;
            }
        }

        return state == 4;
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