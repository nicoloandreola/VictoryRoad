package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller associato alla schermata principale del gioco.
 *
 * Permette al giocatore di iniziare una nuova partita, caricare
 * una partita precedentemente salvata oppure chiudere l'applicazione.
 *
 * I suoi compiti sono quindi di reagire ai tre pulsanti, interrogare ControllerGioco
 * per il salvataggio e delegare la navigazione a GestoreSchermate.
 *
 * @author Nicolò Andreola
 */
public class ControllerMenu implements ControllerSchermata, Initializable {

    private ControllerGioco controllerGioco;
    private GestoreSchermate gestoreSchermate;

    @FXML
    private Button caricaPartita;

    /**
     * {@inheritDoc}
     *
     * Dopo aver ricevuto le dipendenze, aggiorna anche lo stato del pulsante
     * di caricamento: il pulsante viene disabilitato quando non è disponibile
     * alcuna partita salvata.
     */
    @Override
    public void configura(ControllerGioco controllerGioco, GestoreSchermate gestoreSchermate) {
        this.controllerGioco = controllerGioco;
        this.gestoreSchermate = gestoreSchermate;
        this.caricaPartita.setDisable(!this.controllerGioco.haSalvataggioDisponibile());
    }

    /**
     * {@inheritDoc}
     *
     * In questo caso il metodo è vuoto perché questa schermata non richiede inizializzazioni
     * che dipendono esclusivamente da JavaFX o da file FXML, ma solo da {@link ControllerGioco} e
     * le operazioni che dipendono dalle risorse condivise vengono eseguite successivamente in
     * {@link #configura(ControllerGioco, GestoreSchermate)}, che viene chiamato dopo questo metodo.
     *
     * Infatti prima viene eseguito {@code loader.load()}, durante il quale FXMLLoader chiama
     * {@code initialize()}, e solo dopo recuperiamo il controller e invochiamo {@code configura()}.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Mostra la schermata dalla quale il giocatore può scegliere
     * il protagonista con cui iniziare una nuova partita.
     *
     * @throws IOException se il caricamento della schermata fallisce
     */
    @FXML
    private void nuovaPartita() throws IOException {
        this.gestoreSchermate.mostraSchermata("sceltaProtagonista");
    }

    /**
     * Carica la partita precedentemente salvata e, se il caricamento
     * termina correttamente, mostra la schermata dei livelli.
     *
     * @throws IOException se si verifica un errore durante il caricamento
     *                     della partita o della schermata successiva
     */
    @FXML
    private void caricaSalvataggio() throws IOException {
        this.controllerGioco.caricaPartitaSalvata();
        this.gestoreSchermate.mostraSchermata("livelli");
    }

    /**
     * Termina l'esecuzione dell'applicazione JavaFX.
     */
    @FXML
    private void esci() {
        Platform.exit();
    }
}
