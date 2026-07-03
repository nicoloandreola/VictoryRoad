package it.unicam.cs.mpgc.rpg129542.model;

public class Protagonista extends Personaggio {

    public Protagonista(String nome, StatisticheBase statisticheBase, TecnicaSpeciale tecnicaIniziale) {
        super(nome, statisticheBase, tecnicaIniziale);
    }

    public boolean possiedeTecnica(TecnicaSpeciale tecnica) {
        if (tecnica == null)
            throw new IllegalArgumentException("La tecnica non può essere nulla!");
        return getTecnicheSpeciali().contains(tecnica);
    }


}
