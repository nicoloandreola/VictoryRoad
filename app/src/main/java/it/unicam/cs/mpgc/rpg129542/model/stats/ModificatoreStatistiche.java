package it.unicam.cs.mpgc.rpg129542.model.stats;

import lombok.NonNull;

/**
 * Interfaccia che definisce il contratto da rispettare per l'implementazione
 * di un oggetto che modifica le statistiche di base TEMPORANEAMENTE durante un match.
 *
 * Le classi che la implementano dovranno sovrascrive un solo metodo che permette
 * appunto di applicare variazioni ai valori delle statistiche di base.
 *
 * @author Nicolò Andreola
 */

public interface ModificatoreStatistiche {

    /**
     * Modifica le statistiche di base secondo la logica dello specifico modificatore.
     *
     * @param stats statistiche sulle quali applicare il modificatore
     * @return nuova istanza contenente le statistiche modificate
     */
    StatisticheBase applica(@NonNull StatisticheBase stats);
}
