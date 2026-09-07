package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller associato alla schermata di scelta del protagonista.
 *
 * Mostra i protagonisti disponibili e le rispettive caratteristiche,
 * permette al giocatore di selezionarne uno e, dopo la conferma,
 * avvia una nuova partita utilizzando il protagonista scelto.
 *
 * @author Nicolò Andreola
 */
public class ControllerSceltaProtagonista implements ControllerSchermata, Initializable {

    private static final String CLASSE_CARD_SELEZIONATA = "card-selezionata";

    private ControllerGioco controllerGioco;
    private GestoreSchermate gestoreSchermate;

    private Protagonista protagonista1, protagonista2, protagonista3;
    private Protagonista protagonistaSelezionato;

    @FXML
    private VBox cardProtagonista1, cardProtagonista2, cardProtagonista3;
    @FXML
    private Label nomeProtagonista1, nomeProtagonista2, nomeProtagonista3;
    @FXML
    private Label attaccoProtagonista1, attaccoProtagonista2, attaccoProtagonista3;
    @FXML
    private Label difesaProtagonista1, difesaProtagonista2, difesaProtagonista3;
    @FXML
    private Label agilitaProtagonista1, agilitaProtagonista2, agilitaProtagonista3;
    @FXML
    private Label overallProtagonista1, overallProtagonista2, overallProtagonista3;
    @FXML
    private Label tecnicaProtagonista1, tecnicaProtagonista2, tecnicaProtagonista3;
    @FXML
    private Button conferma;

    /**
     * {@inheritDoc}
     *
     * Dopo aver ricevuto le dipendenze, recupera i protagonisti disponibili
     * dal controller principale e ne mostra i dati nelle rispettive card.
     */
    @Override
    public void configura(ControllerGioco controllerGioco, GestoreSchermate gestoreSchermate) {
        this.controllerGioco = controllerGioco;
        this.gestoreSchermate = gestoreSchermate;

        List<Protagonista> protagonisti = this.controllerGioco.getProtagonisti();
        // Questa View è progettata strutturalmente per tre protagonisti
        if (protagonisti.size() != 3)
            throw new IllegalStateException("La schermata richiede esattamente tre protagonisti!");
        this.protagonista1 = protagonisti.get(0);
        this.protagonista2 = protagonisti.get(1);
        this.protagonista3 = protagonisti.get(2);

        this.mostraProtagonista(this.protagonista1, this.nomeProtagonista1, this.attaccoProtagonista1,
                this.difesaProtagonista1, this.agilitaProtagonista1, this.overallProtagonista1, this.tecnicaProtagonista1);
        this.mostraProtagonista(this.protagonista2, this.nomeProtagonista2, this.attaccoProtagonista2,
                this.difesaProtagonista2, this.agilitaProtagonista2, this.overallProtagonista2, this.tecnicaProtagonista2);
        this.mostraProtagonista(this.protagonista3, this.nomeProtagonista3, this.attaccoProtagonista3,
                this.difesaProtagonista3, this.agilitaProtagonista3,this.overallProtagonista3, this.tecnicaProtagonista3);
    }

    /**
     * {@inheritDoc}
     *
     * Il pulsante di conferma viene inizialmente disabilitato,
     * poiché il giocatore deve prima selezionare un protagonista.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.conferma.setDisable(true);
    }

    // Aggiorna le Label di una card con i dati del protagonista ricevuto.
    private void mostraProtagonista(Protagonista protagonista, Label nome, Label attacco,
                                    Label difesa, Label agilita, Label overall, Label tecnica) {
        StatisticheBase statistiche = protagonista.getStatisticheBase();
        TecnicaSpeciale tecnicaIniziale = this.getTecnicaIniziale(protagonista);
        nome.setText(protagonista.getNome());
        attacco.setText("Attacco: " + statistiche.getAttacco());
        difesa.setText("Difesa: " + statistiche.getDifesa());
        agilita.setText("Agilità: " + statistiche.getAgilita());
        overall.setText(String.valueOf(protagonista.getOverall()));
        tecnica.setText("Tecnica iniziale: " + tecnicaIniziale.getNome());
    }

    private TecnicaSpeciale getTecnicaIniziale(Protagonista protagonista) {
        for (TecnicaSpeciale tecnica : protagonista.getTecnicheSpeciali())
            return tecnica;
        throw new IllegalStateException("Il protagonista non possiede alcuna tecnica!");
    }

    @FXML
    private void scegliProtagonista1() {
        this.selezionaProtagonista(this.protagonista1, this.cardProtagonista1);
    }

    @FXML
    private void scegliProtagonista2() {
        this.selezionaProtagonista(this.protagonista2, this.cardProtagonista2);
    }

    @FXML
    private void scegliProtagonista3() {
        this.selezionaProtagonista(this.protagonista3, this.cardProtagonista3);
    }

    // Memorizza il protagonista scelto, prepara graficamente la card selezionata
    // e abilita il pulsante che permette di confermare la scelta.
    private void selezionaProtagonista(Protagonista protagonista, VBox cardSelezionata) {
        this.protagonistaSelezionato = protagonista;

        this.cardProtagonista1.getStyleClass().remove(CLASSE_CARD_SELEZIONATA);
        this.cardProtagonista2.getStyleClass().remove(CLASSE_CARD_SELEZIONATA);
        this.cardProtagonista3.getStyleClass().remove(CLASSE_CARD_SELEZIONATA);

        cardSelezionata.getStyleClass().add(CLASSE_CARD_SELEZIONATA);

        this.conferma.setDisable(false);
    }

    @FXML
    private void confermaScelta() throws IOException {
        if (this.protagonistaSelezionato == null)
            throw new IllegalStateException("Nessun protagonista selezionato!");

        this.controllerGioco.iniziaNuovaPartita(this.protagonistaSelezionato.getId());
        this.gestoreSchermate.mostraSchermata("livelli");
    }
}
