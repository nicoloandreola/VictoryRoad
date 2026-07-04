package it.unicam.cs.mpgc.rpg129542.model;

import lombok.NonNull;

/**
 * Rappresenta il personaggio controllato dal giocatore ed estende {@link Personaggio}
 *
 * @author Nicolò Andreola
 */

public class Protagonista extends Personaggio {
    /**
     * Crea un protagonista con una tecnica speciale iniziale.
     *
     * @param nome nome del protagonista
     * @param statisticheBase statistiche permanenti iniziali
     * @param tecnicaIniziale prima tecnica posseduta
     *
     * @throws IllegalArgumentException se uno dei parametri non rispetta
     *         i vincoli definiti da {@link Personaggio}
     */
    public Protagonista(String nome, StatisticheBase statisticheBase, TecnicaSpeciale tecnicaIniziale) {
        super(nome, statisticheBase, tecnicaIniziale);
    }

    /**
     * Verifica se il protagonista possiede già una determinata tecnica.
     *
     * @param tecnica tecnica da cercare
     * @return {@code true} se la tecnica è posseduta, {@code false} altrimenti
     *
     * @throws NullPointerException se la tecnica passata è nulla
     */

    public boolean possiedeTecnica(@NonNull TecnicaSpeciale tecnica) {
        return getTecnicheSpeciali().contains(tecnica);
    }


}
