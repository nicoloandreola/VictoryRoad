package it.unicam.cs.mpgc.rpg129542;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import it.unicam.cs.mpgc.rpg129542.controller.ControllerPartita;
import it.unicam.cs.mpgc.rpg129542.controller.StrategiaAvversario;
import it.unicam.cs.mpgc.rpg129542.controller.StrategiaCasuale;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaStandard;
import it.unicam.cs.mpgc.rpg129542.persistence.Persistenza;
import it.unicam.cs.mpgc.rpg129542.persistence.PersistenzaXML;
import it.unicam.cs.mpgc.rpg129542.view.GestoreSchermate;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto di ingresso dell'applicazione JavaFX VictoryRoad.
 *
 * La classe costruisce e collega le principali dipendenze necessarie
 * all'avvio dell'applicazione: persistenza, logica dei match, strategia
 * dell'avversario, controller principale e gestione delle schermate.
 *
 * Configura inoltre lo Stage principale e la gestione della sua chiusura,
 * richiedendo conferma quando sono presenti progressi non salvati
 * oppure un match ancora in corso.
 *
 * Terminata l'inizializzazione, mostra la prima schermata dell'applicazione
 * (il menu principale) e delega la gestione dell'interfaccia a {@link GestoreSchermate}.
 *
 * @author Nicolò Andreola
 */
public class VictoryRoadApp extends Application {

    /**
     * Inizializza l'applicazione e mostra la prima schermata.
     *
     * @param stage finestra principale creata da JavaFX
     *
     * @throws IOException se si verifica un errore durante il caricamento
     *                     dei dati iniziali o della prima schermata
     */
    @Override
    public void start(Stage stage) throws IOException {
        ControllerGioco controllerGioco = this.creaControllerGioco();
        GestoreSchermate gestoreSchermate = new GestoreSchermate(stage, controllerGioco);
        this.configuraStage(stage);
        this.configuraChiusura(stage, controllerGioco, gestoreSchermate);
        gestoreSchermate.mostraSchermata("menu");
    }

    // Costruisce il controller principale dell'applicazione e lo inizializza
    private ControllerGioco creaControllerGioco() throws IOException {
        Persistenza persistenza = new PersistenzaXML();
        LogicaMatch logicaMatch = new LogicaStandard();
        StrategiaAvversario strategiaAvversario = new StrategiaCasuale();
        ControllerGioco controllerGioco = new ControllerGioco(persistenza, logicaMatch, strategiaAvversario);
        controllerGioco.inizializza();
        return controllerGioco;
    }

    // Imposta titolo e dimensioni della finestra
    private void configuraStage(Stage stage) {
        stage.setTitle("Victory Road");
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.setResizable(false);
    }

    // Gestisce la chiusura dell'applicazione dalla X della finestra
    private void configuraChiusura(Stage stage, ControllerGioco controllerGioco, GestoreSchermate gestoreSchermate) {
        stage.setOnCloseRequest(evento -> {
            ControllerPartita partita = controllerGioco.getPartitaCorrente();

            if (partita == null)
                return;

            if (!this.isAlertNecessario(controllerGioco, partita))
                return;

            boolean conferma = gestoreSchermate.chiediConferma("Chiudi Victory Road",
                    "Vuoi davvero chiudere il gioco?", "I progressi non salvati andranno persi.");

            if (!conferma)
                evento.consume();
        });
    }

    private boolean isAlertNecessario(ControllerGioco controllerGioco, ControllerPartita partita) {
        return controllerGioco.haModificheNonSalvate() || partita.isMatchInCorso();
    }

    /**
     * Avvia il runtime JavaFX.
     *
     * @param args argomenti ricevuti dalla linea di comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}
