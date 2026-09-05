package it.unicam.cs.mpgc.rpg129542.controller;

import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.livello.StatoAvversario;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Questa classe rappresenta il controller principale dell'applicazione ed è
 * responsabile del coordinamento tra model, persistenza e GUI.
 *
 * La classe carica i dati statici necessari all'applicazione, permette
 * di avviare una nuova partita o ripristinarne una precedentemente
 * salvata e coordina il salvataggio della progressione corrente.
 * La gestione della partita una volta avviata non appartiene direttamente
 * a questa classe, ma viene delegata a {@link ControllerPartita}, che
 * coordina livelli, avversari, match e ricompense.
 *
 * Inoltre non conosce il formato utilizzato per memorizzare i dati,
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

    private final Persistenza persistenza;
    private final LogicaMatch logicaMatch;
    private final StrategiaAvversario strategiaAvversario;

    private Set<TecnicaSpeciale> tecniche;
    private List<Protagonista> protagonisti;
    private List<Livello> livelli;

    @Getter
    private ControllerPartita partitaCorrente;

    /**
     * Crea il controller associandogli il sistema di persistenza, la
     * logica utilizzata per risolvere i match e la strategia utilizzata
     * per scegliere automaticamente le azioni degli avversari.
     *
     * @param persistenza sistema utilizzato per caricare e salvare i dati
     * @param logicaMatch logica utilizzata per risolvere i turni dei match
     * @param strategiaAvversario regole usate per determinare le azioni degli avversari
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     */
    public ControllerGioco(@NonNull Persistenza persistenza, @NonNull LogicaMatch logicaMatch,
                           @NonNull StrategiaAvversario strategiaAvversario) {
        this.persistenza = persistenza;
        this.logicaMatch = logicaMatch;
        this.strategiaAvversario = strategiaAvversario;
    }

    /**
     * Restituisce tutte le tecniche speciali disponibili nel gioco.
     *
     * @return insieme non modificabile delle tecniche disponibili
     */
    public Set<TecnicaSpeciale> getTecniche() {
        return Set.copyOf(this.tecniche);
    }

    /**
     * Restituisce i protagonisti tra cui il giocatore può scegliere
     * all'inizio di una nuova partita.
     *
     * @return lista non modificabile dei protagonisti disponibili
     */
    public List<Protagonista> getProtagonisti() {
        return List.copyOf(this.protagonisti);
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
        this.tecniche = this.persistenza.caricaTecniche();
        this.protagonisti = this.persistenza.caricaProtagonisti();
        this.livelli = this.persistenza.caricaLivelli();
        this.partitaCorrente = null;
    }

    /**
     * Verifica se è presente una partita precedentemente salvata.
     *
     * @return {@code true} se è disponibile un salvataggio, {@code false} altrimenti
     */
    public boolean haSalvataggioDisponibile() {
        return this.persistenza.verificaSalvataggio();
    }

    /**
     * Avvia una nuova partita utilizzando il protagonista
     * identificato dall'id ricevuto. Il protagonista selezionato
     * e i livelli caricati vengono utilizzati per creare un nuovo
     * {@link ControllerPartita}, che gestirà la sessione di gioco.
     *
     * @param idProtagonista identificativo del protagonista scelto
     *
     * @throws NullPointerException se l'id è {@code null}
     *
     * @throws IllegalArgumentException se nessun protagonista
     *                                  disponibile possiede quell'id
     */
    public void iniziaNuovaPartita(@NonNull String idProtagonista) throws IOException{
        this.ricaricaDatiPartita();
        Protagonista protagonista = this.trovaProtagonistaPerId(idProtagonista);
        this.partitaCorrente = new ControllerPartita(protagonista, this.livelli,
                this.logicaMatch, this.strategiaAvversario);
    }

    /**
     * Ripristina una partita precedentemente salvata: a partire dai dati statici già
     * caricati vengono ricostruiti il protagonista scelto, le sue statistiche permanenti,
     * le tecniche imparate e gli avversari già sconfitti.
     *
     * Terminato il ripristino, viene creato un nuovo {@link ControllerPartita}, che
     * individua automaticamente il livello più avanzato attualmente sbloccato.
     *
     * @throws IOException se il salvataggio non è disponibile
     *                     o si verifica un errore durante la lettura
     */
    public void caricaPartitaSalvata() throws IOException {
        SalvataggioDati dati = this.persistenza.caricaPartita();
        this.ricaricaDatiPartita();
        Protagonista protagonista = this.trovaProtagonistaPerId(dati.getIdProtagonista());
        this.ripristinaDatiSalvati(protagonista, dati);
        this.partitaCorrente = new ControllerPartita(protagonista, this.livelli,
                this.logicaMatch, this.strategiaAvversario);
    }

    // Ricarica protagonisti e livelli dalla configurazione iniziale prima di
    // avviare o ripristinare una partita, evitando di riutilizzare oggetti
    // eventualmente modificati da una sessione precedente nella stessa esecuzione.
    private void ricaricaDatiPartita() throws IOException {
        this.protagonisti = this.persistenza.caricaProtagonisti();
        this.livelli = this.persistenza.caricaLivelli();
    }

    /**
     * Salva lo stato corrente della partita in accordo con {@link SalvataggioDati},
     * comprendendo il protagonista, le sue statistiche permanenti, le tecniche
     * imparate e gli avversari già sconfitti.
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
        if (this.partitaCorrente == null)
            throw new IllegalStateException("Nessuna partita attiva da salvare!");

        if (this.partitaCorrente.isMatchInCorso())
            throw new IllegalStateException("Non è possibile salvare durante un match!");

        Protagonista protagonista = this.partitaCorrente.getProtagonistaCorrente();

        Set<String> tecnicheImparate = protagonista.getTecnicheSpeciali().stream()
                .map(TecnicaSpeciale::getId)
                .collect(Collectors.toSet());

        Set<String> avversariSconfitti = this.livelli.stream()
                .flatMap(livello -> livello.getAvversari().entrySet().stream())
                .filter(entry -> entry.getValue() == StatoAvversario.SCONFITTO)
                .map(entry -> entry.getKey().getId())
                .collect(Collectors.toSet());

        SalvataggioDati dati = new SalvataggioDati(protagonista.getId(),
                protagonista.getStatisticheBase(), tecnicheImparate, avversariSconfitti);

        this.persistenza.salvaPartita(dati);
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
            TecnicaSpeciale tecnica = this.tecniche.stream()
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
        return this.protagonisti.stream()
                .filter(protagonista -> protagonista.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nessun protagonista trovato con id: " + id));
    }
}
