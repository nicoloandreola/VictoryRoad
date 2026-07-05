package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Questa classe rappresenta un personaggio generico del gioco e, in quanto
 * astratta, non contiene logica specifica del protagonista o dell'avversario.
 *
 * Contiene solo le informazioni comuni a protagonisti e avversari:
 * nome, id, statistiche, risorse di match e tecniche speciali.
 *
 * @author Nicolò Andreola
 */
@Getter
public abstract class Personaggio {


    private final String nome;
    private final String id;
    private final GestoreStatistiche gestoreStatistiche;
    private Set<TecnicaSpeciale> tecnicheSpeciali;
    private RisorseMatch risorseMatch;

    /**
     * Crea un personaggio con una singola tecnica speciale iniziale
     * (ogni protagonista all'inizio ha una sola tecnica speciale).
     *
     * Il costruttore è {@code protected} perché un {@link Personaggio}
     * deve essere istanziato attraverso una sottoclasse concreta, come
     * {@link Protagonista} o {@link Avversario}.
     *
     * @param nome nome del personaggio
     * @param id identificatore univoco del personaggio
     * @param statisticheBase statistiche di base iniziali
     * @param tecnicaIniziale prima tecnica posseduta dal personaggio
     *
     * @throws NullPointerException se uno tra i parametri è nullo
     *
     * @throws IllegalArgumentException se il nome o l'id è vuoto
     */
    protected Personaggio(@NonNull String nome, @NonNull String id, @NonNull StatisticheBase statisticheBase, @NonNull TecnicaSpeciale tecnicaIniziale) {
        this.verificaNomeAndId(nome, id);
        this.nome = nome;
        this.id = id;
        this.gestoreStatistiche = new GestoreStatistiche(statisticheBase);
        this.tecnicheSpeciali = new HashSet<>();
        this.tecnicheSpeciali.add(tecnicaIniziale);
        this.risorseMatch = new RisorseMatch();
    }

    /**
     * Crea un personaggio con un insieme di tecniche speciali
     * (gli avversari invece possono aver sin da subito più tecniche).
     *
     * @param nome nome del personaggio
     * @param id identificatore univoco del personaggio
     * @param statisticheBase statistiche permanenti iniziali
     * @param tecnicheSpeciali tecniche inizialmente possedute
     *
     * @throws NullPointerException se uno tra i parametri è nullo o se il set
     *                              passato contiene elementi nulli
     *
     * @throws IllegalArgumentException se il nome, l'id o il set delle tecniche è vuoto
     */
    public Personaggio(@NonNull String nome, @NonNull String id, @NonNull StatisticheBase statisticheBase, @NonNull Set<TecnicaSpeciale> tecnicheSpeciali) {
        this.verificaNomeAndId(nome, id);
        this.verificaSetTecniche(tecnicheSpeciali);
        this.nome = nome;
        this.id = id;
        this.gestoreStatistiche = new GestoreStatistiche(statisticheBase);
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
     * Restituisce una lista contenente tutte le tecniche speciali di un certo tipo
     * attraverso l'utilizzo di uno stream
     *
     * @param tipo oggetto di tipo {@link TipoTecnica} che specifica la categoria
     *             delle tecniche desiderate
     * @return {@link List} contenente tutte le tecniche del tipo passato
     */

    public List<TecnicaSpeciale> getTecnichePerTipo(@NonNull TipoTecnica tipo) {
        return this.tecnicheSpeciali.stream()
                .filter(tecnica -> tecnica.getTipo() == tipo)
                .toList();
    }

    /**
     * Ripristina le risorse del personaggio semplicemente riassegnando al
     * campo {@link #risorseMatch }una nuova istanza della classe {@link RisorseMatch}.
     *
     * Deve essere usato all'inizio di un nuovo match o dopo un gol,
     * quando stamina e resistenza devono tornare ai valori massimi.
     *
     */
    public void ripristinaRisorseMatch() {
        this.risorseMatch = new RisorseMatch();
    }

    /**
     * Verifica se il personaggio possiede una determinata tecnica.
     *
     * @param tecnica tecnica da cercare
     * @return {@code true} se la tecnica è già presente nel set del
     *          personaggio, {@code false} altrimenti
     *
     * @throws NullPointerException se la tecnica passata è nulla
     */

    public boolean possiedeTecnica(@NonNull TecnicaSpeciale tecnica) {
        return this.tecnicheSpeciali.contains(tecnica);
    }

    /**
     * Aggiunge una nuova tecnica speciale se il personaggio non la ha già.
     *
     * @param tecnica tecnica da aggiungere
     * @return {@code true} se la tecnica non è già presente nel set del
     *          personaggio, {@code false} altrimenti
     *
     * @throws NullPointerException se la tecnica passata è nulla
     */    protected final boolean aggiungiTecnica(@NonNull TecnicaSpeciale tecnica) {
        return this.tecnicheSpeciali.add(tecnica);
    }

    /**
     * Confronta due personaggi basandosi esclusivamente sul loro id (campo
     * utilizzato proprio per definire UNIVOCAMENTE un’istanza di questa classe)
     *
     * @param obj oggetto da confrontare
     *
     * @return {@code true} se i personaggi hanno lo stesso nome e lo stesso overall
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Personaggio))
            return false;
        Personaggio other = (Personaggio) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    private void verificaNomeAndId(String nome, String id) {
        if (nome.isBlank())
            throw new IllegalArgumentException("Nome del personaggio non può essere vuoto!");
        if (id.isBlank())
            throw new IllegalArgumentException("ID del personaggio non può essere vuoto!");
    }

    private void verificaSetTecniche(Set<TecnicaSpeciale> tecnicheSpeciali) {
        if (tecnicheSpeciali.isEmpty())
            throw new IllegalArgumentException("Il personaggio deve avere almeno una tecnica speciale!");
        if (tecnicheSpeciali.contains(null))
            throw new NullPointerException("Le tecniche non possono contenere valori nulli!");
    }

}
