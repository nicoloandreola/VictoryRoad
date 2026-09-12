package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;
import it.unicam.cs.mpgc.rpg129542.controller.ControllerPartita;
import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneAttaccante;
import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneDifensore;
import it.unicam.cs.mpgc.rpg129542.model.azioni.Contrasto;
import it.unicam.cs.mpgc.rpg129542.model.azioni.Dribbling;
import it.unicam.cs.mpgc.rpg129542.model.azioni.Parata;
import it.unicam.cs.mpgc.rpg129542.model.azioni.Tiro;
import it.unicam.cs.mpgc.rpg129542.model.livello.Campo;
import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.match.EsitoTurno;
import it.unicam.cs.mpgc.rpg129542.model.match.Match;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.RisorseMatch;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSupporto;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TipoTecnica;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller associato alla schermata di un match.
 *
 * Mostra lo stato corrente dello scontro tra protagonista e avversario,
 * comprendendo punteggio, possesso, statistiche effettive, stamina,
 * resistenza e informazioni relative all'ultima azione eseguita.
 *
 * La classe si limita quindi a tradurre le scelte dell'utente in chiamate
 * al controller applicativo e ad aggiornare conseguentemente l'interfaccia
 * JavaFX: non contiene le regole con cui vengono calcolati gli esiti del
 * combattimento, che rimangono responsabilità del model e del controller.
 *
 * La schermata utilizza {@link ControllerPartita} per coordinare lo
 * svolgimento dei turni. Quando il protagonista attacca, la scelta
 * della difesa dell'avversario e la successiva risoluzione del turno
 * avvengono automaticamente dopo la selezione dell'azione del giocatore.
 *
 * Quando invece attacca l'avversario, la sua azione viene determinata
 * prima della risposta del protagonista, così che la View possa mostrare
 * solamente le difese compatibili e disabilitare le altre.
 *
 * Le tecniche mostrate nel {@link MenuButton} sono solamente quelle
 * compatibili con il turno corrente. Una tecnica compatibile rimane
 * visibile anche quando la stamina non è sufficiente per utilizzarla:
 * in tal caso viene oscurata e accompagnata da un tooltip esplicativo
 * che mostra il motivo dell'indisponibilità al passaggio del mouse.
 *
 * @author Nicolò Andreola
 */
public class ControllerMatch implements ControllerSchermata, Initializable {

    private GestoreSchermate gestoreSchermate;
    private ControllerPartita controllerPartita;
    private Match matchCorrente;
    private Protagonista protagonista;
    private Avversario avversario;
    private AzioneAttaccante attaccoAvversarioCorrente;

    @FXML
    private Label punteggioMatch, dettaglioMatch;
    @FXML
    private Label nomeProtagonistaCard, nomeAvversarioCard;
    @FXML
    private ImageView immagineProtagonista, immagineAvversario, immagineCampo;
    @FXML
    private ProgressBar staminaProtagonista, resistenzaProtagonista, staminaAvversario, resistenzaAvversario;
    @FXML
    private Label valoreStaminaProtagonista, valoreResistenzaProtagonista, valoreStaminaAvversario, valoreResistenzaAvversario;
    @FXML
    private Label attaccoProtagonista, difesaProtagonista, agilitaProtagonista;
    @FXML
    private Label attaccoAvversario, difesaAvversario, agilitaAvversario;
    @FXML
    private Label possesso, titoloAzioni, esitoTurno;
    @FXML
    private Label nomeProtagonistaTurno, azioneProtagonistaTurno;
    @FXML
    private Label nomeAvversarioTurno, azioneAvversarioTurno;
    @FXML
    private Button pulsanteTiro, pulsanteDribbling, pulsanteParata, pulsanteContrasto;
    @FXML
    private MenuButton menuTecniche;

