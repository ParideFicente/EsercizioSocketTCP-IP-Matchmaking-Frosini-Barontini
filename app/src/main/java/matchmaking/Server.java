/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package matchmaking;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    static ArrayList<Giocatore> giocatori = new ArrayList<>();    //lista dei giocatori in attesa

    static int ngg;                                              //numero di giocatori per gruppo
    static int dml;                                              //differenza massima di livello

    public static void main(String[] args) {

        Scanner tastiera = new Scanner(System.in);

        System.out.print("Inserisci numero giocatori per gruppo: ");
        ngg = tastiera.nextInt();                                 //numero di giocatori per partita

        System.out.print("Inserisci differenza massima di livello: ");
        dml = tastiera.nextInt();                                 //differenza massima tra i livelli

        if (ngg < 2 || dml < 0) {
            System.out.println("Parametri non validi!");
            return;
        }

        try {
            ServerSocket socketAccettazione = new ServerSocket(50000);          //creazione del server

            System.out.println("Server avviato");

            while (true) {                                       //mantiene il server sempre attivo

                System.out.println("In attesa di un client...");

                Socket s = socketAccettazione.accept();           //attende un client

                System.out.println("Client connesso: " + s.getInetAddress());

                Giocatore giocatore = new Giocatore(s);           //crea un giocatore

                Thread t = new Thread(giocatore);                  //crea il thread
                t.start();                                        //avvia il thread
            }

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public static synchronized void aggiungiGiocatore(Giocatore g) {

        giocatori.add(g);                                        //aggiunge il giocatore alla lista

        System.out.println("Giocatore aggiunto: " + g.nickname);
        System.out.println("Giocatori in attesa: " + giocatori.size());

        controllaPartite();                                      //controlla se si può creare una partita
    }

    public static synchronized void rimuoviGiocatore(Giocatore g) {

        if (giocatori.remove(g)) {                               //rimuove il giocatore se presente

            System.out.println("Giocatore disconnesso: " + g.nickname);
            System.out.println("Giocatori in attesa: " + giocatori.size());
        }
    }

    public static synchronized void controllaPartite() {

        boolean partitaCreata = true;

        while (partitaCreata) {                                  //controlla se si possono creare altre partite

            partitaCreata = false;

            for (int i = 0; i < giocatori.size(); i++) {

                ArrayList<Giocatore> gruppo = new ArrayList<>();  //lista dei giocatori della partita

                Giocatore primo = giocatori.get(i);
                gruppo.add(primo);

                for (int j = 0; j < giocatori.size(); j++) {

                    Giocatore altro = giocatori.get(j);

                    if (altro != primo &&
                        altro.livello >= primo.livello &&
                        (long) altro.livello - primo.livello <= dml) {

                        gruppo.add(altro);                       //aggiunge un giocatore compatibile
                    }

                    if (gruppo.size() == ngg) {
                        break;
                    }
                }

                if (gruppo.size() == ngg) {

                    System.out.println("Partita trovata!");

                    //invia la lista dei compagni ai giocatori
                    for (int k = 0; k < gruppo.size(); k++) {

                        Giocatore g = gruppo.get(k);

                        String messaggio = "Partita trovata! Compagni: ";

                        for (int h = 0; h < gruppo.size(); h++) {

                            Giocatore compagno = gruppo.get(h);

                            if (compagno != g) {
                                messaggio += compagno.nickname + " - "
                                          + compagno.ip + " - Livello "
                                          + compagno.livello + "; ";
                            }
                        }

                        g.inviaMessaggio(messaggio);
                    }

                    //rimuove i giocatori che hanno trovato una partita
                    for (int k = 0; k < gruppo.size(); k++) {
                        giocatori.remove(gruppo.get(k));
                    }

                    System.out.println("Giocatori rimasti in attesa: " + giocatori.size());

                    partitaCreata = true;
                    break;
                }
            }
        }
    }
}