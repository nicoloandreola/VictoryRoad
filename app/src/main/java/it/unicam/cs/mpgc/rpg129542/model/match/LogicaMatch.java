package it.unicam.cs.mpgc.rpg129542.model.match;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import lombok.NonNull;

/**
 * Definisce il contratto per la logica utilizzata nella risoluzione
 * dei turni di un {@link Match}.
 *
 * Una implementazione di questa interfaccia stabilisce quali risposte
 * difensive siano compatibili con ciascuna azione dell'attaccante e
 * determina l'esito prodotto dalla combinazione delle azioni scelte.
 *
 * La separazione tra questa interfaccia e {@link Match} permette
 * di modificare o sostituire le regole di combattimento senza modificare
 * la classe che coordina lo svolgimento della partita.
 *
 * @author Nicolò Andreola
 */
public interface LogicaMatch {

    /**
     * Risolve un turno sulla base delle azioni scelte dall'attaccante
     * e dal difensore.
     *
     * Le tecniche possono essere {@code null} quando nel turno in
     * considerazione non vengono utilizzate.
     *
     * @param attaccante personaggio che possiede il turno offensivo
     * @param attacco azione scelta dall'attaccante
     * @param difensore personaggio che risponde all'azione offensiva
     * @param difesa risposta scelta dal difensore
     * @param tecnicaAttaccante eventuale tecnica scelta dall'attaccante
     * @param tecnicaDifensore eventuale tecnica scelta dal difensore
     *
     * @return {@link EsitoTurno} prodotto dalla risoluzione del turno
     *
     * @throws NullPointerException se attaccante, difensore,
     *         attacco o difesa sono {@code null}
     * @throws IllegalArgumentException se le azioni scelte non sono
     *         compatibili o una tecnica non appartiene al tipo richiesto
     */
    EsitoTurno risolviAzione(@NonNull Personaggio attaccante, @NonNull AzioneAttaccante attacco,
                             @NonNull Personaggio difensore, @NonNull AzioneDifensore difesa,
                             TecnicaSpeciale tecnicaAttaccante, TecnicaSpeciale tecnicaDifensore);

    /**
     * Verifica se una determinata risposta del difensore è compatibile
     * con l'azione scelta dall'attaccante.
     *
     * @param attacco azione scelta dall'attaccante
     * @param difesa risposta scelta dal difensore
     *
     * @return {@code true} se la risposta è ammessa per l'attacco specificato,
     *         {@code false} altrimenti
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     */
    boolean isRispostaValida(@NonNull AzioneAttaccante attacco, @NonNull AzioneDifensore difesa);
}
