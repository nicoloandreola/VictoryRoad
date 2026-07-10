package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.Random;

/**
 * Rappresenta un match tra un {@link Protagonista} e un {@link Avversario}: il suo compito
 * è coordinare lo svolgimento della partita, gestendo turni, punteggio, gol e conclusione
 * del match.
 * <p>
 * La classe gestisce l'orchestrazione del match (turni, punteggio, stato); il calcolo
 * dell'esito delle singole azioni è isolato in metodi di supporto dedicati, in modo da
 * evitare duplicazione tra azioni simili (es. tiro e tecnica offensiva).
 * <p>
 * Un match si conclude quando uno dei due personaggi arriva a {@value #GOL_PER_VITTORIA} gol.
 *
 * @author Nicolò Andreola
 */
@Getter
public class Match {

    /**
     * Numero di gol necessari per concludere un match con una vittoria o una sconfitta.
     */
    public static final int GOL_PER_VITTORIA = 3;

    private final Protagonista protagonista;
    private final Avversario avversario;

    private Personaggio turnoDi;
    private StatoMatch statoMatch;
    private int golProtagonista;
    private int golAvversario;

    /**
     * Crea un nuovo match inizializzando a 0 i gol dei due personaggi e scegliendo
     * casualmente chi dei due personaggi inizia ad attaccare con il metodo privato
     * {@link #sorteggiaPersonaggioIniziale()} che simula il lancio di una moneta.
     *
     * Ripristina inoltre completamente le risorse dei due personaggi prima dell'inizio.
     *
     * @param protagonista personaggio controllato dal giocatore
     * @param avversario   personaggio controllato dal gioco
     * @throws NullPointerException se uno dei due personaggi passati è {@code null}
     */
    public Match(@NonNull Protagonista protagonista, @NonNull Avversario avversario) {
        this.protagonista = protagonista;
        this.avversario = avversario;
        this.golProtagonista = 0;
        this.golAvversario = 0;
        this.statoMatch = StatoMatch.IN_CORSO;
        this.turnoDi = sorteggiaPersonaggioIniziale();

        this.protagonista.ripristinaRisorseMatch();
        this.avversario.ripristinaRisorseMatch();
    }

    // Sorteggia casualmente il personaggio che eseguirà la prima azione del match.
    private Personaggio sorteggiaPersonaggioIniziale() {
        Random random = new Random();
        boolean testa = random.nextBoolean();
        return testa ? this.protagonista : this.avversario;
    }

    // Verifica a chi spetta la prossima mossa
    public boolean isTurnoProtagonista() {
        return this.turnoDi.equals(this.protagonista);
    }

    // Restituisce il personaggio che in questo turno subisce l'eventuale azione offensiva.
    private Personaggio getDifensoreCorrente() {
        return isTurnoProtagonista() ? this.avversario : this.protagonista;
    }

    // Esegue un'azione base (non una tecnica speciale) per il personaggio di turno.
    public RisultatoAzione eseguiAzioneBase(@NonNull Azione azione) {
        if (!verificaMatchInCorso())
            throw new IllegalStateException("Match terminato!");

        Personaggio attaccante = getTurnoDi();
        Personaggio difensore = getDifensoreCorrente();

        return switch (azione) {
            case TIRO -> eseguiTiro(attaccante, difensore);
            case PARATA -> eseguiParata(attaccante);
            case DRIBBLING -> eseguiDribbling(attaccante);
            case TECNICA_SPECIALE -> throw new IllegalArgumentException(
                    "Per usare una tecnica speciale devi indicare anche la tecnica scelta!");
        };
    }

    // Esegue una tecnica speciale per il personaggio di turno.
    public RisultatoAzione eseguiTecnica(@NonNull TecnicaSpeciale tecnicaScelta) {
        if (!verificaMatchInCorso())
            throw new IllegalStateException("Match terminato!");

        Personaggio attaccante = getTurnoDi();
        Personaggio difensore = getDifensoreCorrente();

        verificaTecnicaPosseduta(attaccante, tecnicaScelta);

        return switch (tecnicaScelta.getTipo()) {
            case OFFENSIVA -> eseguiTecnicaOffensiva(attaccante, difensore, tecnicaScelta);
            case DIFENSIVA, SUPPORTO -> eseguiTecnicaDifensiva(attaccante, tecnicaScelta);
        };
    }

