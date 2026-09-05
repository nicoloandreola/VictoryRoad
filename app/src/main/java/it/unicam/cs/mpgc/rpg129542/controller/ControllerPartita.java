package it.unicam.cs.mpgc.rpg129542.controller;

import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneAttaccante;
import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneDifensore;
import it.unicam.cs.mpgc.rpg129542.model.livello.Campo;
import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.match.EsitoTurno;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
import it.unicam.cs.mpgc.rpg129542.model.match.Match;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller responsabile della gestione di una partita già avviata:
 * mantiene lo stato dinamico della sessione di gioco costituito
 * dal protagonista utilizzato, dal livello attualmente selezionato e
 * dall'eventuale match in corso.
 *
 * Le sue responsabilità principali sono il coordinamento della progressione tra
 * i livelli, la selezione degli avversari, l'avvio e la conclusione dei match,
 * l'assegnazione delle tecniche ottenute come ricompensa e il miglioramento
 * permanente delle statistiche dopo il completamento di un livello.
 *
 * La logica specifica delle singole operazioni rimane delegata agli oggetti
 * del model: {@link Livello} gestisce lo stato dei propri avversari,
 * {@link Match} coordina i turni e {@link Protagonista} gestisce
 * l'apprendimento delle tecniche e il miglioramento delle statistiche.
 *
 * Infine, oltre a non contenere dipendenze da JavaFX come {@link ControllerGioco},
 * questa classe è completamente indipendente anche dal sistema di persistenza,
 * poiché la creazione, il caricamento e il salvataggio di una partita rimangono
 * responsabilità esclusive di {@link ControllerGioco}.
 *
 * @author Nicolò Andreola
 */
public class ControllerPartita {


    // Limiti degli incrementi casuali applicati alle statistiche
    // del protagonista dopo il completamento di un livello.
    private static final int INCREMENTO_MINIMO = 2;
    private static final int INCREMENTO_MASSIMO = 5;

    private final LogicaMatch logicaMatch;
    private final List<Livello> livelli;
    private final StrategiaAvversario strategiaAvversario;

    @Getter
    private final Protagonista protagonistaCorrente;
    @Getter
    private Livello livelloCorrente;
    @Getter
    private Match matchCorrente;

    private AzioneAttaccante attaccoTurno;
    private AzioneDifensore difesaTurno;

    /**
     * Crea il controller di una partita associandogli il protagonista,
     * i livelli del gioco, la logica utilizzata nei match e la strategia
     * utilizzata per determinare le azioni degli avversari.
     *
     * Il livello corrente viene inizializzato automaticamente al livello
     * più avanzato attualmente sbloccato (in una nuova partita questo
     * corrisponde al primo livello).
     *
     * @param protagonistaCorrente protagonista controllato dal giocatore
     * @param livelli livelli che compongono il gioco
     * @param logicaMatch logica utilizzata per risolvere i turni dei match
     * @param strategiaAvversario regole usate per scegliere le azioni dell'avversario
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     */
    public ControllerPartita(@NonNull Protagonista protagonistaCorrente, @NonNull List<Livello> livelli,
                             @NonNull LogicaMatch logicaMatch, @NonNull StrategiaAvversario strategiaAvversario) {
        this.protagonistaCorrente = protagonistaCorrente;
        this.livelli = livelli;
        this.logicaMatch = logicaMatch;
        this.strategiaAvversario = strategiaAvversario;
        this.livelloCorrente = this.getMaxLivelloSbloccato();
        this.matchCorrente = null;
    }

    /**
     * Verifica se il giocatore ha completato l'intero gioco.
     *
     * Il gioco è completato solamente quando tutti i livelli risultano
     * completati, quindi quando tutti gli avversari del gioco sono stati sconfitti.
     *
     * @return {@code true} se tutti i livelli sono completati,
     *         {@code false} altrimenti
     */
    public boolean isGiocoCompletato() {
        return this.livelli.stream().allMatch(Livello::isCompletato);
    }

