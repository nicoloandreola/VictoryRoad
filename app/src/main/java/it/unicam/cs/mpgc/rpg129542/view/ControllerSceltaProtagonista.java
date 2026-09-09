package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
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

    // Classe CSS applicata alla card del protagonista selezionato
    // per evidenziarla graficamente rispetto alle altre.
    private static final String CLASSE_CARD_SELEZIONATA = "card-selezionata";

    private ControllerGioco controllerGioco;
    private GestoreSchermate gestoreSchermate;

    private Protagonista protagonistaSelezionato;
    private VBox cardSelezionata;

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

    private List<Protagonista> protagonisti;
    private VBox[] cardsProtagonisti;
    private Label[] nomi, valoriAttacco, valoriDifesa, valoriAgilita, valoriOverall, tecnicheIniziali;

    /**
     * {@inheritDoc}
     *
     * Dopo aver ricevuto le dipendenze, recupera i protagonisti disponibili
     * dal controller principale e ne mostra i dati nelle rispettive card.
     *
     * @throws IllegalStateException se il numero di protagonisti disponibili
     *                              è diverso da tre, visto che la schermata
     *                              è progettata strutturalmente per 3 protagonisti
     */
    @Override
    public void configura(ControllerGioco controllerGioco, GestoreSchermate gestoreSchermate) {
        this.controllerGioco = controllerGioco;
        this.gestoreSchermate = gestoreSchermate;
        this.protagonisti = this.controllerGioco.getProtagonisti();
        if (protagonisti.size() != 3)
            throw new IllegalStateException("La schermata richiede esattamente tre protagonisti!");
        for (int i = 0; i < protagonisti.size(); i++)
            mostraProtagonista(i);
    }

    /**
     * {@inheritDoc}
     *
     * Inizializza i componenti della schermata raggruppando in array
     * le card e le Label corrispondenti dei tre protagonisti, così da
     * poterle gestire uniformemente attraverso il loro indice.
     *
     * Inoltre disabilita inizialmente il pulsante di conferma
     * poiché il giocatore deve prima selezionare un protagonista.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.cardsProtagonisti = new VBox[]{cardProtagonista1, cardProtagonista2, cardProtagonista3};
        this.nomi = new Label[]{nomeProtagonista1, nomeProtagonista2, nomeProtagonista3};
        this.valoriAttacco = new Label[]{attaccoProtagonista1, attaccoProtagonista2, attaccoProtagonista3};
        this.valoriDifesa = new Label[]{difesaProtagonista1, difesaProtagonista2, difesaProtagonista3};
        this.valoriAgilita = new Label[]{agilitaProtagonista1, agilitaProtagonista2, agilitaProtagonista3};
        this.valoriOverall = new Label[]{overallProtagonista1, overallProtagonista2, overallProtagonista3};
        this.tecnicheIniziali = new Label[]{tecnicaProtagonista1, tecnicaProtagonista2, tecnicaProtagonista3};
        this.conferma.setDisable(true);
    }

    // Aggiorna le Label di una card con i dati del protagonista corrispondente.
    private void mostraProtagonista(int indice) {
        Protagonista protagonista = this.protagonisti.get(indice);
        StatisticheBase s = protagonista.getStatisticheBase();
        this.nomi[indice].setText(protagonista.getNome());
        this.valoriAttacco[indice].setText("Attacco: " + s.getAttacco());
        this.valoriDifesa[indice].setText("Difesa: " + s.getDifesa());
        this.valoriAgilita[indice].setText("Agilità: " + s.getAgilita());
        this.valoriOverall[indice].setText(String.valueOf(protagonista.getOverall()));
        this.tecnicheIniziali[indice].setText("Tecnica iniziale: " + protagonista.getTecnicaIniziale().getNome());
    }

    @FXML
    private void scegliProtagonista1() {
        this.selezionaProtagonista(0);
    }

    @FXML
    private void scegliProtagonista2() {
        this.selezionaProtagonista(1);
    }

    @FXML
    private void scegliProtagonista3() {
        this.selezionaProtagonista(2);
    }

    // Memorizza il protagonista scelto, evidenzia graficamente la card selezionata
    // e abilita il pulsante che permette di confermare la scelta.
    private void selezionaProtagonista(int indice) {
        this.protagonistaSelezionato = this.protagonisti.get(indice);
        if (this.cardSelezionata != null)
            this.cardSelezionata.getStyleClass().remove(CLASSE_CARD_SELEZIONATA);
        this.cardSelezionata = this.cardsProtagonisti[indice];
        this.cardSelezionata.getStyleClass().add(CLASSE_CARD_SELEZIONATA);
        this.conferma.setDisable(false);
    }

    @FXML
    private void confermaScelta() throws IOException {
        if (this.protagonistaSelezionato == null)
            throw new IllegalStateException("Nessun protagonista selezionato!");

        this.controllerGioco.iniziaNuovaPartita(this.protagonistaSelezionato.getId());
        this.gestoreSchermate.mostraSchermata("mappa");
    }

    @FXML
    private void tornaAlMenu() throws IOException {
        this.gestoreSchermate.mostraSchermata("menu");
    }
}