    // Esegue un tiro usando l'attacco effettivo del personaggio di turno.
    private RisultatoAzione eseguiTiro(Personaggio attaccante, Personaggio difensore) {
        int attacco = attaccante.getStatisticheEffettive().getAttacco();
        return calcolaAttacco(Azione.TIRO, attaccante, difensore, attacco, "ha tirato", "il tiro");
    }

    // Esegue una parata recuperando resistenza in base alla difesa effettiva del personaggio.
    private RisultatoAzione eseguiParata(Personaggio personaggio) {
        int difesa = personaggio.getStatisticheEffettive().getDifesa();
        int recupero = calcolaRecupero(difesa);
        personaggio.recuperaResistenza(recupero);
        return calcolaRecupero(Azione.PARATA, EsitoAzione.RESISTENZA_RECUPERATA, personaggio, recupero,
        "ha effettuato una parata", "resistenza");
    }

    // Esegue un dribbling recuperando stamina in base all'agilità effettiva del personaggio.
    private RisultatoAzione eseguiDribbling(Personaggio personaggio) {
        int agilita = personaggio.getStatisticheEffettive().getAgilita();
        int recupero = calcolaRecupero(agilita);
        personaggio.recuperaStamina(recupero);
        return calcolaRecupero(Azione.DRIBBLING, EsitoAzione.STAMINA_RECUPERATA, personaggio, recupero,
                "ha effettuato un dribbling", "stamina");
    }

    // Esegue una tecnica offensiva, consumando la stamina richiesta e usando il
    // valore prodotto dalla tecnica come valore di attacco
    private RisultatoAzione eseguiTecnicaOffensiva(Personaggio attaccante, Personaggio difensore,
                                                   TecnicaSpeciale tecnicaScelta) {
        int valoreAttacco = tecnicaScelta.usa(attaccante);
        return calcolaAttacco(Azione.TECNICA_SPECIALE, attaccante, difensore, valoreAttacco,
                "ha usato " + tecnicaScelta.getNome(), "la tecnica " + tecnicaScelta.getNome());
    }

    /* Esegue una tecnica difensiva o di supporto, consumando la stamina richiesta e
      usando il valore prodotto dalla tecnica per calcolare il recupero di resistenza. */
    private RisultatoAzione eseguiTecnicaDifensiva(Personaggio personaggio, TecnicaSpeciale tecnicaScelta) {
        int valoreDifensivo = tecnicaScelta.usa(personaggio);
        int recupero = calcolaRecupero(valoreDifensivo);
        personaggio.recuperaResistenza(recupero);
        return calcolaRecupero(Azione.TECNICA_SPECIALE, EsitoAzione.RESISTENZA_RECUPERATA, personaggio, recupero,
        "ha usato " + tecnicaScelta.getNome(), "resistenza");
    }


    /* Completa un'azione offensiva: calcola il danno, lo applica al difensore e determina
    se l'azione è stata respinta, se ha causato un gol o se ha semplicemente inflitto danno.
    Condivisa tra tiro e tecnica offensiva per evitare di duplicare la stessa logica. */
    private RisultatoAzione calcolaAttacco(Azione azione, Personaggio attaccante, Personaggio difensore,
                                           int valoreAttacco, String verboAzione, String nomeAzionePerRespinta) {
        int difesa = difensore.getStatisticheEffettive().getDifesa();
        int danno = calcolaDanno(valoreAttacco, difesa);

        if (danno == 0) {
            cambiaTurno();
            String descrizione = difensore.getNome() + " ha respinto " + nomeAzionePerRespinta
                    + " di " + attaccante.getNome() + ".";
            return creaRisultato(azione, EsitoAzione.ATTACCO_RESPINTO, attaccante.getNome(), 0, descrizione);
        }

        difensore.subisciDanno(danno);

        if (difensore.getResistenza() == 0) {
            return registraGol(azione, attaccante);
        }

        cambiaTurno();
        String descrizione = attaccante.getNome() + " " + verboAzione
                + " e ha inflitto " + danno + " danni a " + difensore.getNome() + ".";
        return creaRisultato(azione, EsitoAzione.DANNO_INFLITTO, attaccante.getNome(), danno, descrizione);
    }