    /**
     * Restituisce tutti i livelli che compongono il gioco.
     *
     * @return lista non modificabile dei livelli
     */
    public List<Livello> getLivelli() {
        return List.copyOf(this.livelli);
    }

    /**
     * Seleziona uno dei livelli attualmente accessibili.
     *
     * Il primo livello è sempre disponibile, mentre ciascun livello
     * successivo può essere selezionato solamente se nel precedente
     * è stato sbloccato l'avanzamento.
     *
     * È sempre possibile tornare a un livello precedente già sbloccato,
     * ad esempio per affrontare l'avversario non ancora sconfitto e
     * completare interamente il livello.
     *
     * @param livello livello che il giocatore vuole affrontare
     *
     * @throws NullPointerException se il livello è {@code null}
     *
     * @throws IllegalArgumentException se il livello non ha un indice valido
     *
     * @throws IllegalStateException se il livello non è ancora accessibile
     *                               oppure è presente un match in corso
     */
    public void selezionaLivello(@NonNull Livello livello) {
        if (this.isMatchInCorso())
            throw new IllegalStateException("Non è possibile cambiare livello durante un match!");

        int indice = this.livelli.indexOf(livello);

        if (indice < 0)
            throw new IllegalArgumentException("Il livello non esiste!");

        if (indice > 0 && !this.livelli.get(indice - 1).isLivelloSuccessivoSbloccato())
            throw new IllegalStateException("Il livello non è ancora disponibile!");

        this.livelloCorrente = livello;
    }

    /**
     * Seleziona, all'interno del livello corrente, l'avversario
     * che il protagonista intende affrontare nel prossimo match.
     *
     * @param avversario avversario scelto dal giocatore
     *
     * @throws NullPointerException se l'avversario è {@code null}
     *
     * @throws IllegalArgumentException se l'avversario non appartiene
     *                                  al livello corrente
     *
     * @throws IllegalStateException se l'avversario è già stato sconfitto o
     *                               è già in corso un match
     */
    public void selezionaAvversario(@NonNull Avversario avversario) {
        if (this.isMatchInCorso())
            throw new IllegalStateException("Non è possibile cambiare avversario durante un match!");
        this.livelloCorrente.selezionaAvversario(avversario);
    }

    /**
     * Avvia un match tra il protagonista e l'avversario scelto nel livello corrente.
     *
     * Prima della creazione del match viene applicato temporaneamente
     * a entrambi i personaggi il modificatore associato al campo
     * tramite il metodo {@link #applicaEffettoCampo(Campo, Avversario)}.
     *
     * @throws IllegalStateException se è già presente un match in corso
     *                               oppure nessun avversario è stato selezionato
     */
    public void iniziaMatch() {
        if (this.isMatchInCorso())
            throw new IllegalStateException("È già presente un match in corso!");
        Livello livello = this.getLivelloCorrente();
        Avversario avversario = livello.getAvversarioSelezionato();
        this.attaccoTurno = null;
        this.difesaTurno = null;
        this.applicaEffettoCampo(livello.getCampo(), avversario);
        this.matchCorrente = new Match(this.protagonistaCorrente, avversario, this.logicaMatch);
    }

    /**
     * Chiede a {@link StrategiaAvversario} la difesa che l'avversario
     * opporrà all'attacco scelto dal protagonista.
     *
     * L'attacco e la difesa selezionata vengono memorizzati fino alla
     * successiva esecuzione del turno, permettendo alla View di mostrare
     * la scelta dell'avversario prima della risoluzione.
     *
     * @param attacco azione scelta dal protagonista
     *
     * @return difesa scelta automaticamente dall'avversario,
     *         oppure {@code null} se l'attacco non richiede risposta
     *
     * @throws NullPointerException se l'attacco è {@code null}
     *
     * @throws IllegalStateException se non è presente un match in corso
     *                               oppure il protagonista non è
     *                               l'attaccante corrente
     */
    public AzioneDifensore scegliDifesaAvversario(@NonNull AzioneAttaccante attacco) {
        if (!this.isMatchInCorso())
            throw new IllegalStateException("Nessun match in corso!");

        if (!this.matchCorrente.isTurnoProtagonista())
            throw new IllegalStateException("Il protagonista non è l'attaccante corrente!");

        Avversario avversario = this.matchCorrente.getAvversario();

        this.attaccoTurno = attacco;
        this.difesaTurno = this.strategiaAvversario.determinaDifesa(avversario, attacco);

        return this.difesaTurno;
    }

