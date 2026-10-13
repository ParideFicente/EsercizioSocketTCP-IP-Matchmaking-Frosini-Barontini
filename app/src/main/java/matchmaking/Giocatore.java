/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package matchmaking;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Giocatore implements Runnable {

    String nickname;                         //nickname del giocatore
    int livello;                              //livello del giocatore
    String ip;                                //indirizzo IP del giocatore

    Socket s;                                 //connessione del giocatore
    PrintWriter out;                          //stream per inviare messaggi

    public Giocatore(Socket s) {
        this.s = s;
        this.ip = s.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {

        try {
            out = new PrintWriter(s.getOutputStream(), true);       //creazione degli stream
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            out.println("Benvenuto client!");                       //invia un messaggio al client

            nickname = in.readLine();                               //legge il nickname del giocatore
            livello = Integer.parseInt(in.readLine());              //legge il livello del giocatore

            System.out.println("Nickname: " + nickname);
            System.out.println("Livello: " + livello);
            System.out.println("IP: " + ip);

            Server.aggiungiGiocatore(this);                          //aggiunge il giocatore al server

            //la connessione rimane aperta fino al matchmaking

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());

            try {
                s.close();
            } catch (Exception ex) {
                System.out.println("Errore chiusura: " + ex.getMessage());
            }
        }
    }

    public void inviaMessaggio(String messaggio) {

        out.println(messaggio);                                      //invia il risultato al client

        try {
            s.close();                                              //chiude la connessione dopo il matchmaking
        } catch (Exception e) {
            System.out.println("Errore chiusura: " + e.getMessage());
        }
    }
}