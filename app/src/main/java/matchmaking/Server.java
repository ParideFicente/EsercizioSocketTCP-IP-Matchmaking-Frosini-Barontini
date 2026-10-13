/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package matchmaking;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Server {

    public static void main(String[] args) {

        try {
            ServerSocket socketAccettazione = new ServerSocket(50000);          //creazione del server

            System.out.println("Server avviato");
            System.out.println("In attesa di un client...");

            Socket s = socketAccettazione.accept();                 //attende un client

            System.out.println("Client connesso: " + s.getInetAddress());       //stampa l'indirizzo IP del client che si è collegato

            PrintWriter out = new PrintWriter(s.getOutputStream(), true);       //creazione degli stream per comunicare con il client
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            out.println("Benvenuto client!");                    //invia un messaggio al client

            String nickname = in.readLine();                     //legge il nickname inviato dal client
            int livello = Integer.parseInt(in.readLine());       //legge il livello e lo converte in numero

            System.out.println("Nickname: " + nickname);
            System.out.println("Livello: " + livello);

            s.close();                                           //chiusura della connessione
            socketAccettazione.close();

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}