    /* Completa un'azione di recupero (parata, dribbling o tecnica difensiva) dopo che il
    chiamante ha già calcolato il recupero e lo ha applicato alla risorsa giusta:
    si occupa della parte comune a tutte e tre, cioè cambiare turno, comporre la
    descrizione e creare il risultato. Evita di duplicare questi passaggi in ogni metodo. */
    private RisultatoAzione calcolaRecupero(Azione azione, EsitoAzione esito, Personaggio personaggio,
                                            int recupero,
                                            String verboAzione, String nomeRisorsa) {
        cambiaTurno();

        String descrizione = personaggio.getNome() + " " + verboAzione
                + " e ha recuperato " + recupero + " punti " + nomeRisorsa + ".";
        return creaRisultato(azione, esito, personaggio.getNome(), recupero, descrizione);
    }

    // Registra un gol segnato dall'attaccante, aggiorna punteggio e stato del match
    // e, se il match non è terminato, ripristina le risorse prima del turno successivo.
    private RisultatoAzione registraGol(Azione azione, Personaggio attaccante) {
        incrementaPunteggio(attaccante);
        aggiornaEsitoMatch();

        if (this.verificaMatchInCorso()) {
            this.protagonista.ripristinaRisorseMatch();
            this.avversario.ripristinaRisorseMatch();
            cambiaTurno();
        }

        String descrizione = attaccante.getNome() + " ha segnato un gol!";
        return creaRisultato(azione, EsitoAzione.GOL, attaccante.getNome(), 1, descrizione);
    }

    private RisultatoAzione creaRisultato(Azione azione, EsitoAzione esito, String nomePersonaggio, int valore, String descrizione) {
        return new RisultatoAzione(azione, esito, this.statoMatch, nomePersonaggio, valore, descrizione);
    }

    // Incrementa il punteggio del personaggio che ha segnato.
    private void incrementaPunteggio(Personaggio attaccante) {
        if (attaccante.equals(this.protagonista))
            this.golProtagonista++;
        else
            this.golAvversario++;
    }

    // Aggiorna lo stato del match in base al punteggio corrente.
    // Se nessuno ha ancora raggiunto i 3 gol, lo stato resta invariato.
    private void aggiornaEsitoMatch() {
        if (this.golProtagonista >= GOL_PER_VITTORIA)
            this.statoMatch = StatoMatch.VITTORIA;
        else if (this.golAvversario >= GOL_PER_VITTORIA)
            this.statoMatch = StatoMatch.SCONFITTA;
    }

    // Permette di alternare il turno tra protagonista e avversario
    private void cambiaTurno() {
        this.turnoDi = isTurnoProtagonista() ? this.avversario : this.protagonista;
    }

    // Verifica che il match non sia terminato
    private boolean verificaMatchInCorso() {
        return this.statoMatch == StatoMatch.IN_CORSO;
    }

    // Verifica che il personaggio possieda la tecnica che sta provando a usare.
    private void verificaTecnicaPosseduta(Personaggio personaggio, TecnicaSpeciale tecnica) {
        if (!personaggio.possiedeTecnica(tecnica))
            throw new IllegalArgumentException("Il personaggio non possiede questa tecnica!");
    }

    // Calcola il danno prodotto da un'azione offensiva.
    private static int calcolaDanno(int valoreAttacco, int valoreDifesa) {
        return Math.max(0, valoreAttacco - valoreDifesa);
    }

    // Calcola la quantità di risorsa recuperata a partire da una statistica.
    private static int calcolaRecupero(int statistica) {
        return Math.max(1, statistica / 2);
    }
}