    /**
     * Esegue il turno precedentemente scelto dal metodo
     * {@link #scegliDifesaAvversario(AzioneAttaccante)}, nel quale
     * il protagonista ricopre il ruolo di attaccante.
     *
     * @return esito prodotto dal turno
     *
     * @throws IllegalStateException se non è presente un match in corso
     *                               oppure la difesa dell'avversario
     *                               non è ancora stata scelta
     */
    public EsitoTurno eseguiAttaccoProtagonista() {
        if (!this.isMatchInCorso())
            throw new IllegalStateException("Nessun match in corso!");

        if (this.attaccoTurno == null)
            throw new IllegalStateException("La risposta dell'avversario non è ancora stata determinata!");

        EsitoTurno esito = this.matchCorrente.giocaTurno(this.attaccoTurno, this.difesaTurno);
        this.attaccoTurno = null;
        this.difesaTurno = null;
        return esito;
    }

    /**
     * Chiede a {@link StrategiaAvversario} l'attacco che verrà eseguito
     * dall'avversario nel turno corrente.
     *
     * L'azione scelta viene memorizzata fino all'esecuzione del turno,
     * così che possa essere mostrata al giocatore prima della scelta
     * della propria risposta difensiva.
     *
     * Chiamate successive effettuate nello stesso turno restituiscono
     * la stessa azione già selezionata, evitando un nuovo sorteggio.
     *
     * @return attacco scelto dall'avversario
     *
     * @throws IllegalStateException se non è presente un match in corso
     *                               oppure il protagonista è
     *                               l'attaccante corrente
     */
    public AzioneAttaccante scegliAttaccoAvversario() {
        if (!this.isMatchInCorso())
            throw new IllegalStateException("Nessun match in corso!");

        if (this.matchCorrente.isTurnoProtagonista())
            throw new IllegalStateException("L'avversario non è l'attaccante corrente!");

        if (this.attaccoTurno == null) {
            Avversario avversario = this.matchCorrente.getAvversario();
            this.attaccoTurno = this.strategiaAvversario.determinaAttacco(avversario);
        }
        return this.attaccoTurno;
    }

    /**
     * Esegue il turno dell'avversario utilizzando l'attacco
     * precedentemente scelto da {@link #scegliAttaccoAvversario()}
     * e la risposta scelta dal protagonista.
     *
     * Dopo la corretta esecuzione del turno l'attacco memorizzato viene
     * eliminato, così che un eventuale turno successivo dell'avversario
     * richieda una nuova scelta da parte della strategia.
     *
     * Il parametro {@code difesa} può essere {@code null} quando
     * l'attacco scelto dall'avversario non richiede alcuna risposta.
     *
     * @param difesa risposta scelta dal protagonista, oppure {@code null}
     *               se non richiesta dall'attacco
     *
     * @return esito prodotto dal turno
     *
     * @throws IllegalStateException se non è presente un match in corso,
     *                               se il protagonista è l'attaccante corrente
     *                               oppure se l'attacco dell'avversario non è
     *                               ancora stato determinato
     */
    public EsitoTurno eseguiAttaccoAvversario(AzioneDifensore difesa) {
        if (!this.isMatchInCorso())
            throw new IllegalStateException("Nessun match in corso!");

        if (this.matchCorrente.isTurnoProtagonista())
            throw new IllegalStateException("L'avversario non è l'attaccante corrente!");

        if (this.attaccoTurno == null)
            throw new IllegalStateException("L'attacco dell'avversario non è ancora stato scelto!");

        EsitoTurno esito = this.matchCorrente.giocaTurno(this.attaccoTurno, difesa);
        this.attaccoTurno = null;
        return esito;
    }