    /**
     * {@inheritDoc}
     *
     * Inizializza gli elementi della schermata che non dipendono ancora
     * dallo stato della partita. Le informazioni effettive del match
     * vengono caricate successivamente da {@link #configura(ControllerGioco, GestoreSchermate)}.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.attaccoAvversarioCorrente = null;
        this.azioneProtagonistaTurno.setText("—");
        this.azioneAvversarioTurno.setText("—");
        this.esitoTurno.setText("Nessun turno eseguito");
        this.menuTecniche.getItems().clear();
    }

    /**
     * {@inheritDoc}
     *
     * Recupera la partita e il match correnti, verifica che lo scontro
     * sia stato effettivamente avviato e inizializza tutte le informazioni
     * mostrate dalla schermata.
     *
     * Infine prepara il primo turno in base al personaggio sorteggiato
     * come attaccante da {@link Match}: se inizia il protagonista vengono
     * mostrate le azioni offensive, mentre se inizia l'avversario viene
     * prima determinato e mostrato il suo attacco.
     *
     * @throws IllegalStateException se non esiste una partita attiva
     *                               oppure non è presente un match in corso
     */
    @Override
    public void configura(ControllerGioco controllerGioco, GestoreSchermate gestoreSchermate) {
        this.gestoreSchermate = gestoreSchermate;
        this.controllerPartita = controllerGioco.getPartitaCorrente();
        if (this.controllerPartita == null)
            throw new IllegalStateException("Nessuna partita attiva!");
        this.matchCorrente = this.controllerPartita.getMatchCorrente();

        if (this.matchCorrente == null)
            throw new IllegalStateException("Nessun match in corso!");
        this.protagonista = this.matchCorrente.getProtagonista();
        this.avversario = this.matchCorrente.getAvversario();
        this.mostraDatiMatch();
        this.aggiornaStatoMatch();
        this.preparaTurno();
    }

    @FXML
    private void eseguiTiro() {
        this.eseguiAttaccoProtagonista(new Tiro());
    }

    @FXML
    private void eseguiDribbling() {
        this.eseguiAttaccoProtagonista(new Dribbling());
    }

    @FXML
    private void eseguiParata() {
        this.eseguiDifesaProtagonista(new Parata());
    }

    @FXML
    private void eseguiContrasto() {
        this.eseguiDifesaProtagonista(new Contrasto());
    }

    private void mostraDatiMatch() {
        this.mostraPersonaggi();
        this.mostraCampo();
        this.aggiornaStatistiche();
    }

    private void mostraPersonaggi() {
        this.nomeProtagonistaCard.setText(this.protagonista.getNome());
        this.nomeAvversarioCard.setText(this.avversario.getNome());
        this.nomeProtagonistaTurno.setText(this.protagonista.getNome() + ":");
        this.nomeAvversarioTurno.setText(this.avversario.getNome() + ":");
        Image immagineProtagonista = CaricatoreImmagini.carica(this.protagonista.getPercorsoImmagine());
        Image immagineAvversario = CaricatoreImmagini.carica(this.avversario.getPercorsoImmagine());
        this.immagineProtagonista.setImage(immagineProtagonista);
        this.immagineAvversario.setImage(immagineAvversario);
    }

    private void mostraCampo() {
        Livello livello = this.controllerPartita.getLivelloCorrente();
        Campo campo = livello.getCampo();
        this.dettaglioMatch.setText("Livello " + livello.getNumero() + " • " + campo.getNome());
        Image immagineCampo = CaricatoreImmagini.carica(campo.getPercorsoImmagine());
        this.immagineCampo.setImage(immagineCampo);
    }

    private void aggiornaStatistiche() {
        StatisticheBase statisticheProtagonista = this.protagonista.getStatisticheEffettive();
        this.attaccoProtagonista.setText("Attacco: " + statisticheProtagonista.getAttacco());
        this.difesaProtagonista.setText("Difesa: " + statisticheProtagonista.getDifesa());
        this.agilitaProtagonista.setText("Agilità: " + statisticheProtagonista.getAgilita());

        StatisticheBase statisticheAvversario = this.avversario.getStatisticheEffettive();
        this.attaccoAvversario.setText("Attacco: " + statisticheAvversario.getAttacco());
        this.difesaAvversario.setText("Difesa: " + statisticheAvversario.getDifesa());
        this.agilitaAvversario.setText("Agilità: " + statisticheAvversario.getAgilita());
    }

    private void aggiornaStatoMatch() {
        this.punteggioMatch.setText(this.matchCorrente.getGolProtagonista()
                + "  –  " + this.matchCorrente.getGolAvversario());
        this.possesso.setText(this.matchCorrente.getAttaccanteCorrente().getNome());
        this.aggiornaRisorse(this.protagonista, this.staminaProtagonista, this.resistenzaProtagonista, this.valoreStaminaProtagonista, this.valoreResistenzaProtagonista);
        this.aggiornaRisorse(this.avversario, this.staminaAvversario, this.resistenzaAvversario, this.valoreStaminaAvversario, this.valoreResistenzaAvversario);
    }

