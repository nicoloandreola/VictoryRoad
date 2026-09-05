package it.unicam.cs.mpgc.rpg129542.controller;

import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneAttaccante;
import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneDifensore;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import lombok.NonNull;

/**
 * Interfaccia che rappresenta il contratto che stabilisce le regole utilizzate per determinare
 * automaticamente le azioni eseguite da un {@link Avversario} durante un match.
 *
 * La strategia si occupa esclusivamente della scelta delle azioni: la loro
 * esecuzione e la risoluzione degli effetti rimangono responsabilità delle
 * classi del model.
 *
 * Separare la scelta delle azioni dalla gestione complessiva della partita
 * permette al controller di dipendere da questa astrazione anziché da una particolare
 * strategia concreta. In questo modo è possibile introdurre comportamenti differenti
 * dell'avversario senza modificare la gestione del match o le relative regole.
 *
 * @author Nicolò Andreola
 */
public interface StrategiaAvversario {

    /**
     * Determina l'azione che l'avversario deve eseguire quando ricopre
     * il ruolo di attaccante.
     *
     * L'implementazione deve restituire solamente un'azione utilizzabile
     * dall'avversario nello stato corrente del match.
     *
     * @param avversario avversario che deve effettuare l'attacco
     *
     * @return azione offensiva scelta per l'avversario
     *
     * @throws NullPointerException se l'avversario è {@code null}
     */
    AzioneAttaccante determinaAttacco(@NonNull Avversario avversario);

    /**
     * Determina la risposta dell'avversario all'azione scelta
     * dall'attaccante.
     *
     * La risposta deve essere compatibile con il tipo di attacco
     * ricevuto. Se l'azione non richiede alcuna risposta difensiva,
     * il metodo restituisce {@code null}.
     *
     * @param avversario avversario che deve difendere
     * @param attacco azione scelta dall'attaccante
     *
     * @return azione difensiva scelta oppure {@code null} se
     *         l'attacco non richiede una risposta
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     */
    AzioneDifensore determinaDifesa(@NonNull Avversario avversario, @NonNull AzioneAttaccante attacco);
}
