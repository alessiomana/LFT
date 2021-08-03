import java.util.Scanner;

public class Esercizio1_4_1{
    /**
     COGNOMI NON COMPOSTI
    Modificare l’automa dell’esercizio precedente in modo che riconosca le combinazioni di
    matricola e cognome di studenti del turno 2 o del turno 3 del laboratorio, dove il numero di
    matricola e il cognome:
    - possono essere separati da una sequenza di spazi
    - possono essere precedute e/o seguite da sequenze eventualmente vuote di spazi.
    
    Per esempio, l’automa
    - deve accettare la stringa "654321 Rossi " e " 123456 Bianchi "" (dove, nel secondo esempio, ci sono spazi prima del primo carattere e dopo l’ultimo carattere)
    - non "1234 56Bianchi " e "123456Bia nchi ". 
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
                    else if(ch == ' ')  
                        state = 0;
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
                        state = 4;
                    else if(ch == ' ')
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
                        state = 4;
                    else if(ch == ' ')
                        state = 5;
                    else
                        state = -1;
                    break;
                case 3:
                    if(ch >= 'L' && ch <= 'Z')
                        state = 4;
                    else if(ch == ' ')
                        state = 3;
                    else
                        state = -1;
                    break;
                case 4:
                    if(ch >= 'a' && ch <= 'z')
                        state = 4;
                    else if(ch == ' ')
                        state = 6;
                    else
                        state = -1;
                    break;
                case 5:
                    if(ch >= 'A' && ch <= 'K')
                        state = 4;
                    else if(ch == ' ')
                        state = 5;
                    else
                        state = -1;
                    break;
                case 6:
                    if(ch == ' ')
                        state = 6;
                    //else if(ch >= 'A' && ch <= 'Z') state = 4; ==> cognomi composti
                    else
                        state = -1;
                    break;
            }
        }

        return (state == 4 || state == 6);
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