    private void aggiornaRisorse(Personaggio personaggio, ProgressBar barraStamina, ProgressBar barraResistenza,
    Label valoreStamina, Label valoreResistenza) {
        barraStamina.setProgress(personaggio.getStamina() / (double) RisorseMatch.STAMINA_MASSIMA);
        barraResistenza.setProgress(personaggio.getResistenza() / (double) RisorseMatch.RESISTENZA_MASSIMA);
        valoreStamina.setText("Stamina: " + personaggio.getStamina() + " / " + RisorseMatch.STAMINA_MASSIMA);
        valoreResistenza.setText("Resistenza: " + personaggio.getResistenza() + " / " + RisorseMatch.RESISTENZA_MASSIMA);
    }

    private void preparaTurno() {
        if (this.matchCorrente.isConcluso()) {
            this.gestisciFineMatch();
            return;
        }
        if (this.matchCorrente.isTurnoProtagonista())
            this.preparaAttaccoProtagonista();
        else
            this.preparaDifesaProtagonista();
    }

    private void preparaAttaccoProtagonista() {
        this.attaccoAvversarioCorrente = null;
        this.titoloAzioni.setText("SCEGLI L'ATTACCO!");
        this.impostaVisibilitaPulsante(this.pulsanteTiro, true);
        this.impostaVisibilitaPulsante(this.pulsanteDribbling, true);
        this.impostaVisibilitaPulsante(this.pulsanteParata, false);
        this.impostaVisibilitaPulsante(this.pulsanteContrasto, false);
        this.pulsanteTiro.setDisable(false);
        this.pulsanteDribbling.setDisable(false);
        this.aggiornaMenuTecniche();
    }

    private void preparaDifesaProtagonista() {
        this.attaccoAvversarioCorrente = this.controllerPartita.scegliAttaccoAvversario();
        this.titoloAzioni.setText(this.avversario.getNome() + " usa "
                + this.attaccoAvversarioCorrente.getNome() + " — SCEGLI LA DIFESA!");

        if (this.attaccoAvversarioCorrente instanceof TecnicaSupporto) {
            EsitoTurno esito = this.controllerPartita.eseguiTurnoAvversario(null);
            this.mostraEsitoTurno("Nessuna risposta", this.attaccoAvversarioCorrente.getNome(), esito);
            this.attaccoAvversarioCorrente = null;
            this.completaTurno();
            return;
        }

        this.impostaVisibilitaPulsante(this.pulsanteTiro, false);
        this.impostaVisibilitaPulsante(this.pulsanteDribbling, false);
        this.impostaVisibilitaPulsante(this.pulsanteParata, true);
        this.impostaVisibilitaPulsante(this.pulsanteContrasto, true);
        this.pulsanteParata.setDisable(!this.controllerPartita.isDifesaCompatibile(new Parata()));
        this.pulsanteContrasto.setDisable(!this.controllerPartita.isDifesaCompatibile(new Contrasto()));
        this.aggiornaMenuTecniche();
    }

    // Un pulsante non visibile viene anche escluso dal calcolo del layout,
    // così gli altri controlli possono occupare correttamente lo spazio rimasto.
    private void impostaVisibilitaPulsante(Button pulsante, boolean visibile) {
        pulsante.setVisible(visibile);
        pulsante.setManaged(visibile);
    }

    // Quando il protagonista attacca mostra quelle OFFENSIVE e di SUPPORTO;
    // quando difende, invece, fa controllare la compatibilità al controller.
    private void aggiornaMenuTecniche() {
        this.menuTecniche.getItems().clear();

        if (this.matchCorrente.isTurnoProtagonista()) {
            this.protagonista.getTecnichePerTipo(TipoTecnica.OFFENSIVA).forEach(this::aggiungiTecnicaAlMenu);
            this.protagonista.getTecnichePerTipo(TipoTecnica.SUPPORTO).forEach(this::aggiungiTecnicaAlMenu);
        }
        else {
            this.protagonista.getTecnichePerTipo(TipoTecnica.DIFENSIVA).forEach(this::aggiungiTecnicaAlMenu);
            this.protagonista.getTecnichePerTipo(TipoTecnica.SUPPORTO).stream()
                    .filter(tecnica -> this.controllerPartita.isDifesaCompatibile((AzioneDifensore) tecnica))
                    .forEach(this::aggiungiTecnicaAlMenu);
        }
        // L'intestazione del menù viene mostrata solo se c'è almeno una tecnica
        if (!this.menuTecniche.getItems().isEmpty())
            this.aggiungiIntestazioneMenuTecniche();
        this.menuTecniche.setDisable(this.menuTecniche.getItems().isEmpty());
    }

