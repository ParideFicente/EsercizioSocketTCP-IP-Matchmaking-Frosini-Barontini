package matchmaking;

import javax.swing.SwingUtilities;

public class ServerGUI extends javax.swing.JFrame {

    public ServerGUI() {
        initComponents();
        setLocationRelativeTo(null);                       //posiziona la finestra al centro
        btnFerma.setEnabled(false);
    }

    private void avviaServer() {
        final int numero;
        final int differenza;
        try {
            numero = Integer.parseInt(txtNGG.getText().trim());
            differenza = Integer.parseInt(txtDML.getText().trim());
            if (numero < 2 || differenza < 0) {
                lblStato.setText("Stato: Parametri non validi");
                return;
            }
        } catch (NumberFormatException e) {
            lblStato.setText("Stato: Inserisci numeri validi");
            return;
        }

        btnAvvia.setEnabled(false);
        btnFerma.setEnabled(true);
        txtNGG.setEnabled(false);
        txtDML.setEnabled(false);
        txtEventi.setText("");
        lblStato.setText("Stato: Avvio del server...");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Server.avviaConParametri(numero, differenza, ServerGUI.this);
            }
        });
        t.start();                                          //avvia il server senza bloccare la finestra
    }

    private void fermaServer() {
        Server.ferma();                                     //interrompe l'ascolto del server
    }

    //aggiorna gli eventi nella finestra in modo sicuro
    public void aggiungiEvento(final String messaggio) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                txtEventi.append(messaggio + "\n");
                txtEventi.setCaretPosition(txtEventi.getDocument().getLength());
            }
        });
    }

    public void aggiornaStato(final boolean attivo) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                lblStato.setText(attivo ? "Stato: Server avviato (porta 50000)" : "Stato: Server spento");
                btnAvvia.setEnabled(!attivo);
                btnFerma.setEnabled(attivo);
                txtNGG.setEnabled(!attivo);
                txtDML.setEnabled(!attivo);
            }
        });
    }

    //il layout e gestito dal GUI Builder di NetBeans
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitolo = new javax.swing.JLabel();
        lblNGG = new javax.swing.JLabel();
        lblDML = new javax.swing.JLabel();
        lblPorta = new javax.swing.JLabel();
        lblStato = new javax.swing.JLabel();
        txtNGG = new javax.swing.JTextField();
        txtDML = new javax.swing.JTextField();
        txtPorta = new javax.swing.JTextField();
        btnAvvia = new javax.swing.JButton();
        btnFerma = new javax.swing.JButton();
        scrollEventi = new javax.swing.JScrollPane();
        txtEventi = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MatchMaking PvP - Server");

        lblTitolo.setFont(new java.awt.Font("SansSerif", 1, 20)); // NOI18N
        lblTitolo.setText("SERVER MATCHMAKING PVP");

        lblNGG.setText("Giocatori per gruppo:");

        lblDML.setText("Differenza max livelli:");

        lblPorta.setText("Porta TCP:");

        lblStato.setText("Stato: Server spento");

        txtPorta.setText("50000");
        txtPorta.setEditable(false);
        txtNGG.setText("2");
        txtDML.setText("3");

        btnFerma.setText("ARRESTA SERVER");
        btnFerma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFermaActionPerformed(evt);
            }
        });

        btnAvvia.setText("AVVIA SERVER");
        btnAvvia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvviaActionPerformed(evt);
            }
        });

        txtEventi.setColumns(20);
        txtEventi.setRows(5);
        txtEventi.setEditable(false);
        txtEventi.setLineWrap(true);
        txtEventi.setWrapStyleWord(true);
        scrollEventi.setViewportView(txtEventi);

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
                            .addComponent(lblNGG)
                            .addComponent(lblDML)
                            .addComponent(lblPorta))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNGG, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .addComponent(txtDML)
                            .addComponent(txtPorta)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAvvia)
                        .addGap(16, 16, 16)
                        .addComponent(btnFerma))
                    .addComponent(lblStato)
                    .addComponent(scrollEventi, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitolo)
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNGG)
                    .addComponent(txtNGG, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDML)
                    .addComponent(txtDML, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPorta)
                    .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAvvia, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFerma, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblStato)
                .addGap(12, 12, 12)
                .addComponent(scrollEventi, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAvviaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvviaActionPerformed
        avviaServer();
    }//GEN-LAST:event_btnAvviaActionPerformed

    private void btnFermaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFermaActionPerformed
        fermaServer();
    }//GEN-LAST:event_btnFermaActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAvvia;
    private javax.swing.JButton btnFerma;
    private javax.swing.JLabel lblPorta;
    private javax.swing.JLabel lblDML;
    private javax.swing.JLabel lblNGG;
    private javax.swing.JLabel lblStato;
    private javax.swing.JLabel lblTitolo;
    private javax.swing.JScrollPane scrollEventi;
    private javax.swing.JTextField txtPorta;
    private javax.swing.JTextField txtDML;
    private javax.swing.JTextField txtNGG;
    private javax.swing.JTextArea txtEventi;
    // End of variables declaration//GEN-END:variables
}
