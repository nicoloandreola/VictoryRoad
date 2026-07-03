package it.unicam.cs.mpgc.rpg129542.model;

import java.util.Set;

/**
 * Rappresenta un personaggio affrontato dal protagonista.
 * Le responsabilità specifiche dell'avversario verranno aggiunte quando
 * saranno definite le relative regole di dominio.
 */
public final class Avversario extends Personaggio {

    public Avversario(String nome, StatisticheBase statisticheBase, TecnicaSpeciale tecnicaIniziale) {
        super(nome, statisticheBase, tecnicaIniziale);
    }

    public Avversario(String nome, StatisticheBase statisticheBase,
                      Set<TecnicaSpeciale> tecnicheSpeciali) {
        super(nome, statisticheBase, tecnicheSpeciali);
    }
}
