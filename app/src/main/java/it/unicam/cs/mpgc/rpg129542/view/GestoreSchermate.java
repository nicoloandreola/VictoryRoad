package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NonNull;

import java.io.IOException;
import java.net.URL;

/**
 * Si occupa di caricare le schermate FXML dell'applicazione e di mostrarle
 * sullo {@link Stage} principale.
 *
 * Ogni volta che una schermata viene caricata, se il suo controller implementa
 * {@link ControllerSchermata}, gli vengono forniti automaticamente il
 * {@link ControllerGioco} condiviso e questo stesso gestore: in questo modo i
 * singoli controller non devono preoccuparsi di come recuperare questi riferimenti.
 *
 * @author Nicolò Andreola
 */
public class GestoreSchermate {

    private final Stage stagePrincipale;
    private final ControllerGioco controllerGioco;

    /**
     * Crea il gestore delle schermate.
     *
     * @param stagePrincipale finestra principale dell'applicazione
     * @param controllerGioco controller dell'applicazione da fornire a ogni schermata caricata
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     */
    public GestoreSchermate(@NonNull Stage stagePrincipale, @NonNull ControllerGioco controllerGioco) {
        this.stagePrincipale = stagePrincipale;
        this.controllerGioco = controllerGioco;
    }

    /**
     * Carica la schermata identificata dal nome del file FXML (senza estensione,
     * situato nella cartella {@code resources/fxml}) e la mostra sullo stage principale.
     *
     * Non utilizza direttamente il metodo statico {@link FXMLLoader#load(URL)},
     * ma si salva l'istanza del loader nell'omonima variabile per poter poi
     * recuperare il controller creato dal FXML con {@link FXMLLoader#getController()}
     * e configurarlo correttamente con {@link ControllerSchermata#configura(ControllerGioco, GestoreSchermate)}.
     *
     * @param nomeFxml nome del file FXML da caricare
     *
     * @throws NullPointerException se {@code nomeFxml} è {@code null}
     *
     * @throws IOException se il caricamento del file FXML fallisce
     *
     * @throws IllegalStateException se il controller associato al file FXML
     *                              non implementa {@link ControllerSchermata}
     */
    public void mostraSchermata(@NonNull String nomeFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(nomeFxml + ".fxml"));
        Parent radice = loader.load();
        Object controller = loader.getController();
        if (!(controller instanceof ControllerSchermata))
            throw new IllegalStateException("Il controller FXML deve implementare ControllerSchermata!");
        ControllerSchermata schermata = (ControllerSchermata) controller;
        schermata.configura(this.controllerGioco, this);
        Scene scena = new Scene(radice);
        this.stagePrincipale.setScene(scena);
        this.stagePrincipale.show();
    }
}
