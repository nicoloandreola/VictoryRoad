package it.unicam.cs.mpgc.rpg129542.model.statistiche;

import lombok.Getter;

/**
 * Questa classe si limita a rappresentare le statistiche base di un
 * personaggio: se migliorano o ricevono modifiche temporanee, queste
 * vengono gestite dalla classe {@link ModificatoreStatistiche}
 *
 * @author Nicolò Andreola
 */
@Getter
public class StatisticheBase {

    private final int attacco;
    private final int difesa;
    private final int agilita;

    /**
     * Assegna i valori a tutte le statistiche di base.
     *
     * @param attacco valore di attacco
     * @param difesa valore di difesa
     * @param agilita valore di agilità
     *
     * @throws IllegalArgumentException se almeno un valore è minore o uguale a zero
     */
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

    /**
     * Due istanze di questa classe sono uguali se tutti e 3 i campi lo sono
     *
     * @param obj   riferimento all'oggetto da confrontare.
     * @return {@code true} se tutti i campi hanno lo stesso valore
     *
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(!(obj instanceof StatisticheBase))
            return false;
        StatisticheBase other = (StatisticheBase) obj;
        return (this.attacco == other.attacco) && (this.difesa == other.difesa)
                && (this.agilita == other.agilita);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Integer.hashCode(this.attacco);
        result = prime * result + Integer.hashCode(this.difesa);
        result = prime * result + Integer.hashCode(this.agilita);
        return result;
    }
}
