package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import it.unicam.cs.mpgc.rpg129542.controller.ControllerPartita;
import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.livello.StatoAvversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller associato alla schermata della mappa di gioco.
 *
 * Mostra la progressione complessiva della partita, i livelli disponibili
 * e le informazioni relative al protagonista corrente. Permette inoltre
 * di selezionare un livello e uno dei suoi avversari prima di avviare
 * il relativo match.
 *
 * La schermata utilizza {@link ControllerPartita} per tutte le operazioni
 * che modificano lo stato della partita, limitandosi a rappresentare
 * graficamente i dati e a reagire alle azioni dell'utente.
 *
 * @author Nicolò Andreola
 */
public class ControllerMappa implements ControllerSchermata, Initializable {

    // Classi CSS aggiunte e rimosse dinamicamente in base allo stato
    // dei livelli e degli avversari mostrati nella schermata.
    private static final String CLASSE_LIVELLO_SELEZIONATO = "nodo-livello-selezionato";
    private static final String CLASSE_LIVELLO_COMPLETATO = "nodo-livello-completato";
    private static final String CLASSE_CARD_SELEZIONATA = "card-selezionata";

    private ControllerGioco controllerGioco;
    private GestoreSchermate gestoreSchermate;
    private ControllerPartita controllerPartita;

    private List<Livello> livelli;
    private List<Avversario> avversariCorrenti;

    @FXML
    private Label nomeProtagonista;
    @FXML
    private Text overallProtagonista;
    @FXML
    private ProgressBar barraLivelliCompletati, barraAvversariSconfitti;
    @FXML
    private Label percentualeLivelliCompletati, percentualeAvversariSconfitti;
    @FXML
    private Button livello1, livello2, livello3, livello4, livello5, livello6, livello7;
    @FXML
    private Label titoloLivello, statoLivello, nomeCampo, descrizioneCampo;
    @FXML
    private VBox cardAvversario1, cardAvversario2;
    @FXML
    private Label nomeAvversario1, nomeAvversario2;
    @FXML
    private Text overallAvversario1, overallAvversario2;
    @FXML
    private ImageView immagineAvversario1, immagineAvversario2;
    @FXML
    private Label statisticheAvversario1, statisticheAvversario2;
    @FXML
    private Label statoAvversario1, statoAvversario2;
    @FXML
    private Button pulsanteAvversario1, pulsanteAvversario2;
    @FXML
    private Button pulsanteIniziaMatch;

    private Button[] pulsantiLivelli, pulsantiAvversari;
    private VBox[] cardsAvversari;
    private Label[] nomiAvversari, statisticheAvversari, statiAvversari;
    private Text[] overallAvversari;
    private ImageView[] immaginiAvversari;

    /**
     * {@inheritDoc}
     *
     * Prepara gli array utilizzati per gestire in modo uniforme i sette
     * pulsanti dei livelli e le due card degli avversari. Il pulsante
     * per iniziare il match viene inizialmente disabilitato finché
     * non risulta selezionato un avversario del livello corrente.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.pulsantiLivelli = new Button[]{livello1, livello2, livello3, livello4, livello5, livello6, livello7};
        this.cardsAvversari = new VBox[]{cardAvversario1, cardAvversario2};
        this.nomiAvversari = new Label[]{nomeAvversario1, nomeAvversario2};
        this.overallAvversari = new Text[]{overallAvversario1, overallAvversario2};
        this.immaginiAvversari = new ImageView[]{immagineAvversario1, immagineAvversario2};
        this.statisticheAvversari = new Label[]{statisticheAvversario1, statisticheAvversario2};
        this.statiAvversari = new Label[]{statoAvversario1, statoAvversario2};
        this.pulsantiAvversari = new Button[]{pulsanteAvversario1, pulsanteAvversario2};
        this.pulsanteIniziaMatch.setDisable(true);
    }

    /**
     * {@inheritDoc}
     *
     * Dopo aver ricevuto le dipendenze recupera la partita corrente,
     * mostra le informazioni del protagonista, aggiorna la progressione
     * complessiva e visualizza il livello attualmente selezionato.
     */
    @Override
    public void configura(ControllerGioco controllerGioco, GestoreSchermate gestoreSchermate) {
        this.controllerGioco = controllerGioco;
        this.gestoreSchermate = gestoreSchermate;
        this.controllerPartita = this.controllerGioco.getPartitaCorrente();
        if (this.controllerPartita == null)
            throw new IllegalStateException("Nessuna partita attiva!");
        this.livelli = this.controllerPartita.getLivelli();
        this.aggiornaProtagonista();
        this.aggiornaProgressione();
        this.aggiornaMappa();
        this.mostraLivelloCorrente();
    }

    @FXML
    private void selezionaLivello(ActionEvent evento) {
        Button pulsante = (Button) evento.getSource();
        for (int i = 0; i < this.pulsantiLivelli.length; i++)
            if (this.pulsantiLivelli[i] == pulsante) {
                this.controllerPartita.selezionaLivello(this.livelli.get(i));
                this.aggiornaMappa();
                this.mostraLivelloCorrente();
                return;
            }
    }

    @FXML
    private void selezionaAvversario(ActionEvent evento) {
        Button pulsante = (Button) evento.getSource();
        for (int i = 0; i < this.pulsantiAvversari.length; i++)
            if (this.pulsantiAvversari[i] == pulsante) {
                Avversario avversario = this.avversariCorrenti.get(i);
                this.controllerPartita.selezionaAvversario(avversario);
                this.mostraLivelloCorrente();
                return;
            }
    }

    @FXML
    private void salvaPartita() throws IOException {
        this.controllerGioco.salvaPartitaCorrente();
    }