    /**
     * Termina il match corrente rimuovendo gli effetti temporanei del campo
     * e aggiornando, in caso di vittoria, la progressione del giocatore.
     *
     * Dopo una vittoria l'avversario viene registrato come sconfitto.
     * Se con tale vittoria risultano sconfitti entrambi gli avversari
     * del livello, le statistiche del protagonista vengono migliorate
     * permanentemente.
     *
     * Prima di modificare la progressione viene verificata e gestita
     * l'eventuale tecnica scelta come ricompensa tramite il metodo
     * privato {@link #gestisciRicompensa(TecnicaSpeciale)}
     *
     * Gli incrementi delle tre statistiche sono indipendenti e generati
     * casualmente tra {@value #INCREMENTO_MINIMO} e {@value #INCREMENTO_MASSIMO}
     * dal metodo privato {@link #miglioraStatisticheFineLivello()}.
     *
     * @param tecnicaRicompensa tecnica scelta dopo una vittoria,
     *                          oppure {@code null} se non sono disponibili
     *                          nuove tecniche o in caso di sconfitta
     *
     * @throws IllegalStateException se nessun match è in corso
     *                               oppure il match non è ancora concluso
     *
     * @throws IllegalArgumentException se la tecnica indicata non rappresenta
     *                                  una ricompensa valida
     */
    public void terminaMatch(TecnicaSpeciale tecnicaRicompensa) {
        if (!this.isMatchInCorso())
            throw new IllegalStateException("Nessun match in corso!");

        if(!this.matchCorrente.isConcluso())
            throw new IllegalStateException("Il match non è ancora terminato!");

        this.gestisciRicompensa(tecnicaRicompensa);
        Avversario avversario = this.matchCorrente.getAvversario();

        if(this.isVittoriaProtagonista()) {
            this.livelloCorrente.registraVittoria(avversario);
            if (this.livelloCorrente.isCompletato())
                this.miglioraStatisticheFineLivello();
        }

        this.protagonistaCorrente.getGestoreStatistiche().rimuoviModificatore();
        avversario.getGestoreStatistiche().rimuoviModificatore();
        this.attaccoTurno = null;
        this.difesaTurno = null;
        this.matchCorrente = null;
    }

    /**
     * Verifica se è attualmente presente un match in corso.
     *
     * @return {@code true} se esiste un match corrente,
     *         {@code false} altrimenti
     */
    public boolean isMatchInCorso() {
        return this.matchCorrente != null;
    }

    /**
     * Restituisce le tecniche possedute dall'avversario sconfitto
     * che il protagonista non possiede ancora e che possono quindi
     * essere scelte come ricompensa.
     *
     * Il metodo costituisce l'operazione pubblica attraverso la quale
     * la View può conoscere le tecniche da mostrare al giocatore
     * dopo la vittoria di un match.
     *
     * @return insieme delle tecniche selezionabili come ricompensa
     *
     * @throws IllegalStateException se non è presente un match concluso
     *                               con la vittoria del protagonista
     */
    public Set<TecnicaSpeciale> getTecnicheRicompensa() {
        if (!this.isMatchInCorso() || !this.matchCorrente.isConcluso() || !this.isVittoriaProtagonista())
            throw new IllegalStateException("Le tecniche possono essere scelte solamente dopo una vittoria!");
        return this.calcolaTecnicheRicompensa();
    }

    private void applicaEffettoCampo(Campo campo, Avversario avversario) {
        this.protagonistaCorrente.getGestoreStatistiche().applicaModificatore(campo.getModificatore());
        avversario.getGestoreStatistiche().applicaModificatore(campo.getModificatore());
    }

    private boolean isVittoriaProtagonista() {
        if (!this.isMatchInCorso())
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
}
