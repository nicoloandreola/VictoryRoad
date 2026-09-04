package it.unicam.cs.mpgc.rpg129542.controller;

import it.unicam.cs.mpgc.rpg129542.model.livello.Campo;
import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.livello.StatoAvversario;
import it.unicam.cs.mpgc.rpg129542.model.match.EsitoTurno;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
import it.unicam.cs.mpgc.rpg129542.model.match.Match;
import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneAttaccante;
import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneDifensore;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import it.unicam.cs.mpgc.rpg129542.persistence.Persistenza;
import it.unicam.cs.mpgc.rpg129542.persistence.salvataggio.SalvataggioDati;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Questa classe rappresenta il controller principale dell'applicazione ed è
 * responsabile del coordinamento tra model, persistenza e GUI.
 *
 * La classe mantiene lo stato della sessione di gioco corrente, costituito
 * principalmente dal protagonista scelto, dai livelli caricati, dal livello
 * corrente e dall'eventuale match in corso.
 *
 * Non contiene logica specifica delle singole entità del model: operazioni
 * come la selezione di un avversario, la risoluzione di un turno o la
 * registrazione di una vittoria vengono delegate rispettivamente a
 * {@link Livello} e {@link Match}. Il controller si limita a coordinare
 * tali operazioni nel corretto ordine.
 *
 * Analogamente, non conosce il formato utilizzato per memorizzare i dati,
 * poiché dipende esclusivamente dall'interfaccia {@link Persistenza},
 * cosicché un'implementazione differente della persistenza può essere
 * utilizzata senza modificare questa classe.
 *
 * Infine la classe è completamente indipendente da JavaFX: sarà la View
 * a invocarne i metodi in risposta alle azioni dell'utente e a utilizzare
 * i dati esposti dal controller per aggiornare l'interfaccia.
 *
 * @author Nicolò Andreola
 *
 */
public class ControllerGioco {

    /**
     * Limiti degli incrementi casuali applicati alle statistiche
     * del protagonista dopo il completamento di un livello.
     */
    private static final int INCREMENTO_MINIMO = 2;
    private static final int INCREMENTO_MASSIMO = 5;
    private final Persistenza persistenza;
    private final LogicaMatch logicaMatch;

    private Set<TecnicaSpeciale> tecnicheDisponibili;
    private List<Protagonista> protagonistiDisponibili;
    private List<Livello> livelli;

    @Getter
    private Protagonista protagonistaCorrente;
    @Getter
    private Livello livelloCorrente;
    @Getter
    private Match matchCorrente;

    /**
     * Crea il controller associandogli il sistema di persistenza e la
     * logica utilizzata per risolvere i match.
     *
     * @param persistenza sistema utilizzato per caricare e salvare i dati
     * @param logicaMatch logica utilizzata per risolvere i turni dei match
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     */
    public ControllerGioco(@NonNull Persistenza persistenza, @NonNull LogicaMatch logicaMatch) {
        this.persistenza = persistenza;
        this.logicaMatch = logicaMatch;
    }

    /**
     * Inizializza il controller caricando dalla persistenza tutti i dati
     * statici necessari al funzionamento del gioco: tecniche speciali,
     * protagonisti disponibili e livelli.
     *
     * Questo metodo deve essere chiamato all'avvio dell'applicazione,
     * prima di iniziare o caricare una partita.
     *
     * @throws IOException se si verifica un errore durante il caricamento
     *                     della configurazione iniziale
     */
    public void inizializza() throws IOException {
        this.tecnicheDisponibili = this.persistenza.caricaTecniche();
        this.protagonistiDisponibili = this.persistenza.caricaProtagonisti();
        this.livelli = this.persistenza.caricaLivelli();
        this.protagonistaCorrente = null;
        this.livelloCorrente = null;
        this.matchCorrente = null;
    }

    /**
     * Restituisce tutte le tecniche speciali disponibili nel gioco.
     *
     * @return insieme non modificabile delle tecniche disponibili
     */
    public Set<TecnicaSpeciale> getTecnicheDisponibili() {
        return Set.copyOf(this.tecnicheDisponibili);
    }

    /**
     * Restituisce i protagonisti tra cui il giocatore può scegliere
     * all'inizio di una nuova partita.
     *
     * @return lista non modificabile dei protagonisti disponibili
     */
    public List<Protagonista> getProtagonistiDisponibili() {
        return List.copyOf(this.protagonistiDisponibili);
    }

    /**
     * Restituisce tutti i livelli che compongono la campagna.
     *
     * @return lista non modificabile dei livelli
     */
    public List<Livello> getLivelli() {
        return List.copyOf(this.livelli);
    }

