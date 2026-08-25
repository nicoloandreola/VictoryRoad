package it.unicam.cs.mpgc.rpg129542.model.match;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import lombok.Getter;
import lombok.NonNull;

import java.util.Random;

/**
 * Rappresenta e coordina un match tra un {@link Protagonista} e un {@link Avversario}.
 *
 * La classe mantiene lo stato della partita occupandosi del punteggio,
 * del possesso, dell'alternanza dei turni, del ripristino delle risorse
 * dopo un gol e della determinazione del vincitore.
 *
 * La risoluzione delle singole azioni non viene effettuata direttamente
 * da questa classe, ma è delegata a un'implementazione di {@link LogicaMatch}.
 * In questo modo la gestione dello stato del match rimane separata dalle
 * specifiche regole utilizzate per calcolare gli esiti di ogni turno.
 *
 * Il match termina quando uno dei due personaggi raggiunge {@value #GOL_PER_VITTORIA} gol.
 *
 * @author Nicolò Andreola
 */
@Getter
public class Match {

    /**
     * Numero di gol necessari per vincere un match
     */
    public static final int GOL_PER_VITTORIA = 3;

    private final Protagonista protagonista;
    private final Avversario avversario;
    private Personaggio attaccanteCorrente;
    private final LogicaMatch logicaMatch;
    private int golProtagonista;
    private int golAvversario;

    /**
     * Crea un nuovo match inizializzando a 0 i gol dei due personaggi e scegliendo
     * casualmente chi dei due inizia ad attaccare con il metodo privato
     * {@link #sorteggiaPersonaggioIniziale()} che simula il lancio di una moneta.
     *
     * Inoltre, ripristina completamente le risorse dei due personaggi prima dell'inizio.
     *
     * @param protagonista personaggio controllato dal giocatore
     * @param avversario   personaggio controllato dal gioco
     * @throws NullPointerException se uno dei due parametri passati è {@code null}
     */
    public Match(@NonNull Protagonista protagonista, @NonNull Avversario avversario, @NonNull LogicaMatch logicaMatch) {
        this.protagonista = protagonista;
        this.avversario = avversario;
        this.golProtagonista = 0;
        this.golAvversario = 0;
        this.logicaMatch = logicaMatch;
        this.attaccanteCorrente = sorteggiaPersonaggioIniziale();

        this.protagonista.ripristinaRisorseMatch();
        this.avversario.ripristinaRisorseMatch();
    }

    // Sorteggia casualmente il personaggio che eseguirà la prima azione offensiva del match.
    private Personaggio sorteggiaPersonaggioIniziale() {
        Random random = new Random();
        boolean testa = random.nextBoolean();
        return testa ? this.protagonista : this.avversario;
    }

    /**
     * Esegue un turno del match utilizzando le azioni e le eventuali
     * tecniche selezionate.
     *
     * Prima della risoluzione verifica che il match non sia già concluso
     * e che le eventuali tecniche selezionate siano possedute dai rispettivi
     * personaggi. La risoluzione delle azioni viene quindi delegata alla
     * classe {@link LogicaMatch}.
     *
     * L'esito restituito viene infine utilizzato per aggiornare lo stato
     * del match, ad esempio modificando il possesso o incrementando il punteggio.
     *
     * @param attacco azione scelta dall'attaccante
     * @param difesa risposta scelta dal difensore
     * @param tecnicaAttaccante eventuale tecnica utilizzata dall'attaccante
     * @param tecnicaDifensore eventuale tecnica utilizzata dal difensore
     *
     * @return {@link EsitoTurno} prodotto dal turno
     *
     * @throws NullPointerException se attacco o difesa sono {@code null}
     * @throws IllegalStateException se il match è già concluso
     * @throws IllegalArgumentException se viene selezionata una tecnica
     *         non posseduta dal personaggio o se la combinazione di azioni
     *         non è valida
     */
    public EsitoTurno giocaTurno(@NonNull AzioneAttaccante attacco, @NonNull AzioneDifensore difesa,
                                 TecnicaSpeciale tecnicaAttaccante, TecnicaSpeciale tecnicaDifensore) {
        if(this.isConcluso())
            throw new IllegalStateException("Uno dei 2 giocatori ha già vinto!");
        if(tecnicaAttaccante != null)
            this.verificaTecnicaPosseduta(this.attaccanteCorrente, tecnicaAttaccante);
        if(tecnicaDifensore != null)
            this.verificaTecnicaPosseduta(this.getDifensoreCorrente(), tecnicaDifensore);
        EsitoTurno esito = this.logicaMatch.risolviAzione(this.attaccanteCorrente, attacco,
                this.getDifensoreCorrente(), difesa, tecnicaAttaccante, tecnicaDifensore);
        this.gestisciEsito(esito);
        return esito;
    }

    private void gestisciEsito(@NonNull EsitoTurno esito) {

        switch (esito) {

            case GOL_SEGNATO -> {
                this.aggiornaPunteggio(this.attaccanteCorrente);
                if(!this.isConcluso()) {
                    this.ripristinaRisorse();
                    this.cambiaTurno();
                }
            }

            case ATTACCO_RESPINTO, DANNO_INFLITTO, PALLA_PERSA, STAMINA_RECUPERATA ->
                    this.cambiaTurno();

            // Se il dribbling riesce, l'attaccante mantiene il possesso e resta
            // tale anche nel turno successivo, quindi non avviene il cambio di turno
            case DRIBBLING_RIUSCITO -> {}
        }
    }

    /**
     * Verifica se il match è terminato.
     *
     * @return {@code true} se almeno uno dei due personaggi ha raggiunto
     *         il numero di gol necessario per la vittoria, {@code false} altrimenti
     */
    public boolean isConcluso() {
        return (this.golAvversario >= GOL_PER_VITTORIA) || (this.golProtagonista >= GOL_PER_VITTORIA);
    }

    /**
     * Restituisce il personaggio che ha vinto il match.
     *
     * @return vincitore del match
     *
     * @throws IllegalStateException se il match non è ancora concluso
     */
    public Personaggio getVincitore() {
        if (!this.isConcluso())
            throw new IllegalStateException("Il match non è ancora terminato!");
        return this.golProtagonista >= GOL_PER_VITTORIA ? this.protagonista : this.avversario;
    }

    // Verifica se il protagonista è l'attaccante corrente
    private boolean isTurnoProtagonista() {return this.attaccanteCorrente.equals(this.protagonista);}

    private Personaggio getDifensoreCorrente() {
        return isTurnoProtagonista() ? this.avversario : this.protagonista;
    }

    private void aggiornaPunteggio(Personaggio attaccante) {
        if (attaccante.equals(this.protagonista))
            this.golProtagonista++;
        else
            this.golAvversario++;
    }

    private void cambiaTurno() {
        this.attaccanteCorrente = isTurnoProtagonista() ? this.avversario : this.protagonista;
    }

    private void ripristinaRisorse() {
        this.protagonista.ripristinaRisorseMatch();
        this.avversario.ripristinaRisorseMatch();
    }

    private void verificaTecnicaPosseduta(Personaggio personaggio, TecnicaSpeciale tecnica) {
        if (!personaggio.possiedeTecnica(tecnica))
            throw new IllegalArgumentException("Il personaggio non possiede questa tecnica!");
    }
}
