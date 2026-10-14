/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package matchmaking;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try {
            Scanner tastiera = new Scanner(System.in);        //lettura dei dati dalla tastiera

            System.out.print("Inserisci IP del server: ");
            String ipServer = tastiera.nextLine().trim();

            System.out.print("Inserisci porta del server: ");
            int porta = Integer.parseInt(tastiera.nextLine().trim());

            if (ipServer.isEmpty() || porta < 1 || porta > 65535) {
                System.out.println("IP o porta non validi");
                return;
            }

            System.out.println("Connessione al server...");

            Socket s = new Socket(ipServer, porta);            //connessione al server tramite IP e porta

            System.out.println("Connesso al server!");

            PrintWriter out = new PrintWriter(s.getOutputStream(), true);       //creazione degli stream per comunicare con il server
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String messaggio = in.readLine();                 //legge il messaggio inviato dal server

            System.out.println("Il server dice: " + messaggio);

            System.out.print("Inserisci nickname: ");
            String nickname = tastiera.nextLine();            //nickname del giocatore

            System.out.print("Inserisci livello: ");
            int livello = tastiera.nextInt();                 //livello del giocatore

            out.println(nickname);                           //invia il nickname al server
            out.println(livello);                             //invia il livello al server

            System.out.println("In attesa di altri giocatori...");

            String risposta;

            while ((risposta = in.readLine()) != null) {      //attende il risultato del matchmaking

                if (risposta.equals("VERIFICA")) {

                    out.println("OK");                        //conferma di essere ancora collegato

                } else {

                    System.out.println("Il server dice: " + risposta);
                    break;
                }
            }

            s.close();                                       //chiusura della connessione

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());  //stampa un eventuale errore
        }
    }
}