    @FXML
    private void tornaAlMenu() throws IOException {
        this.gestoreSchermate.mostraSchermata("menu");
    }

    @FXML
    private void iniziaMatch() throws IOException {
        this.controllerPartita.iniziaMatch();
        this.gestoreSchermate.mostraSchermata("match");
    }

    // Aggiorna nell'intestazione le informazioni del protagonista corrente.
    private void aggiornaProtagonista() {
        Protagonista protagonista = this.controllerPartita.getProtagonistaCorrente();
        this.nomeProtagonista.setText(protagonista.getNome());
        this.overallProtagonista.setText(String.valueOf(protagonista.getOverall()));
    }

    // Aggiorna le barre relative alla progressione complessiva.
    private void aggiornaProgressione() {
        int percentualeLivelli = this.controllerPartita.getPercentualeLivelliCompletati();
        this.percentualeLivelliCompletati.setText(percentualeLivelli + "%");
        this.barraLivelliCompletati.setProgress(percentualeLivelli / 100.0);
        int percentualeAvversari = this.controllerPartita.getPercentualeAvversariSconfitti();
        this.percentualeAvversariSconfitti.setText(percentualeAvversari + "%");
        this.barraAvversariSconfitti.setProgress(percentualeAvversari / 100.0);
    }

    // Aggiorna disponibilità e stile grafico dei pulsanti dei livelli.
    private void aggiornaMappa() {
        Livello livelloCorrente = this.controllerPartita.getLivelloCorrente();
        for (int i = 0; i < this.livelli.size(); i++) {
            Livello livello = this.livelli.get(i);
            Button pulsante = this.pulsantiLivelli[i];
            pulsante.setDisable(!this.controllerPartita.isLivelloDisponibile(livello));
            pulsante.getStyleClass().remove(CLASSE_LIVELLO_SELEZIONATO);
            pulsante.getStyleClass().remove(CLASSE_LIVELLO_COMPLETATO);
            if (livello.isCompletato())
                pulsante.getStyleClass().add(CLASSE_LIVELLO_COMPLETATO);
            if (livello.equals(livelloCorrente))
                pulsante.getStyleClass().add(CLASSE_LIVELLO_SELEZIONATO);
        }
    }

    private void mostraLivelloCorrente() {
        Livello livello = this.controllerPartita.getLivelloCorrente();
        this.titoloLivello.setText("Livello " + livello.getNumero());
        this.statoLivello.setText(livello.isCompletato() ? "Completato" : "Disponibile");
        this.mostraCampo(livello);
        this.aggiornaAvversariCorrenti(livello);
        this.aggiornaPulsanteMatch();
    }

    private void mostraCampo(Livello livello) {
        this.nomeCampo.setText(livello.getCampo().getNome());
        this.descrizioneCampo.setText(livello.getCampo().getDescrizione());
    }

    // Recupera gli avversari del livello e aggiorna le due card della schermata.
    private void aggiornaAvversariCorrenti(Livello livello) {
        this.avversariCorrenti = new ArrayList<>(livello.getAvversari().keySet());
        if (this.avversariCorrenti.size() != 2)
            throw new IllegalStateException("La schermata può contenere solo 2 avversari!");
        for (int i = 0; i < this.cardsAvversari.length; i++)
            this.mostraAvversario(i);
    }

    private void mostraAvversario(int indice) {
        Avversario avversario = this.avversariCorrenti.get(indice);
        Livello livello = this.controllerPartita.getLivelloCorrente();
        StatoAvversario stato = livello.getStatoAvversario(avversario);
        StatisticheBase statistiche = avversario.getStatisticheBase();
        this.nomiAvversari[indice].setText(avversario.getNome());
        this.overallAvversari[indice].setText(String.valueOf(avversario.getOverall()));
        this.statisticheAvversari[indice].setText("ATT " + statistiche.getAttacco()
                        + " | DIF " + statistiche.getDifesa() + " | AGI " + statistiche.getAgilita());
        this.statiAvversari[indice].setText(this.aggiornaStatoAvversario(stato));
        Image immagineAvversario = CaricatoreImmagini.carica(avversario.getPercorsoImmagine());
        this.immaginiAvversari[indice].setImage(immagineAvversario);
        this.aggiornaCardAvversario(indice, stato);
    }

    private String aggiornaStatoAvversario(StatoAvversario stato) {
        return switch (stato) {
            case DA_SCONFIGGERE -> "Da sconfiggere";
            case SELEZIONATO -> "Pronto per combattere";
            case SCONFITTO -> "Sconfitto";
        };
    }

    // Aggiorna stile, testo e disponibilità del pulsante in base allo stato dell'avversario.
    private void aggiornaCardAvversario(int indice, StatoAvversario stato) {
        VBox card = this.cardsAvversari[indice];
        Button pulsante = this.pulsantiAvversari[indice];
        card.getStyleClass().remove(CLASSE_CARD_SELEZIONATA);

        switch (stato) {
            case DA_SCONFIGGERE -> {
                pulsante.setText("Seleziona");
                pulsante.setDisable(false);
            }

            case SELEZIONATO -> {
                card.getStyleClass().add(CLASSE_CARD_SELEZIONATA);
                pulsante.setText("Selezionato");
                pulsante.setDisable(true);
            }

            case SCONFITTO -> {
                pulsante.setText("Sconfitto");
                pulsante.setDisable(true);
            }
        }
    }

    private void aggiornaPulsanteMatch() {
        boolean avversarioSelezionato = this.controllerPartita.getLivelloCorrente().isAvversarioSelezionato();
        this.pulsanteIniziaMatch.setDisable(!avversarioSelezionato);
    }
}
