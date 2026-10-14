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
    static ArrayList<Giocatore> connessioni = new ArrayList<>();  //lista dei client collegati

    static int ngg;                                              //numero di giocatori per gruppo
    static int dml;                                              //differenza massima di livello
    static int sessione = 0;                                     //numero dell'avvio del server

    static ServerSocket socketAccettazione;
    static ServerGUI finestra;
    static boolean attivo = false;

    public static void main(String[] args) {

        Scanner tastiera = new Scanner(System.in);

        System.out.print("Inserisci numero giocatori per gruppo: ");
        int numero = tastiera.nextInt();

        System.out.print("Inserisci differenza massima di livello: ");
        int differenza = tastiera.nextInt();

        avviaConParametri(numero, differenza, null);             //avvia il server dal terminale
    }

    public static void avviaConParametri(int numero, int differenza, ServerGUI gui) {

        if (numero < 2 || differenza < 0) {
            messaggio("Parametri non validi!");

            if (gui != null) {
                gui.aggiornaStato(false);
            }
            return;
        }

        ServerSocket accettazione;
        int avvio;

        synchronized (Server.class) {

            if (attivo) {
                if (gui != null) {
                    gui.aggiungiEvento("Server gia avviato");
                }
                return;
            }

            ngg = numero;
            dml = differenza;
            finestra = gui;

            try {
                accettazione = new ServerSocket(50000);         //creazione del server
                socketAccettazione = accettazione;

                attivo = true;
                sessione++;
                avvio = sessione;

            } catch (Exception e) {
                messaggio("Errore avvio: " + e.getMessage());

                if (gui != null) {
                    gui.aggiornaStato(false);
                }
                return;
            }
        }

        messaggio("Server avviato sulla porta 50000");
        messaggio("NGG: " + ngg + " - DML: " + dml);

        if (gui != null) {
            gui.aggiornaStato(true);
        }

        try {
            while (true) {                                      //mantiene il server sempre attivo

                Socket s = accettazione.accept();               //attende un client

                synchronized (Server.class) {

                    if (!attivo || sessione != avvio) {
                        s.close();
                        break;
                    }

                    Giocatore giocatore = new Giocatore(s, avvio); //crea un giocatore

                    connessioni.add(giocatore);                 //aggiunge il client alla lista

                    messaggio("Client connesso: " + s.getInetAddress());

                    Thread t = new Thread(giocatore);           //crea il thread
                    t.start();                                  //avvia il thread
                }
            }

        } catch (Exception e) {

            synchronized (Server.class) {
                if (attivo && sessione == avvio) {
                    messaggio("Errore: " + e.getMessage());
                }
            }

        } finally {

            synchronized (Server.class) {
                if (attivo && sessione == avvio) {
                    ferma();
                }
            }
        }
    }

    public static synchronized void ferma() {

        if (!attivo) {
            return;
        }

        attivo = false;

        try {
            socketAccettazione.close();                          //chiude la socket del server
        } catch (Exception e) {
            messaggio("Errore chiusura server: " + e.getMessage());
        }

        for (int i = 0; i < connessioni.size(); i++) {

            try {
                connessioni.get(i).s.close();                    //chiude i client collegati
            } catch (Exception e) {
                messaggio("Errore chiusura client: " + e.getMessage());
            }
        }

        connessioni.clear();
        giocatori.clear();                                       //svuota la lista dei giocatori

        messaggio("Server arrestato");

        if (finestra != null) {
            finestra.aggiornaStato(false);
        }
    }

    public static void messaggio(String testo) {

        System.out.println(testo);

        ServerGUI gui = finestra;

        if (gui != null) {
            gui.aggiungiEvento(testo);                           //stampa il messaggio nella GUI
        }
    }

    public static synchronized boolean deveMostrareErrore(Giocatore g) {

        return attivo && g.sessione == sessione
                && !g.partitaTrovata && !g.s.isClosed();
    }

    public static synchronized boolean aggiungiGiocatore(Giocatore g) {

        if (!attivo || g.sessione != sessione || g.s.isClosed()) {
            return false;
        }

        giocatori.add(g);                                        //aggiunge il giocatore alla lista

        messaggio("Giocatore aggiunto: " + g.nickname
                + " (livello " + g.livello + ")");

        messaggio("Giocatori in attesa: " + giocatori.size());

        controllaPartite();                                      //controlla se si puo creare una partita

        return true;
    }

    public static synchronized void rimuoviGiocatore(Giocatore g) {

        connessioni.remove(g);

        if (giocatori.remove(g)) {                                //rimuove il giocatore se presente

            messaggio("Giocatore disconnesso: " + g.nickname);
            messaggio("Giocatori in attesa: " + giocatori.size());

            if (g.gruppoVerifica != null) {
                annullaVerifica(g.gruppoVerifica);                //annulla il gruppo incompleto
            }

            if (attivo && g.sessione == sessione) {
                controllaPartite();
            }
        }
    }

    public static synchronized void controllaPartite() {

        if (!attivo) {
            return;
        }

        for (int i = 0; i < giocatori.size(); i++) {

            Giocatore primo = giocatori.get(i);

            if (primo.gruppoVerifica != null) {
                continue;
            }

            ArrayList<Giocatore> gruppo = new ArrayList<>();      //lista dei giocatori della partita

            gruppo.add(primo);

            for (int j = 0; j < giocatori.size(); j++) {

                Giocatore altro = giocatori.get(j);

                if (altro != primo &&
                        altro.gruppoVerifica == null &&
                        altro.livello >= primo.livello &&
                        (long) altro.livello - primo.livello <= dml) {

                    gruppo.add(altro);                           //aggiunge un giocatore compatibile
                }

                if (gruppo.size() == ngg) {
                    break;
                }
            }

            if (gruppo.size() == ngg) {

                for (int k = 0; k < gruppo.size(); k++) {

                    Giocatore g = gruppo.get(k);

                    g.gruppoVerifica = gruppo;
                    g.confermato = false;
                }

                messaggio("Verifica dei giocatori prima della partita...");

                for (int k = 0; k < gruppo.size(); k++) {

                    gruppo.get(k).out.println("VERIFICA");        //chiede una conferma ai client
                }

                avviaTimeout(gruppo);                            //avvia il tempo di attesa
            }
        }
    }

    private static void avviaTimeout(final ArrayList<Giocatore> gruppo) {

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Thread.sleep(4000);                           //attende 4 secondi
                } catch (InterruptedException e) {
                    return;
                }

                Server.controllaTimeout(gruppo);
            }
        });

        t.start();
    }

    public static synchronized void confermaGiocatore(Giocatore g) {

        ArrayList<Giocatore> gruppo = g.gruppoVerifica;

        if (!attivo || g.sessione != sessione || gruppo == null) {
            return;
        }

        g.confermato = true;                                      //il giocatore ha risposto

        for (int i = 0; i < gruppo.size(); i++) {

            if (!gruppo.get(i).confermato) {
                return;
            }
        }

        messaggio("Partita trovata!");

        for (int i = 0; i < gruppo.size(); i++) {

            Giocatore giocatore = gruppo.get(i);

            giocatore.partitaTrovata = true;

            giocatori.remove(giocatore);                          //rimuove dalla lista d'attesa
            giocatore.gruppoVerifica = null;
        }

        //invia la lista dei compagni ai giocatori
        for (int i = 0; i < gruppo.size(); i++) {

            Giocatore g1 = gruppo.get(i);

            String testo = "Partita trovata! Compagni: ";

            for (int j = 0; j < gruppo.size(); j++) {

                Giocatore g2 = gruppo.get(j);

                if (g2 != g1) {

                    testo += g2.nickname + " - "
                            + g2.ip + " - Livello "
                            + g2.livello + "; ";
                }
            }

            g1.inviaMessaggio(testo);                             //invia il gruppo al client
        }

        messaggio("Giocatori rimasti in attesa: " + giocatori.size());

        controllaPartite();                                       //controlla se ci sono altri gruppi
    }

    private static void annullaVerifica(ArrayList<Giocatore> gruppo) {

        for (int i = 0; i < gruppo.size(); i++) {

            gruppo.get(i).gruppoVerifica = null;
            gruppo.get(i).confermato = false;
        }
    }

    public static synchronized void controllaTimeout(ArrayList<Giocatore> gruppo) {

        if (!attivo ||
                gruppo.get(0).sessione != sessione ||
                gruppo.get(0).gruppoVerifica != gruppo) {

            return;
        }

        messaggio("Verifica scaduta: un giocatore non risponde");

        for (int i = 0; i < gruppo.size(); i++) {

            Giocatore g = gruppo.get(i);

            if (!g.confermato) {

                giocatori.remove(g);                             //rimuove i giocatori che non rispondono

                try {
                    g.s.close();
                } catch (Exception e) {
                    messaggio("Errore chiusura: " + e.getMessage());
                }
            }
        }

        annullaVerifica(gruppo);                                  //annulla il gruppo

        messaggio("Giocatori in attesa: " + giocatori.size());

        controllaPartite();
    }
}