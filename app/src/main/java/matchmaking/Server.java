/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package matchmaking;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {

        ArrayList<Giocatore> giocatori = new ArrayList<>();       //lista dei giocatori connessi

        try {
            ServerSocket socketAccettazione = new ServerSocket(50000);          //creazione del server

            System.out.println("Server avviato");

            while (true) {                                      //mantiene il server sempre attivo

                System.out.println("In attesa di un client...");

                Socket s = socketAccettazione.accept();          //attende un client

                System.out.println("Client connesso: " + s.getInetAddress()); //stampa l'IP del client

                Giocatore giocatore = new Giocatore(s, giocatori); //crea un giocatore
                Thread t = new Thread(giocatore);                  //crea il thread
                t.start();                                        //avvia il thread
            }

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}