package it.unicam.cs.mpgc.rpg129542.model;

public class MalusStatistiche implements ModificatoreStatistiche{

    private final int malusAttacco;
    private final int malusDifesa;
    private final int malusAgilita;

    public MalusStatistiche(int malusAttacco, int malusDifesa, int malusAgilita) {
        if (malusAttacco < 0 || malusDifesa < 0 || malusAgilita < 0)
            throw new IllegalArgumentException("I malus non devono essere quantità negative!");
        this.malusAttacco = malusAttacco;
        this.malusDifesa = malusDifesa;
        this.malusAgilita = malusAgilita;
    }

    @Override
    public StatisticheBase applica(StatisticheBase stats) {
        int nuovoAttacco = this.verificaMalus(stats.getAttacco(), this.malusAttacco);
        int nuovaDifesa = this.verificaMalus(stats.getDifesa(), this.malusDifesa);
        int nuovaAgilita = this.verificaMalus(stats.getAgilita(), this.malusAgilita);
        return new StatisticheBase(nuovoAttacco, nuovaDifesa, nuovaAgilita);
    }

    private int verificaMalus(int valoreStatistica, int malus) {
        return Math.max(1, valoreStatistica - malus);
    }
}