    /**
     * Avvia una nuova partita utilizzando il protagonista
     * identificato dall'id ricevuto.
     *
     * @param idProtagonista identificativo del protagonista scelto
     *
     * @throws NullPointerException se l'id è {@code null}
     *
     * @throws IllegalArgumentException se nessun protagonista
     *                                  disponibile possiede quell'id
     */
    public void iniziaNuovaPartita(@NonNull String idProtagonista) {
        this.protagonistaCorrente = this.trovaProtagonistaPerId(idProtagonista);
        this.livelloCorrente = this.livelli.getFirst();
    }

    /**
     * Ripristina una partita precedentemente salvata: a partire dai dati statici già
     * caricati vengono ricostruiti il protagonista scelto, le sue statistiche permanenti,
     * le tecniche imparate e gli avversari già sconfitti.
     *
     * @throws IOException se il salvataggio non è disponibile
     *                     o si verifica un errore durante la lettura
     */
    public void caricaPartitaSalvata() throws IOException {
        SalvataggioDati dati = this.persistenza.caricaPartita();
        Protagonista protagonista = this.trovaProtagonistaPerId(dati.getIdProtagonista());
        this.ripristinaDatiSalvati(protagonista, dati);
        this.protagonistaCorrente = protagonista;
        this.livelloCorrente = this.getMaxLivelloSbloccato();
        this.matchCorrente = null;
    }

    /**
     * Salva lo stato corrente della partita, comprendendo il protagonista, le sue
     * statistiche permanenti, le tecniche imparate e gli avversari già sconfitti.
     *
     * Non è possibile effettuare un salvataggio durante un match, poiché
     * le informazioni temporanee di una partita non fanno parte dei dati
     * memorizzati nel salvataggio.
     *
     * @throws IllegalStateException se non è presente una partita attiva
     *                               oppure è in corso un match
     *
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    public void salvaPartitaCorrente() throws IOException {
        if (this.protagonistaCorrente == null)
            throw new IllegalStateException("Nessuna partita attiva da salvare!");

        if (this.matchCorrente != null)
            throw new IllegalStateException("Non è possibile salvare durante un match!");

        Set<String> tecnicheImparate = this.protagonistaCorrente.getTecnicheSpeciali().stream()
                .map(TecnicaSpeciale::getId)
                .collect(Collectors.toSet());

        Set<String> avversariSconfitti = this.livelli.stream()
                .flatMap(livello -> livello.getAvversari().entrySet().stream())
                .filter(entry -> entry.getValue() == StatoAvversario.SCONFITTO)
                .map(entry -> entry.getKey().getId())
                .collect(Collectors.toSet());

        SalvataggioDati dati = new SalvataggioDati(this.protagonistaCorrente.getId(),
                this.protagonistaCorrente.getStatisticheBase(), tecnicheImparate, avversariSconfitti);

        this.persistenza.salvaPartita(dati);
    }

    /**
     * Verifica se il giocatore ha completato l'intero gioco.
     *
     * @return {@code true} se tutti i livelli sono completati,
     *         {@code false} altrimenti
     */
    public boolean isGiocoCompletato() {
        return this.livelli.stream().allMatch(Livello::isCompletato);
    }

    /**
     * Seleziona uno dei livelli attualmente accessibili.
     *
     * Il primo livello è sempre disponibile, mentre ciascun livello
     * successivo può essere selezionato solamente se nel precedente
     * è stato sbloccato l'avanzamento.
     *
     * È sempre possibile tornare a un livello precedente già sbloccato,
     * ad esempio per affrontare l'avversario non ancora sconfitto.
     *
     * @param livello livello che il giocatore vuole affrontare
     *
     * @throws NullPointerException se il livello è {@code null}
     *
     * @throws IllegalArgumentException se il livello non appartiene alla campagna
     *
     * @throws IllegalStateException se il livello non è ancora accessibile
     *                               oppure è presente un match in corso
     */
    public void selezionaLivello(@NonNull Livello livello) {
        if (this.matchCorrente != null)
            throw new IllegalStateException("Non è possibile cambiare livello durante un match!");

        int indice = this.livelli.indexOf(livello);

        if (indice < 0)
            throw new IllegalArgumentException("Il livello non appartiene alla campagna!");

        if (indice > 0 && !this.livelli.get(indice - 1).isLivelloSuccessivoSbloccato())
            throw new IllegalStateException("Il livello non è ancora disponibile!");

        this.livelloCorrente = livello;
    }

    /**
     * Seleziona, all'interno del livello corrente, l'avversario che il
     * protagonista intende affrontare nel prossimo match.
     *
     * @param avversario avversario scelto dal giocatore
     *
     * @throws NullPointerException se l'avversario è {@code null}
     *
     * @throws IllegalArgumentException se l'avversario non appartiene
     *                                  al livello corrente
     *
     * @throws IllegalStateException se l'avversario è già stato sconfitto
     */
    public void selezionaAvversario(@NonNull Avversario avversario) {
        this.livelloCorrente.selezionaAvversario(avversario);
    }

