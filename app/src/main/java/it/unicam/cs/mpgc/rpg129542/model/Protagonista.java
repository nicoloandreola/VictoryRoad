package it.unicam.cs.mpgc.rpg129542.model;

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
     * @throws IllegalArgumentException se la tecnica passata è nulla
     */
    public boolean possiedeTecnica(TecnicaSpeciale tecnica) {
        if (tecnica == null)
            throw new IllegalArgumentException("La tecnica non può essere nulla!");
        return getTecnicheSpeciali().contains(tecnica);
    }


}