    private void aggiungiIntestazioneMenuTecniche() {
        Label nome = new Label("NOME");
        Label potenza = new Label("POTENZA");
        Label costo = new Label("COSTO");

        nome.setPrefWidth(150);
        potenza.setPrefWidth(70);
        costo.setPrefWidth(90);

        HBox intestazione = new HBox(12.0, nome, potenza, costo);
        intestazione.getStyleClass().add("intestazione-tecniche-menu");

        CustomMenuItem item = new CustomMenuItem(intestazione, false);
        this.menuTecniche.getItems().addFirst(item);
    }

    // Crea una voce personalizzata del CustomMenuItem mostrando
    // nome, potenza e costo in stamina della tecnica.
    private void aggiungiTecnicaAlMenu(TecnicaSpeciale tecnica) {
        Label nomeTecnica = new Label(tecnica.getNome());
        Label potenzaTecnica = new Label(tecnica.getPotenza() + "");
        Label costoStamina = new Label(tecnica.getCostoStamina() + "");

        nomeTecnica.setPrefWidth(150);
        potenzaTecnica.setPrefWidth(70);
        costoStamina.setPrefWidth(90);

        nomeTecnica.getStyleClass().add("nome-tecnica-menu");
        potenzaTecnica.getStyleClass().add("potenza-tecnica-menu");
        costoStamina.getStyleClass().add("costo-tecnica-menu");

        HBox contenuto = new HBox(12.0, nomeTecnica, potenzaTecnica, costoStamina);
        contenuto.getStyleClass().add("tecnica-menu");

        boolean disponibile = tecnica.isDisponibile(this.protagonista);
        CustomMenuItem item = new CustomMenuItem(contenuto, disponibile);
        if (disponibile)
            item.setOnAction(evento -> this.eseguiTecnica(tecnica));
        else {
            contenuto.setOpacity(0.5);
            Tooltip.install(contenuto, new Tooltip("Stamina insufficiente!"));
        }
        this.menuTecniche.getItems().add(item);
    }

    private void eseguiTecnica(TecnicaSpeciale tecnica) {
        if (this.matchCorrente.isTurnoProtagonista()) {
            if (!(tecnica instanceof AzioneAttaccante attacco))
                throw new IllegalArgumentException("La tecnica non può essere usata in attacco!");
            this.eseguiAttaccoProtagonista(attacco);
        }
        else {
            if (!(tecnica instanceof AzioneDifensore difesa))
                throw new IllegalArgumentException("La tecnica non può essere usata in difesa!");
            this.eseguiDifesaProtagonista(difesa);
        }
    }

    private void eseguiAttaccoProtagonista(AzioneAttaccante attacco) {
        AzioneDifensore difesa = this.controllerPartita.scegliDifesaAvversario(attacco);
        EsitoTurno esito = this.controllerPartita.eseguiTurnoProtagonista();
        String azioneAvversario = difesa == null ? "Nessuna risposta" : difesa.getNome();
        this.mostraEsitoTurno(attacco.getNome(), azioneAvversario, esito);
        this.completaTurno();
    }

    private void eseguiDifesaProtagonista(AzioneDifensore difesa) {
        AzioneAttaccante attacco = this.attaccoAvversarioCorrente;
        EsitoTurno esito = this.controllerPartita.eseguiTurnoAvversario(difesa);
        this.mostraEsitoTurno(difesa.getNome(), attacco.getNome(), esito);
        this.attaccoAvversarioCorrente = null;
        this.completaTurno();
    }

    private void completaTurno() {
        this.aggiornaStatoMatch();
        this.preparaTurno();
    }

    private void mostraEsitoTurno(String azioneProtagonista, String azioneAvversario, EsitoTurno esito) {
        this.azioneProtagonistaTurno.setText(azioneProtagonista);
        this.azioneAvversarioTurno.setText(azioneAvversario);
        this.esitoTurno.setText("Esito: " + this.descriviEsito(esito));
    }

    private String descriviEsito(EsitoTurno esito) {
        return switch (esito) {
            case DRIBBLING_RIUSCITO -> "Dribbling riuscito";
            case PALLA_PERSA -> "Palla persa";
            case ATTACCO_RESPINTO -> "Attacco respinto";
            case DANNO_INFLITTO -> "Danno inflitto";
            case STAMINA_RECUPERATA -> "Stamina recuperata";
            case GOL_SEGNATO -> "Gol segnato";
        };
    }

    private void gestisciFineMatch() {
        try {
            this.gestoreSchermate.mostraSchermata("esitoMatch");
        }
        catch (IOException e) {
            throw new IllegalStateException("Impossibile mostrare la schermata di esito del match", e);
        }
    }
}
