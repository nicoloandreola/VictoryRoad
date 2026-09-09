package it.unicam.cs.mpgc.rpg129542;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
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
 * La classe si occupa esclusivamente di costruire e collegare
 * le principali dipendenze necessarie all'avvio dell'applicazione:
 * persistenza, logica dei match, strategia dell'avversario,
 * controller principale e gestione delle schermate.
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
        // Inizializza applicazione
        Persistenza persistenza = new PersistenzaXML();
        LogicaMatch logicaMatch = new LogicaStandard();
        StrategiaAvversario strategiaAvversario = new StrategiaCasuale();
        ControllerGioco controllerGioco = new ControllerGioco(persistenza, logicaMatch, strategiaAvversario);
        controllerGioco.inizializza();
        GestoreSchermate gestoreSchermate = new GestoreSchermate(stage, controllerGioco);
        // Imposta dimensioni della finestra
        stage.setTitle("Victory Road");
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.setResizable(false);
        // Mostra il menu principale
        gestoreSchermate.mostraSchermata("menu");
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
