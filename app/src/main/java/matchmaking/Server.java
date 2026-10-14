package matchmaking;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    static ArrayList<Giocatore> giocatori = new ArrayList<>();    //lista dei giocatori in attesa
    static int ngg;                                              //numero di giocatori per gruppo
    static int dml;                                              //differenza massima di livello
    static ServerSocket socketAccettazione;
    static ServerGUI finestra;
    static boolean attivo = false;

    public static void main(String[] args) {
        Scanner tastiera = new Scanner(System.in);
        System.out.print("Inserisci numero giocatori per gruppo: ");
        int numero = tastiera.nextInt();
        System.out.print("Inserisci differenza massima di livello: ");
        int differenza = tastiera.nextInt();
        avviaConParametri(numero, differenza, null);             //avvio da terminale
    }

    public static void avviaConParametri(int numero, int differenza, ServerGUI gui) {
        if (numero < 2 || differenza < 0) {
            System.out.println("Parametri non validi!");
            if (gui != null) gui.aggiornaStato(false);
            return;
        }

        synchronized (Server.class) {
            if (attivo) {
                if (gui != null) gui.aggiungiEvento("Server gia avviato");
                return;
            }
            ngg = numero;
            dml = differenza;
            finestra = gui;
            try {
                socketAccettazione = new ServerSocket(50000);   //creazione del server
                attivo = true;
            } catch (Exception e) {
                messaggio("Errore avvio: " + e.getMessage());
                if (gui != null) gui.aggiornaStato(false);
                return;
            }
        }

        messaggio("Server avviato sulla porta 50000");
        messaggio("NGG: " + ngg + " - DML: " + dml);
        if (gui != null) gui.aggiornaStato(true);

        try {
            while (true) {                                    //mantiene il server in ascolto
                Socket s = socketAccettazione.accept();         //attende un client
                synchronized (Server.class) {
                    if (!attivo) {
                        s.close();
                        break;
                    }
                }
                messaggio("Client connesso: " + s.getInetAddress());
                Giocatore giocatore = new Giocatore(s);         //crea un giocatore
                Thread t = new Thread(giocatore);               //crea il thread
                t.start();                                      //avvia il thread
            }
        } catch (Exception e) {
            if (attivo) messaggio("Errore: " + e.getMessage());
        } finally {
            ferma();
        }
    }

    public static void ferma() {
        synchronized (Server.class) {
            if (!attivo) return;
            attivo = false;
            try {
                socketAccettazione.close();                     //ferma l'ascolto
            } catch (Exception e) {
                messaggio("Errore chiusura server: " + e.getMessage());
            }
            for (int i = 0; i < giocatori.size(); i++) {
                try {
                    giocatori.get(i).s.close();                  //chiude i client in attesa
                } catch (Exception e) {
                    messaggio("Errore chiusura client: " + e.getMessage());
                }
            }
            giocatori.clear();                                  //svuota la lista d'attesa
            messaggio("Server arrestato");
            if (finestra != null) finestra.aggiornaStato(false);
        }
    }

    public static void messaggio(String testo) {
        System.out.println(testo);
        ServerGUI gui = finestra;
        if (gui != null) gui.aggiungiEvento(testo);             //mostra gli eventi nella GUI
    }

    public static synchronized void aggiungiGiocatore(Giocatore g) {
        if (!attivo || g.s.isClosed()) return;
        giocatori.add(g);                                       //aggiunge il giocatore alla lista
        messaggio("Giocatore aggiunto: " + g.nickname + " (livello " + g.livello + ")");
        messaggio("Giocatori in attesa: " + giocatori.size());
        controllaPartite();                                     //controlla se si può creare una partita
    }

    public static synchronized void rimuoviGiocatore(Giocatore g) {
        if (giocatori.remove(g)) {                               //rimuove il giocatore disconnesso
            messaggio("Giocatore disconnesso: " + g.nickname);
            messaggio("Giocatori in attesa: " + giocatori.size());
        }
    }

    public static synchronized void controllaPartite() {
        boolean partitaCreata = true;
        while (partitaCreata) {                                  //controlla se si possono creare altre partite
            partitaCreata = false;
            for (int i = 0; i < giocatori.size(); i++) {
                ArrayList<Giocatore> gruppo = new ArrayList<>(); //giocatori della partita
                Giocatore primo = giocatori.get(i);
                gruppo.add(primo);
                for (int j = 0; j < giocatori.size(); j++) {
                    Giocatore altro = giocatori.get(j);
                    if (altro != primo && altro.livello >= primo.livello &&
                        (long) altro.livello - primo.livello <= dml) {
                        gruppo.add(altro);                       //aggiunge un giocatore compatibile
                    }
                    if (gruppo.size() == ngg) break;
                }
                if (gruppo.size() == ngg) {
                    messaggio("Partita trovata!");
                    for (int k = 0; k < gruppo.size(); k++) {
                        Giocatore g = gruppo.get(k);
                        String testo = "Partita trovata! Compagni: ";
                        for (int h = 0; h < gruppo.size(); h++) {
                            Giocatore compagno = gruppo.get(h);
                            if (compagno != g) {
                                testo += compagno.nickname + " - " + compagno.ip
                                       + " - Livello " + compagno.livello + "; ";
                            }
                        }
                        g.inviaMessaggio(testo);                 //invia i compagni al client
                    }
                    for (int k = 0; k < gruppo.size(); k++) {
                        giocatori.remove(gruppo.get(k));         //rimuove i giocatori abbinati
                    }
                    messaggio("Giocatori rimasti in attesa: " + giocatori.size());
                    partitaCreata = true;
                    break;
                }
            }
        }
    }
}
