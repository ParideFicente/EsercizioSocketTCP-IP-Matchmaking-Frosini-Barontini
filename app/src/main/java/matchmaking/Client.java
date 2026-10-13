/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package matchmaking;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Client {

    public static void main(String[] args) {

        try {
            System.out.println("Connessione al server...");

            Socket s = new Socket("127.0.0.1", 50000);        //connessione al server tramite IP e porta

            System.out.println("Connesso al server!");

            //creazione degli stream per comunicare con il server
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String messaggio = in.readLine();                //legge il messaggio inviato dal server

            System.out.println("Il server dice: " + messaggio);

            out.println("Ciao server!");                     //invia un messaggio al server

            s.close();                                       //chiusura della connessione

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());  //stampa un eventuale errore
        }
    }
}