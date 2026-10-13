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
    boolean partitaTrovata = false;           //indica se il giocatore ha trovato una partita

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

            if (nickname == null) {
                return;
            }

            String datoLivello = in.readLine();

            if (datoLivello == null) {
                return;
            }

            livello = Integer.parseInt(datoLivello);                //legge il livello del giocatore

            System.out.println("Nickname: " + nickname);
            System.out.println("Livello: " + livello);
            System.out.println("IP: " + ip);

            Server.aggiungiGiocatore(this);                          //aggiunge il giocatore al server

            while (in.readLine() != null) {                          //attende finche il client rimane connesso
            }

        } catch (Exception e) {

            if (!partitaTrovata) {
                System.out.println("Errore: " + e.getMessage());
            }

        } finally {

            if (!partitaTrovata) {
                Server.rimuoviGiocatore(this);                       //rimuove il giocatore disconnesso
            }

            try {
                s.close();                                          //chiusura della connessione
            } catch (Exception e) {
                System.out.println("Errore chiusura: " + e.getMessage());
            }
        }
    }

    public void inviaMessaggio(String messaggio) {

        partitaTrovata = true;                                       //indica che la partita e stata trovata

        out.println(messaggio);                                      //invia il risultato al client

        try {
            s.close();                                              //chiude la connessione dopo il matchmaking
        } catch (Exception e) {
            System.out.println("Errore chiusura: " + e.getMessage());
        }
    }
}