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

    /**
     * Crea un personaggio con una singola tecnica speciale iniziale
     * (ogni protagonista all'inizio ha una sola tecnica speciale).
     *
     * @param nome nome del personaggio
     * @param statisticheBase statistiche di base iniziali
     * @param tecnicaIniziale prima tecnica posseduta dal personaggio
     *
     * @throws IllegalArgumentException se il nome è nullo o vuoto, se le
     *         statistiche sono nulle oppure se la tecnica è nulla (in questo
     *         ultimo caso ci pensa il metodo add di {@link HashSet} a
     *         lanciarla, quindi non occorre fare nessun controllo ulteriore)
     */
    public Personaggio(String nome, StatisticheBase statisticheBase, TecnicaSpeciale tecnicaIniziale) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome del personaggio non può essere vuoto!");
        if (statisticheBase == null)
            throw new IllegalArgumentException("Statistiche base non possono essere nulle!");
        this.nome = nome;
        this.gestoreStatistiche = new GestoreStatistiche(statisticheBase);
        this.overall = this.getOverall();
        this.tecnicheSpeciali = new HashSet<>();
        this.tecnicheSpeciali.add(tecnicaIniziale);
        this.risorseMatch = new RisorseMatch();
    }

    /**
     * Crea un personaggio con un insieme di tecniche speciali
     * (gli avversari invece possono aver sin da subito più tecniche).
     *
     * @param nome nome del personaggio
     * @param statisticheBase statistiche permanenti iniziali
     * @param tecnicheSpeciali tecniche inizialmente possedute
     *
     * @throws IllegalArgumentException se il nome è nullo o vuoto,
     *         se le statistiche sono nulle oppure se l'insieme delle tecniche
     *         è nullo, vuoto o contiene elementi nulli
     */
    public Personaggio(String nome, StatisticheBase statisticheBase, Set<TecnicaSpeciale> tecnicheSpeciali) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome del personaggio non può essere vuoto!");
        if (statisticheBase == null)
            throw new IllegalArgumentException("Statistiche base non possono essere nulle!");
        if (tecnicheSpeciali == null || tecnicheSpeciali.isEmpty())
            throw new IllegalArgumentException("Il personaggio deve avere almeno una tecnica speciale!");

        this.nome = nome;
        this.gestoreStatistiche = new GestoreStatistiche(statisticheBase);
        this.overall = this.getOverall();
        this.tecnicheSpeciali = new HashSet<>(tecnicheSpeciali);
        this.risorseMatch = new RisorseMatch();
    }

    /**
     * Restituisce le statistiche permanenti del personaggio,
     * senza considerare eventuali modificatori temporanei.
     *
     * @return statistiche base del personaggio
     */
    public StatisticheBase getStatisticheBase() {
        return this.gestoreStatistiche.getStatisticheBase();
    }

    /**
     * Restituisce le statistiche correnti del personaggio: se è presente un modificatore temporaneo,
     * il risultato comprende gli effetti prodotti da tale modificatore
     *
     * @return statistiche effettive del personaggio nel momento in cui è chiamato il metodo
     */
    public StatisticheBase getStatisticheEffettive() {
        return this.gestoreStatistiche.getStatisticheEffettive();
    }

    /**
     * Restituisce l'overall del personaggio come media delle statistiche base.
     *
     * @return overall calcolato dal metodo {@link StatisticheBase#calcolaOverall()}
     */
    public int getOverall() {
        return this.getStatisticheBase().calcolaOverall();
    }

    /**
     * Restituisce la resistenza corrente del personaggio.
     *
     * @return quantità di resistenza disponibile
     */
    public int getResistenza() {
        return this.risorseMatch.getResistenza();
    }

    /**
     * Restituisce la stamina corrente del personaggio.
     *
     * @return quantità di stamina disponibile
     */
    public int getStamina() {
        return this.risorseMatch.getStamina();
    }

    /**
     * Restituisce una copia non modificabile delle tecniche possedute.
     *
     * @return insieme non modificabile delle tecniche speciali
     */
    public Set<TecnicaSpeciale> getTecnicheSpeciali() {
        return Set.copyOf(this.tecnicheSpeciali);
    }

    /**
     * Confronta due personaggi in base al loro nome e al loro overall:
     * lo stato modificabile, come statistiche, risorse e tecniche, non
     * contribuisce all'identità del personaggio.
     *
     * @param obj oggetto da confrontare
     * @return {@code true} se i personaggi hanno lo stesso nome e lo stesso overall
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Personaggio))
            return false;
        Personaggio other = (Personaggio) obj;
        return this.nome.equals(other.nome) && this.overall == other.overall;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + this.nome.hashCode();
        result = prime * result + Integer.hashCode(this.overall);
        return result;
    }

}
