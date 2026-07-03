package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Questa classe rappresenta un personaggio generico del gioco e, in quanto
 * astratta, non contiene logica specifica del protagonista o dell'avversario.
 *
 * Contiene solo le informazioni comuni a protagonisti e avversari:
 * nome, overall, statistiche, risorse di match e tecniche speciali.
 *
 * @author Nicolò Andreola
 */

public abstract class Personaggio {

    @Getter
    private final String nome;
    private int overall;
    private final GestoreStatistiche gestoreStatistiche;
    private Set<TecnicaSpeciale> tecnicheSpeciali;
    private RisorseMatch risorseMatch;

    public Personaggio(String nome, StatisticheBase statisticheBase, TecnicaSpeciale tecnicaIniziale) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome del personaggio non può essere vuoto!");
        if (statisticheBase == null)
            throw new IllegalArgumentException("Statistiche base non possono essere nulle!");
        this.nome = nome;
        this.overall = this.getOverall();
        this.gestoreStatistiche = new GestoreStatistiche(statisticheBase);
        this.tecnicheSpeciali = new HashSet<>();
        this.tecnicheSpeciali.add(tecnicaIniziale);
        this.risorseMatch = new RisorseMatch();
    }

    public Personaggio(String nome, StatisticheBase statisticheBase, Set<TecnicaSpeciale> tecnicheSpeciali) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome del personaggio non può essere vuoto!");
        if (statisticheBase == null)
            throw new IllegalArgumentException("Statistiche base non possono essere nulle!");
        if (tecnicheSpeciali == null || tecnicheSpeciali.isEmpty())
            throw new IllegalArgumentException("Il personaggio deve avere almeno una tecnica speciale!");

        this.nome = nome;
        this.overall = this.getOverall();
        this.gestoreStatistiche = new GestoreStatistiche(statisticheBase);
        this.tecnicheSpeciali = new HashSet<>(tecnicheSpeciali);
        this.risorseMatch = new RisorseMatch();
    }

    public StatisticheBase getStatisticheBase() {
        return this.gestoreStatistiche.getStatisticheBase();
    }

    public StatisticheBase getStatisticheEffettive() {
        return this.gestoreStatistiche.getStatisticheEffettive();
    }

    public int getOverall() {
        return this.getStatisticheBase().calcolaOverall();
    }

    public int getResistenza() {
        return this.risorseMatch.getResistenza();
    }

    public int getStamina() {
        return this.risorseMatch.getStamina();
    }

    public Set<TecnicaSpeciale> getTecnicheSpeciali() {
        return Set.copyOf(this.tecnicheSpeciali);
    }

}
