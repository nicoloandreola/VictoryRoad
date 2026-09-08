package it.unicam.cs.mpgc.rpg129542.model.personaggio;

import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import lombok.Getter;
import lombok.NonNull;

/**
 * Rappresenta il personaggio controllato dal giocatore ed estende {@link Personaggio}
 *
 * Oltre alle caratteristiche comuni a tutti i personaggi, mantiene
 * il riferimento alla propria tecnica iniziale, permette di apprenderne
 * di nuove e di migliorare permanentemente le statistiche durante
 * la progressione del gioco.
 *
 * @author Nicolò Andreola
 */

public class Protagonista extends Personaggio {

    @Getter
    private final TecnicaSpeciale tecnicaIniziale;

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
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     */
    public Protagonista(String nome, String id, StatisticheBase statisticheBase, TecnicaSpeciale tecnicaIniziale) {
        super(nome, id, statisticheBase, tecnicaIniziale);
        this.tecnicaIniziale = tecnicaIniziale;
    }

    /**
     * Permette di aggiungere una nuova tecnica alla collezione del protagonista
     * quando viene appresa come ricompensa dopo una vittoria.
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
