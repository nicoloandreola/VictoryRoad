package it.unicam.cs.mpgc.rpg129542.model.stats;

import lombok.NonNull;

/**
 * Interfaccia che definisce il contratto da rispettare per l'implementazione
 * di un oggetto che modifica le statistiche di base TEMPORANEAMENTE durante un match.
 *
 * Le classi che la implementano dovranno sovrascrive un solo metodo che descrive una
 * trasformazione da un'istanza di {@link StatisticheBase} a una nuova istanza contenente i valori modificati.
 *
 * L'annotazione {@link FunctionalInterface} permette al compilatore di verificare che
 * l'interfaccia mantenga un unico metodo astratto e consente inoltre, quando opportuno,
 * di fornire implementazioni anche tramite espressioni lambda o method reference, oltre
 * che attraverso classi concrete come {@link BonusStatistiche} e {@link MalusStatistiche}.
 *
 * @author Nicolò Andreola
 */
@FunctionalInterface
public interface ModificatoreStatistiche {

    /**
     * Modifica le statistiche di base secondo la logica dello specifico modificatore.
     *
     * @param stats statistiche sulle quali applicare il modificatore
     * @return nuova istanza contenente le statistiche modificate
     */
    StatisticheBase applica(@NonNull StatisticheBase stats);
}
