package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import it.unicam.cs.mpgc.rpg129542.controller.ControllerPartita;
import it.unicam.cs.mpgc.rpg129542.model.match.Match;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Controller associato alla schermata mostrata al termine di un match.
 *
 * La schermata rappresenta graficamente l'esito del match appena concluso:
 * mostra un'immagine differente in caso di vittoria o sconfitta e il
 * punteggio finale ottenuto dai due personaggi.
 *
 * Dopo una vittoria, se l'avversario sconfitto possiede tecniche che il
 * protagonista non ha ancora imparato, permette al giocatore di sceglierne
 * una come ricompensa, prima di tornare alla mappa.
 *
 * Dopo una sconfitta permette invece sia di tornare alla mappa per scegliere un
 * altro avversario da sfidare, che di riiniziare immediatamente un nuovo match
 * contro lo stesso avversario per riprovare a sconfiggerlo.
 *
 * La conclusione effettiva del match viene richiesta a
 * {@link ControllerPartita} solamente quando il giocatore decide come
 * proseguire, così che durante la visualizzazione della schermata rimangano
 * ancora disponibili il punteggio, il vincitore e le eventuali ricompense.
 *
 * @author Nicolò Andreola
 */
public class ControllerEsitoMatch implements ControllerSchermata, Initializable {

    private ControllerGioco controllerGioco;
    private ControllerPartita controllerPartita;
    private GestoreSchermate gestoreSchermate;
    private Match match;

    @FXML
    private ImageView immagineEsito;
    @FXML
    private Label nomeProtagonista, nomeAvversario;
    @FXML
    private Label punteggioFinale;
    @FXML
    private VBox pannelloRicompensa;
    @FXML
    private ComboBox<TecnicaSpeciale> comboBoxRicompensa;
    @FXML
    private Button pulsanteRiprova, pulsanteTornaMappa;

    /**
     * {@inheritDoc}
     *
     * In questo caso il metodo è vuoto perché questa schermata non richiede inizializzazioni
     * che dipendono esclusivamente da JavaFX o da file FXML, ma solo da {@link ControllerGioco}; e
     * le operazioni che dipendono dalle risorse condivise vengono eseguite successivamente in
     * {@link #configura(ControllerGioco, GestoreSchermate)}, che viene chiamato dopo questo metodo.
     *
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * {@inheritDoc}
     *
     * Recupera la partita e il match appena concluso, mostra il punteggio
     * finale e configura la schermata in modo differente a seconda che
     * il protagonista abbia vinto oppure perso.
     *
     * @throws IllegalStateException se non è presente una partita attiva,
     *                               se non esiste un match corrente oppure
     *                               se il match non è ancora terminato
     */
    @Override
    public void configura(ControllerGioco controllerGioco, GestoreSchermate gestoreSchermate) {
        this.controllerGioco = controllerGioco;
        this.gestoreSchermate = gestoreSchermate;
        this.controllerPartita = controllerGioco.getPartitaCorrente();

        if (this.controllerPartita == null)
            throw new IllegalStateException("Nessuna partita attiva!");

        this.match = this.controllerPartita.getMatchCorrente();

        if (this.match == null)
            throw new IllegalStateException("Nessun match disponibile!");

        if (!this.match.isConcluso())
            throw new IllegalStateException("Il match non è ancora terminato!");

        this.mostraPunteggioFinale();
        if (this.controllerPartita.isVittoriaProtagonista())
            this.configuraVittoria();
        else
            this.configuraSconfitta();
    }

    @FXML
    private void selezionaTecnica() {
        this.pulsanteTornaMappa.setDisable(this.comboBoxRicompensa.getValue() == null);
    }

    @FXML
    private void tornaAllaMappa() throws IOException {
        boolean vittoria = this.controllerPartita.isVittoriaProtagonista();
        TecnicaSpeciale tecnicaScelta = this.comboBoxRicompensa.getValue();
        this.controllerPartita.terminaMatch(tecnicaScelta);

        if (vittoria)
            this.controllerGioco.segnaModificheNonSalvate();
        this.gestoreSchermate.mostraSchermata("mappa");
    }

    @FXML
    private void riprovaMatch() throws IOException {
        if (this.controllerPartita.isVittoriaProtagonista())
            throw new IllegalStateException("È possibile riprovare solamente dopo una sconfitta!");

        this.controllerPartita.terminaMatch(null);
        this.controllerPartita.iniziaMatch();
        this.gestoreSchermate.mostraSchermata("match");
    }

    private void mostraPunteggioFinale() {
        this.nomeProtagonista.setText(this.match.getProtagonista().getNome());
        this.punteggioFinale.setText(this.match.getGolProtagonista() + "  -  " + this.match.getGolAvversario());
        this.nomeAvversario.setText(this.match.getAvversario().getNome());
    }

    // Configura immagine, ricompense e pulsanti dopo una vittoria.
    private void configuraVittoria() {
        Image immagine = CaricatoreImmagini.carica("immagini/esitoMatch/vittoria.png");
        this.immagineEsito.setImage(immagine);

        this.pulsanteRiprova.setVisible(false);
        this.pulsanteRiprova.setManaged(false);

        Set<TecnicaSpeciale> tecnicheRicompensa = this.controllerPartita.getTecnicheRicompensa();
        if (tecnicheRicompensa.isEmpty()) {
            this.nascondiPannelloRicompensa();
            this.pulsanteTornaMappa.setDisable(false);
        }
        else {
            this.mostraPannelloRicompensa(tecnicheRicompensa);
            this.pulsanteTornaMappa.setDisable(true);
        }
    }

    // Configura immagine e pulsanti dopo una sconfitta e nasconde il pannello delle ricompense.
    private void configuraSconfitta() {
        Image immagine = CaricatoreImmagini.carica("immagini/esitoMatch/sconfitta.png");
        this.immagineEsito.setImage(immagine);

        this.nascondiPannelloRicompensa();
        this.pulsanteRiprova.setVisible(true);
        this.pulsanteRiprova.setManaged(true);
        this.pulsanteTornaMappa.setDisable(false);
    }

    private void mostraPannelloRicompensa(Set<TecnicaSpeciale> tecnicheRicompensa) {
        this.pannelloRicompensa.setVisible(true);
        this.pannelloRicompensa.setManaged(true);
        this.comboBoxRicompensa.getItems().setAll(tecnicheRicompensa);
    }

    private void nascondiPannelloRicompensa() {
        this.pannelloRicompensa.setVisible(false);
        this.pannelloRicompensa.setManaged(false);
    }
}
