package it.unicam.cs.mpgc.rpg129542.model.personaggio;

import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;

import java.util.Set;

/**
 * Rappresenta un personaggio affrontato dal protagonista,
 * controllato dal gioco ed estende {@link Personaggio}.
 *
 * @author Nicolò Andreola
 */

public class Avversario extends Personaggio {

    /**
     * Crea un avversario con una tecnica speciale iniziale.
     *
     * @param nome nome dell'avversario
     * @param id identificatore univoco dell'avversario
     * @param statisticheBase statistiche permanenti iniziali
     * @param tecnicaIniziale prima tecnica posseduta
     *
     * @throws IllegalArgumentException se uno dei parametri non rispetta
     *         i vincoli definiti da {@link Personaggio}
     */

    public Avversario(String nome, String id, StatisticheBase statisticheBase, TecnicaSpeciale tecnicaIniziale) {
        super(nome, id, statisticheBase, tecnicaIniziale);
    }

    /**
     * Crea un avversario con un insieme di tecniche speciali.
     *
     * @param nome nome dell'avversario
     * @param id identificatore univoco dell'avversario
     * @param statisticheBase statistiche permanenti iniziali
     * @param tecnicheSpeciali tecniche possedute dall'avversario
     *
     * @throws IllegalArgumentException se uno dei parametri non rispetta
     *         i vincoli definiti da {@link Personaggio}
     */

    public Avversario(String nome, String id, StatisticheBase statisticheBase, Set<TecnicaSpeciale> tecnicheSpeciali) {
        super(nome, id, statisticheBase, tecnicheSpeciali);
    }
}
