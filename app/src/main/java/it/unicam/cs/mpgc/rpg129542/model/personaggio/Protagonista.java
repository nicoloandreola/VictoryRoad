package it.unicam.cs.mpgc.rpg129542.model.personaggio;

import it.unicam.cs.mpgc.rpg129542.model.stats.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
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
     * @param id identificatore univoco del protagonista
     * @param statisticheBase statistiche permanenti iniziali
     * @param tecnicaIniziale prima tecnica posseduta
     *
     * @throws IllegalArgumentException se uno dei parametri non rispetta
     *         i vincoli definiti da {@link Personaggio}
     */
    public Protagonista(String nome, String id, StatisticheBase statisticheBase, TecnicaSpeciale tecnicaIniziale) {
        super(nome, id, statisticheBase, tecnicaIniziale);
    }

    /**
     * Permette di aggiungere una nuova tecnica alla collezione del protagonista
     * ogni volta che ne "impara" una (cioè dopo ogni vittoria)
     *
     * @param tecnica nuova tecnica da aggiungere
     * @return {@code true} se la tecnica non è già presente nel set del
     *          personaggio e viene aggiunta correttamente, {@code false} altrimenti
     *
     * @throws NullPointerException se la tecnica passata è nulla
     */

    public boolean imparaTecnica(@NonNull TecnicaSpeciale tecnica) {
        return aggiungiTecnica(tecnica);
    }

    /**
     * Permette di migliorare PERMANENTEMENTE le statistiche del protagonista
     *
     * @param attacco valore che va incrementato alla statistica di attacco
     * @param difesa valore che va incrementato alla statistica di difesa
     * @param agilita valore che va incrementato alla statistica di agilità
     */
    public void miglioraStatistiche(int attacco, int difesa, int agilita) {
        this.getGestoreStatistiche().miglioraStatistiche(attacco, difesa, agilita);
    }


}
