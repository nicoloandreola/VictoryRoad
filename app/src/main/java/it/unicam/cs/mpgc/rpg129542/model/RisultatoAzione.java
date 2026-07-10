package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;
import lombok.NonNull;

/**
 * Rappresenta il risultato prodotto da un'azione svolta durante un match.
 *
 * Questa classe è utile perché permette al model di comunicare al controller
 * cosa è successo senza dipendere dalla futura interfaccia grafica.
 *
 * @author Nicolò Andreola
 */
@Getter
public class RisultatoAzione {

    private final Azione azione;
    private final EsitoAzione esito;
    private final StatoMatch statoMatch;
    private final String nomePersonaggio;
    private final int valore;
    private final String descrizione;

    /**
     * Crea un nuovo risultato di un'azione, validando gli invarianti dell'oggetto.
     *
     * @param azione          l'azione eseguita
     * @param esito           tipo di esito prodotto dall'azione
     * @param statoMatch      stato del match subito dopo l'azione
     * @param nomePersonaggio nome del personaggio protagonista dell'azione
     * @param valore          valore numerico associato all'esito (danno, recupero, gol...);
     *                        non può essere negativo
     * @param descrizione     descrizione testuale dell'azione, pronta per essere mostrata
     * @throws NullPointerException     se uno dei parametri obbligatori è {@code null}
     * @throws IllegalArgumentException se {@code nomePersonaggio} o {@code descrizione} sono
     *                                  vuoti, o se {@code valore} è negativo
     */
    public RisultatoAzione(@NonNull Azione azione, @NonNull EsitoAzione esito, @NonNull StatoMatch statoMatch,
                           @NonNull String nomePersonaggio, int valore, @NonNull String descrizione) {

        if (nomePersonaggio.isBlank())
            throw new IllegalArgumentException("Il nome del personaggio non può essere vuoto!");
        if (descrizione.isBlank())
            throw new IllegalArgumentException("La descrizione non può essere vuota!");
        if (valore < 0)
            throw new IllegalArgumentException("Il valore non può essere negativo!");

        this.azione = azione;
        this.esito = esito;
        this.statoMatch = statoMatch;
        this.nomePersonaggio = nomePersonaggio;
        this.valore = valore;
        this.descrizione = descrizione;
    }

    /**
     * Metodo che permette a Controller e View di sapere quando un match è finito, invece di far
     * ripetere il confronto con StatoMatch.IN_CORSO ogni volta al loro interno.
     *
     * @return {@code true} se, a seguito di questa azione, il match è terminato
     */
    public boolean verificaConclusioneMatch() {
        return this.statoMatch != StatoMatch.IN_CORSO;
    }
}
