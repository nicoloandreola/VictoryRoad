package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;

/**
 * Questa classe si limita a rappresentare le statistiche base di un
 * personaggio: se quest'ultime migliorano o ricevono modifiche temporanee,
 * queste vengono gestite dalla classe ModificatoreStatistiche, in modo da
 * rispettare i principi SOLID, in particolare quello di Single Responsibility
 *
 * @author Nicolò Andreola
 */
@Getter
public class StatisticheBase {

    private final int attacco;
    private final int difesa;
    private final int agilita;

    public StatisticheBase(int attacco, int difesa, int agilita) {
        if(attacco <= 0 || difesa <= 0 || agilita <= 0)
            throw new IllegalArgumentException("Statistiche base devono avere un valore positivo!");
        this.attacco = attacco;
        this.difesa = difesa;
        this.agilita = agilita;
    }

    /**
     * Calcola l'overall del personaggio come media aritmetica delle statistiche base.
     *
     * @return overall del personaggio
     */
    public int calcolaOverall() {
        return (this.attacco + this.difesa + this.agilita) / 3;
    }
}
