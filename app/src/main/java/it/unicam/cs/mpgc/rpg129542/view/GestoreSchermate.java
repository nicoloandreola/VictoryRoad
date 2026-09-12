package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
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
     * situato nello stesso package di questa classe nella cartella {@code resources})
     * e la mostra sullo stage principale.
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeFxml + ".fxml"));
        Parent radice = loader.load();
        ControllerSchermata schermata = this.getControllerSchermata(loader);
        schermata.configura(this.controllerGioco, this);
        Scene scena = new Scene(radice);
        scena.getStylesheets().add(this.caricaFoglioDiStile());
        this.stagePrincipale.setScene(scena);
        this.stagePrincipale.show();
    }

    private ControllerSchermata getControllerSchermata(FXMLLoader loader) {
        Object controller = loader.getController();
        if (!(controller instanceof ControllerSchermata))
            throw new IllegalStateException("Il controller FXML deve implementare ControllerSchermata!");
        return (ControllerSchermata) controller;
    }

    private String caricaFoglioDiStile() {
        URL foglioDiStile = GestoreSchermate.class.getClassLoader().getResource("css/stile.css");
        if (foglioDiStile == null)
            throw new IllegalStateException("Foglio di stile non trovato!");
        return foglioDiStile.toExternalForm();
    }

    /**
     * Mostra un Alert di conferma e attende la scelta dell'utente.
     *
     * @param titolo titolo dell'Alert
     * @param intestazione frase principale dell'Alert
     * @param messaggio messaggio mostrato all'utente
     *
     * @return {@code true} se l'utente conferma, {@code false} altrimenti
     */
    public boolean chiediConferma(@NonNull String titolo, @NonNull String intestazione, @NonNull String messaggio) {
        Alert alert = this.creaAlertConferma(titolo, intestazione, messaggio);
        ButtonType conferma = new ButtonType("Conferma");
        ButtonType annulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.configuraPulsantiAlert(alert, conferma, annulla);
        return alert.showAndWait().orElse(annulla) == conferma;
    }

    private Alert creaAlertConferma(String titolo, String intestazione, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(intestazione);
        alert.setContentText(messaggio);
        alert.getDialogPane().getStylesheets().add(this.caricaFoglioDiStile());
        alert.getDialogPane().getStyleClass().add("alert");
        return alert;
    }

    private void configuraPulsantiAlert(Alert alert, ButtonType conferma, ButtonType annulla) {
        alert.getButtonTypes().setAll(conferma, annulla);
        Button pulsanteConferma = (Button) alert.getDialogPane().lookupButton(conferma);
        Button pulsanteAnnulla = (Button) alert.getDialogPane().lookupButton(annulla);
        pulsanteConferma.setDefaultButton(false);
        pulsanteAnnulla.setDefaultButton(true);
        pulsanteConferma.getStyleClass().add("pulsante-alert-uscita");
        pulsanteAnnulla.getStyleClass().add("pulsante-alert-annulla");
    }
}
