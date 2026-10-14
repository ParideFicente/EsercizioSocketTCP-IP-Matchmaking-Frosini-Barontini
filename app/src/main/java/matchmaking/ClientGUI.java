package matchmaking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class ClientGUI extends javax.swing.JFrame {

    public ClientGUI() {
        initComponents();
        setLocationRelativeTo(null);                          //posiziona la finestra al centro
    }

    //avvia la ricerca senza bloccare la finestra
    private void cercaPartita() {
        final String nickname = txtNickname.getText().trim();
        final String ipServer = txtIP.getText().trim();
        final int livello;

        if (nickname.isEmpty() || ipServer.isEmpty()) {
            lblStato.setText("Stato: Inserisci nickname e IP del server");
            return;
        }

        try {
            livello = Integer.parseInt(txtLivello.getText().trim());
            if (livello < 0) {
                lblStato.setText("Stato: Il livello non puo essere negativo");
                return;
            }
        } catch (NumberFormatException e) {
            lblStato.setText("Stato: Inserisci un livello numerico");
            return;
        }

        btnCerca.setEnabled(false);
        txtNickname.setEnabled(false);
        txtLivello.setEnabled(false);
        txtIP.setEnabled(false);
        lblStato.setText("Stato: Connessione in corso...");
        txtRisultato.setText("");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket s = null;
                try {
                    s = new Socket(ipServer, 50000);                 //connessione al server

                    PrintWriter out = new PrintWriter(s.getOutputStream(), true); //stream per inviare i dati
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                    String benvenuto = in.readLine();                //legge il messaggio del server
                    if (benvenuto == null) {
                        aggiornaFinestra("Stato: Server disconnesso", "Connessione chiusa dal server.", true);
                        return;
                    }

                    out.println(nickname);                           //invia il nickname
                    out.println(livello);                             //invia il livello

                    aggiornaFinestra("Stato: In attesa di una partita...", benvenuto, false);

                    String risposta = in.readLine();                 //attende il risultato del matchmaking
                    if (risposta != null) {
                        aggiornaFinestra("Stato: Ricerca terminata", risposta, true);
                    } else {
                        aggiornaFinestra("Stato: Server disconnesso", "Connessione terminata senza partita.", true);
                    }

                } catch (Exception e) {
                    aggiornaFinestra("Stato: Errore di connessione", e.getMessage(), true);
                } finally {
                    if (s != null) {
                        try {
                            s.close();                               //chiusura della connessione
                        } catch (Exception e) {
                            //la connessione e gia terminata
                        }
                    }
                }
            }
        });
        t.start();                                                    //avvia il thread
    }

    //aggiorna i componenti grafici dal thread della finestra
    private void aggiornaFinestra(final String stato, final String risultato, final boolean finito) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblStato.setText(stato);
                txtRisultato.setText(risultato == null ? "" : risultato);
                if (finito) {
                    btnCerca.setEnabled(true);
                    txtNickname.setEnabled(true);
                    txtLivello.setEnabled(true);
                    txtIP.setEnabled(true);
                }
            }
        });
    }

    /**
     * Questo metodo e generato dal GUI Builder di NetBeans.
     * Per cambiare il layout usare la scheda Design.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitolo = new javax.swing.JLabel();
        lblNickname = new javax.swing.JLabel();
        lblLivello = new javax.swing.JLabel();
        lblIP = new javax.swing.JLabel();
        lblStato = new javax.swing.JLabel();
        txtNickname = new javax.swing.JTextField();
        txtLivello = new javax.swing.JTextField();
        txtIP = new javax.swing.JTextField();
        btnCerca = new javax.swing.JButton();
        scrollRisultato = new javax.swing.JScrollPane();
        txtRisultato = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MatchMaking PvP - Client");

        lblTitolo.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        lblTitolo.setText("MATCHMAKING PVP");

        lblNickname.setText("Nickname:");

        lblLivello.setText("Livello:");

        lblIP.setText("IP Server:");

        lblStato.setText("Stato: Non connesso (porta 50000)");

        txtIP.setText("127.0.0.1");

        btnCerca.setText("CERCA PARTITA");
        btnCerca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCercaActionPerformed(evt);
            }
        });

        txtRisultato.setColumns(20);
        txtRisultato.setRows(5);
        txtRisultato.setEditable(false);
        txtRisultato.setLineWrap(true);
        txtRisultato.setWrapStyleWord(true);
        scrollRisultato.setViewportView(txtRisultato);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitolo)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNickname)
                            .addComponent(lblLivello)
                            .addComponent(lblIP))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNickname, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .addComponent(txtLivello)
                            .addComponent(txtIP)))
                    .addComponent(btnCerca)
                    .addComponent(lblStato)
                    .addComponent(scrollRisultato, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitolo)
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNickname)
                    .addComponent(txtNickname, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLivello)
                    .addComponent(txtLivello, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIP)
                    .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(btnCerca, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblStato)
                .addGap(12, 12, 12)
                .addComponent(scrollRisultato, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCercaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCercaActionPerformed
        cercaPartita();
    }//GEN-LAST:event_btnCercaActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerca;
    private javax.swing.JLabel lblIP;
    private javax.swing.JLabel lblLivello;
    private javax.swing.JLabel lblNickname;
    private javax.swing.JLabel lblStato;
    private javax.swing.JLabel lblTitolo;
    private javax.swing.JScrollPane scrollRisultato;
    private javax.swing.JTextField txtIP;
    private javax.swing.JTextField txtLivello;
    private javax.swing.JTextField txtNickname;
    private javax.swing.JTextArea txtRisultato;
    // End of variables declaration//GEN-END:variables
}
