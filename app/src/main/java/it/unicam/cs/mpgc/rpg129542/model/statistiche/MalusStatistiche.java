package it.unicam.cs.mpgc.rpg129542.model.statistiche;

import lombok.NonNull;

/**
 * Implementa l'interfaccia {@link ModificatoreStatistiche} e rappresenta un
 * modificatore che decrementa temporaneamente di una certa quantità una o più statistiche.
 *
 * Per modificare una sola statistica, la classe mette a disposizione 3 metodi
 * che facilitano l'operazione usando il pattern factory.
 *
 */
public class MalusStatistiche implements ModificatoreStatistiche{

    private final int malusAttacco;
    private final int malusDifesa;
    private final int malusAgilita;

    /**
     * Costruisce un modificatore che decrementa tutte le statistiche di base.
     *
     * @param malusAttacco riduzione dell'attacco
     * @param malusDifesa riduzione della difesa
     * @param malusAgilita riduzione dell'agilità
     *
     * @throws IllegalArgumentException se almeno un malus è negativo
     */
    public MalusStatistiche(int malusAttacco, int malusDifesa, int malusAgilita) {
        if (malusAttacco < 0 || malusDifesa < 0 || malusAgilita < 0)
            throw new IllegalArgumentException("I malus non devono essere quantità negative!");
        this.malusAttacco = malusAttacco;
        this.malusDifesa = malusDifesa;
        this.malusAgilita = malusAgilita;
    }

    /**
     * Crea un modificatore che riduce esclusivamente l'attacco.
     *
     * @param valore riduzione da applicare all'attacco
     *
     * @return un nuovo malus applicato soltanto all'attacco
     *
     * @throws IllegalArgumentException se il valore è negativo
     */
    public static MalusStatistiche soloAttacco(int valore) {
        return new MalusStatistiche(valore, 0, 0);
    }

    /**
     * Crea un modificatore che riduce esclusivamente la difesa.
     *
     * @param valore riduzione da applicare alla difesa
     *
     * @return un nuovo malus applicato soltanto alla difesa
     *
     * @throws IllegalArgumentException se il valore è negativo
     */
    public static MalusStatistiche soloDifesa(int valore) {
        return new MalusStatistiche(0, valore, 0);
    }

    /**
     * Crea un modificatore che riduce esclusivamente l'agilità.
     *
     * @param valore riduzione da applicare all'agilità
     *
     * @return un nuovo malus applicato soltanto all'agilità
     *
     * @throws IllegalArgumentException se il valore è negativo
     */
    public static MalusStatistiche soloAgilita(int valore) {
        return new MalusStatistiche(0, 0, valore);
    }

    /**
     * Applica i malus alle statistiche ricevute sottraendo i valori alle
     * statistiche e garantendo che il risultato non sia inferiore a uno.
     *
     * @param stats statistiche originali
     *
     * @return nuove statistiche ridotte, con valore minimo pari a {@code 1}
     */
    @Override
    public StatisticheBase applica(@NonNull StatisticheBase stats) {
        int nuovoAttacco = this.verificaMalus(stats.getAttacco(), this.malusAttacco);
        int nuovaDifesa = this.verificaMalus(stats.getDifesa(), this.malusDifesa);
        int nuovaAgilita = this.verificaMalus(stats.getAgilita(), this.malusAgilita);
        return new StatisticheBase(nuovoAttacco, nuovaDifesa, nuovaAgilita);
    }

    private int verificaMalus(int valoreStatistica, int malus) {
        return Math.max(1, valoreStatistica - malus);
    }
}
