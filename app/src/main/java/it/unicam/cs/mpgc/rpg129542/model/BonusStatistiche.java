package it.unicam.cs.mpgc.rpg129542.model;

public class BonusStatistiche implements ModificatoreStatistiche{

    private final int bonusAttacco;
    private final int bonusDifesa;
    private final int bonusAgilita;

    public BonusStatistiche(int bonusAttacco, int bonusDifesa, int bonusAgilita) {
        if (bonusAttacco < 0 || bonusDifesa < 0 || bonusAgilita < 0)
            throw new IllegalArgumentException("I bonus non devono essere quantità negative!");
        this.bonusAttacco = bonusAttacco;
        this.bonusDifesa = bonusDifesa;
        this.bonusAgilita = bonusAgilita;
    }

    @Override
    public StatisticheBase applica(StatisticheBase stats) {
        int nuovoAttacco = stats.getAttacco() + this.bonusAttacco;
        int nuovaDifesa = stats.getDifesa() + this.bonusDifesa;
        int nuovaAgilita = stats.getAgilita() + this.bonusAgilita;
        return new StatisticheBase(nuovoAttacco, nuovaDifesa, nuovaAgilita);
    }
}