    /**
     * Avvia un match tra il protagonista e l'avversario del livello corrente scelto.
     *
     * Prima della creazione del match viene applicato temporaneamente
     * a entrambi i personaggi il modificatore associato al campo
     * tramite il metodo {@link #applicaEffettoCampo(Campo, Avversario)}.
     *
     * @throws IllegalStateException se non è presente una partita attiva,
     *                               è già in corso un match oppure nessun
     *                               avversario è stato selezionato
     */
    public void iniziaMatch() {
        if (this.protagonistaCorrente == null)
            throw new IllegalStateException("Nessuna partita attiva!");

        if (this.matchCorrente != null)
            throw new IllegalStateException("È già presente un match in corso!");

        Livello livello = this.getLivelloCorrente();
        Avversario avversario = livello.getAvversarioSelezionato();
        this.applicaEffettoCampo(livello.getCampo(), avversario);
        this.matchCorrente = new Match(this.protagonistaCorrente, avversario, this.logicaMatch);
    }

    /**
     * Esegue un turno del match corrente delegandone la risoluzione alla classe {@link Match}.
     *
     * @param attacco azione scelta dall'attaccante
     * @param difesa risposta scelta dal difensore
     *
     * @return esito prodotto dal turno
     *
     * @throws IllegalStateException se non è presente alcun match
     */
    public EsitoTurno eseguiTurno(@NonNull AzioneAttaccante attacco, AzioneDifensore difesa) {
        if (this.matchCorrente == null)
            throw new IllegalStateException("Nessun match in corso!");
        return this.matchCorrente.giocaTurno(attacco, difesa);
    }

    /**
     * Termina il match corrente rimuovendo gli effetti temporanei del campo
     * e aggiornando, in caso di vittoria, la progressione del giocatore.
     *
     * Dopo una vittoria l'avversario viene registrato come sconfitto,
     * l'eventuale tecnica scelta viene aggiunta al protagonista e, se con
     * tale vittoria risultano sconfitti entrambi gli avversari del livello,
     * le statistiche del protagonista vengono migliorate permanentemente.
     *
     * Per migliorare le statistiche il metodo usa {@link #miglioraStatisticheFineLivello()}
     * che applica a ciascuna un incremento casuale indipendente
     * compreso tra {@value #INCREMENTO_MINIMO} e {@value #INCREMENTO_MASSIMO}.
     *
     *
     * @param tecnicaRicompensa tecnica scelta dal giocatore dopo una
     *                          vittoria, oppure {@code null} se non sono
     *                          presenti tecniche apprendibili o in caso
     *                          di sconfitta
     *
     * @throws IllegalStateException se non è presente un match
     *                               oppure il match non è concluso
     *
     * @throws IllegalArgumentException se la tecnica indicata non appartiene
     *                                  alle possibili ricompense
     */
    public void terminaMatch(TecnicaSpeciale tecnicaRicompensa) {
        if (this.matchCorrente == null)
            throw new IllegalStateException("Nessun match in corso!");

        this.gestisciRicompensa(tecnicaRicompensa);
        Avversario avversario = this.matchCorrente.getAvversario();

        if(this.isVittoriaProtagonista()) {
            this.livelloCorrente.registraVittoria(avversario);
            if (this.livelloCorrente.isCompletato())
                this.miglioraStatisticheFineLivello();
        }

        this.protagonistaCorrente.getGestoreStatistiche().rimuoviModificatore();
        avversario.getGestoreStatistiche().rimuoviModificatore();
        this.matchCorrente = null;
    }

    /**
     * Restituisce le tecniche possedute dall'avversario sconfitto
     * che il protagonista non possiede ancora e che possono quindi
     * essere scelte come ricompensa.
     *
     * @return insieme delle tecniche selezionabili come ricompensa
     *
     * @throws IllegalStateException se non è presente un match concluso
     *                               con la vittoria del protagonista
     */
    public Set<TecnicaSpeciale> getTecnicheRicompensa() {
        if (this.matchCorrente == null || !this.matchCorrente.isConcluso() || !this.isVittoriaProtagonista())
            throw new IllegalStateException("Le tecniche possono essere scelte solamente dopo una vittoria!");
        return this.calcolaTecnicheRicompensa();
    }

    private void applicaEffettoCampo(Campo campo, Avversario avversario) {
        this.protagonistaCorrente.getGestoreStatistiche().applicaModificatore(campo.getModificatore());
        avversario.getGestoreStatistiche().applicaModificatore(campo.getModificatore());
    }

    private boolean isVittoriaProtagonista() {
        if (this.matchCorrente == null)
            throw new IllegalStateException("Nessun match in corso!");
        return this.matchCorrente.getVincitore().equals(this.protagonistaCorrente);
    }

