/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package matchmaking;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Giocatore implements Runnable {

    String nickname;                         //nickname del giocatore
    int livello;                              //livello del giocatore
    String ip;                                //indirizzo IP del giocatore

    Socket s;                                 //connessione del giocatore
    PrintWriter out;                          //stream per inviare messaggi

    boolean partitaTrovata = false;           //indica se il giocatore ha trovato una partita
    boolean confermato = false;               //indica se il giocatore ha risposto
    ArrayList<Giocatore> gruppoVerifica;      //gruppo in fase di verifica

    int sessione;                             //avvio del server a cui appartiene il giocatore

    public Giocatore(Socket s, int sessione) {

        this.s = s;
        this.sessione = sessione;
        this.ip = s.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {

        try {
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);       //creazione degli stream
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            this.out = out;

            out.println("Benvenuto client!");                    //invia un messaggio al client

            nickname = in.readLine();                            //legge il nickname del giocatore

            if (nickname == null) {
                return;
            }

            String datoLivello = in.readLine();

            if (datoLivello == null) {
                return;
            }

            //controlla che i dati del giocatore siano validi
            if (nickname.trim().isEmpty() || livello < 0) {
            out.println("Dati non validi");
            return;
            }

            Server.messaggio("Nickname: " + nickname);
            Server.messaggio("Livello: " + livello);
            Server.messaggio("IP: " + ip);

            if (!Server.aggiungiGiocatore(this)) {               //aggiunge il giocatore alla lista
                return;
            }

            String messaggio;

            while ((messaggio = in.readLine()) != null) {       //attende i messaggi del client

                if (messaggio.equals("OK")) {

                    Server.confermaGiocatore(this);              //conferma che il client e collegato
                }
            }

        } catch (Exception e) {

            if (Server.deveMostrareErrore(this)) {
                Server.messaggio("Errore client: " + e.getMessage());
            }

        } finally {

            Server.rimuoviGiocatore(this);                       //rimuove il giocatore se si disconnette

            try {
                s.close();                                      //chiusura della connessione
            } catch (Exception e) {
                Server.messaggio("Errore chiusura: " + e.getMessage());
            }
        }
    }

    public void inviaMessaggio(String messaggio) {

        out.println(messaggio);                                  //invia il risultato al client

        try {
            s.close();                                          //chiude la connessione dopo il matchmaking
        } catch (Exception e) {
            Server.messaggio("Errore chiusura: " + e.getMessage());
        }
    }
}