package it.unicam.cs.mpgc.rpg129542.model.match;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Definisce il contratto della logica utilizzata per risolvere
 * i confronti tra le azioni eseguite durante un {@link Match}.
 *
 * L'interfaccia si occupa esclusivamente della risoluzione degli effetti
 * delle azioni, ricevendo i valori già calcolati dalle relative
 * implementazioni e determinando il conseguente {@link EsitoTurno}.
 *
 * La separazione tra questa interfaccia e {@link Match} permette di
 * mantenere distinta la logica di risoluzione delle azioni dalla gestione
 * dello stato complessivo della partita (come punteggio, possesso e
 * conclusione del match) e di modificare o sostituire le regole di
 * un duello senza modificare la classe che coordina una partita.
 *
 * Inoltre, differenti implementazioni dell'interfaccia possono definire
 * regole o formule alternative senza richiedere modifiche alla classe
 * {@link Match} o alle singole azioni.
 *
 * @author Nicolò Andreola
 */
public interface LogicaMatch {

    /**
     * Risolve un attacco diretto confrontando il valore offensivo
     * dell'attaccante con quello difensivo prodotto dal difensore.
     *
     * Se l'attacco supera la difesa, viene applicato il relativo danno
     * alla resistenza del difensore e viene determinato l'esito conseguente.
     *
     * @param difensore personaggio che subisce l'attacco
     * @param valoreAttacco valore offensivo prodotto dall'azione dell'attaccante
     * @param valoreDifesa valore prodotto dalla risposta difensiva
     *
     * @return esito prodotto dalla risoluzione dell'attacco
     *
     * @throws NullPointerException se il difensore è {@code null}
     */
    EsitoTurno risolviAttaccoDiretto(@NonNull Personaggio difensore, int valoreAttacco, int valoreDifesa);

    /**
     * Risolve un tentativo di dribbling confrontando il valore
     * dell'azione dell'attaccante con quello della risposta del difensore.
     *
     * @param valoreDribbling valore prodotto dal dribbling dell'attaccante
     * @param valoreContrasto valore prodotto dalla risposta del difensore
     *
     * @return {@link EsitoTurno#DRIBBLING_RIUSCITO} se il dribbling supera
     *         la risposta difensiva, {@link EsitoTurno#PALLA_PERSA} altrimenti
     */
    EsitoTurno risolviDribbling(int valoreDribbling, int valoreContrasto);
}