    private Set<TecnicaSpeciale> calcolaTecnicheRicompensa() {
        return this.matchCorrente.getAvversario()
                .getTecnicheSpeciali()
                .stream()
                .filter(tecnica -> !this.protagonistaCorrente.possiedeTecnica(tecnica))
                .collect(Collectors.toSet());
    }

    private void gestisciRicompensa(TecnicaSpeciale tecnicaRicompensa) {
        boolean vittoria = this.isVittoriaProtagonista();
        Set<TecnicaSpeciale> tecnicheRicompensa = this.calcolaTecnicheRicompensa();

        if (tecnicaRicompensa == null) {
            // Unici casi in cui tecnicaRicompensa deve essere null
            if (vittoria && !tecnicheRicompensa.isEmpty())
                throw new IllegalArgumentException("È necessario scegliere una tecnica da imparare!");
        }
        else {
            // Una tecnica può essere ottenuta solamente dopo una vittoria.
            if (!vittoria)
                throw new IllegalArgumentException("Non è possibile imparare una tecnica dopo una sconfitta!");

            // La tecnica deve appartenere alle ricompense disponibili.
            if (!tecnicheRicompensa.contains(tecnicaRicompensa))
                throw new IllegalArgumentException("La tecnica scelta non è una ricompensa valida!");

            this.protagonistaCorrente.imparaTecnica(tecnicaRicompensa);
        }
    }

    private void miglioraStatisticheFineLivello() {
        Random random = new Random();
        int attacco = random.nextInt(INCREMENTO_MINIMO, INCREMENTO_MASSIMO + 1);
        int difesa = random.nextInt(INCREMENTO_MINIMO, INCREMENTO_MASSIMO + 1);
        int agilita = random.nextInt(INCREMENTO_MINIMO, INCREMENTO_MASSIMO + 1);
        this.protagonistaCorrente.miglioraStatistiche(attacco, difesa, agilita);
    }

    // Restituisce il livello più avanzato attualmente sbloccato,
    // dal quale il giocatore può riprendere a giocare.
    private Livello getMaxLivelloSbloccato() {
        Livello livelloSbloccato = this.livelli.getFirst();
        for (int i = 1; i < this.livelli.size(); i++) {
            if (!this.livelli.get(i - 1).isLivelloSuccessivoSbloccato())
                break;
            livelloSbloccato = this.livelli.get(i);
        }
        return livelloSbloccato;
    }

    // Applica al protagonista e ai livelli i dati dinamici contenuti nel salvataggio:
    // statistiche permanenti, tecniche imparate e avversari già sconfitti.
    private void ripristinaDatiSalvati(Protagonista protagonista, SalvataggioDati dati) {
        this.ripristinaStatistiche(protagonista, dati);
        this.ripristinaTecniche(protagonista, dati);
        this.ripristinaAvversariSconfitti(dati);
    }

    private void ripristinaStatistiche(Protagonista protagonista, SalvataggioDati dati) {
        StatisticheBase attuali = protagonista.getStatisticheBase();
        StatisticheBase salvate = dati.getStatisticheProtagonista();
        int deltaAttacco = salvate.getAttacco() - attuali.getAttacco();
        int deltaDifesa = salvate.getDifesa() - attuali.getDifesa();
        int deltaAgilita = salvate.getAgilita() - attuali.getAgilita();
        protagonista.miglioraStatistiche(deltaAttacco, deltaDifesa, deltaAgilita);
    }

    private void ripristinaTecniche(Protagonista protagonista, SalvataggioDati dati) {
        for (String idTecnica : dati.getTecnicheImparate()) {
            TecnicaSpeciale tecnica = this.tecnicheDisponibili.stream()
                    .filter(t -> t.getId().equals(idTecnica))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Tecnica non trovata: " + idTecnica));
            protagonista.imparaTecnica(tecnica);
        }
    }

    private void ripristinaAvversariSconfitti(SalvataggioDati dati) {
        Set<String> avversariDaRipristinare = new HashSet<>(dati.getAvversariSconfitti());
        for (Livello livello : this.livelli)
            for (Avversario avversario : livello.getAvversari().keySet())
                if (avversariDaRipristinare.remove(avversario.getId())) {
                    livello.selezionaAvversario(avversario);
                    livello.registraVittoria(avversario);
                }
        if (!avversariDaRipristinare.isEmpty())
            throw new IllegalArgumentException("Il salvataggio contiene avversari inesistenti!");
    }

    private Protagonista trovaProtagonistaPerId(String id) {
        return this.protagonistiDisponibili.stream()
                .filter(protagonista -> protagonista.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nessun protagonista trovato con id: " + id));
    }
}
