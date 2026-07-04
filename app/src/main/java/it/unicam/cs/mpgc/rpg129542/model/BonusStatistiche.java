package it.unicam.cs.mpgc.rpg129542.model;

import lombok.NonNull;

/**
 * Implementa l'interfaccia {@link ModificatoreStatistiche} e rappresenta un
 * modificatore che incrementa temporaneamente di una certa quantità una o più statistiche.
 *
 * Per modificare una sola statistica, la classe mette a disposizione 3 metodi
 * che facilitano l'operazione usando il pattern factory.
 *
 */
public class BonusStatistiche implements ModificatoreStatistiche{

    private final int bonusAttacco;
    private final int bonusDifesa;
    private final int bonusAgilita;

    /**
     * Costruisce un modificatore che incrementa tutte le statistiche di base.
     *
     * @param bonusAttacco incremento dell'attacco
     * @param bonusDifesa incremento della difesa
     * @param bonusAgilita incremento dell'agilità
     *
     * @throws IllegalArgumentException se almeno un bonus è negativo
     */
    public BonusStatistiche(int bonusAttacco, int bonusDifesa, int bonusAgilita) {
        if (bonusAttacco < 0 || bonusDifesa < 0 || bonusAgilita < 0)
            throw new IllegalArgumentException("I bonus non devono essere quantità negative!");
        this.bonusAttacco = bonusAttacco;
        this.bonusDifesa = bonusDifesa;
        this.bonusAgilita = bonusAgilita;
    }

    /**
     * Crea un modificatore che incrementa esclusivamente l'attacco.
     *
     * @param valore incremento da applicare all'attacco
     *
     * @return un nuovo bonus applicato soltanto all'attacco
     *
     * @throws IllegalArgumentException se il valore è negativo
     *
     */
    public static BonusStatistiche soloAttacco(int valore) {
        return new BonusStatistiche(valore, 0, 0);
    }

    /**
     * Crea un modificatore che incrementa esclusivamente la difesa.
     *
     * @param valore incremento da applicare alla difesa
     *
     * @return un nuovo bonus applicato soltanto alla difesa
     *
     * @throws IllegalArgumentException se il valore è negativo
     */
    public static BonusStatistiche soloDifesa(int valore) {
        return new BonusStatistiche(0, valore, 0);
    }

    /**
     * Crea un modificatore che incrementa esclusivamente l'agilità.
     *
     * @param valore incremento da applicare all'agilità
     *
     * @return un nuovo bonus applicato soltanto all'agilità
     *
     * @throws IllegalArgumentException se il valore è negativo
     */
    public static BonusStatistiche soloAgilita(int valore) {
        return new BonusStatistiche(0, 0, valore);
    }

    /**
     * Applica i bonus alle statistiche ricevute.
     *
     * @param stats statistiche originali
     *
     * @return nuove statistiche incrementate
     *
     */
    @Override
    public StatisticheBase applica(@NonNull StatisticheBase stats) {
        int nuovoAttacco = stats.getAttacco() + this.bonusAttacco;
        int nuovaDifesa = stats.getDifesa() + this.bonusDifesa;
        int nuovaAgilita = stats.getAgilita() + this.bonusAgilita;
        return new StatisticheBase(nuovoAttacco, nuovaDifesa, nuovaAgilita);
    }
}
