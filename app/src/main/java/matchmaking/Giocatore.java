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
    ArrayList<Giocatore> giocatori;           //lista dei giocatori

    public Giocatore(Socket s, ArrayList<Giocatore> giocatori) {
        this.s = s;
        this.giocatori = giocatori;
        this.ip = s.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {

        try {
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);  //creazione degli stream
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            out.println("Benvenuto client!");        //invia un messaggio al client

            nickname = in.readLine();                //legge il nickname del giocatore
            livello = Integer.parseInt(in.readLine()); //legge il livello del giocatore

            synchronized (giocatori) {
                giocatori.add(this);                 //aggiunge il giocatore alla lista
                System.out.println("Nickname: " + nickname);
                System.out.println("Livello: " + livello);
                System.out.println("IP: " + ip);
                System.out.println("Giocatori registrati: " + giocatori.size());
            }

            s.close();                               //chiusura della connessione

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}