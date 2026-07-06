package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;
import lombok.NonNull;

/**
 * Rappresenta il campo nel quale si svolge un match.
 *
 * Ogni campo possiede un modificatore delle statistiche che descrive l'effetto
 * ambientale prodotto durante il match. Questa classe si limita a definire tale
 * effetto: la sua applicazione ai personaggi è responsabilità di
 * {@link GestoreStatistiche}.
 *
 * L'attributo {@link #nome} funge da identificatore univoco del campo, infatti
 * è utilizzato sia per definire il metodo {@link #equals(Object)}, che per
 * ricavarne il path della sua immagine con link {@link #getPercorsoImmagine()}
 *
 * Le istanze sono immutabili: nome, descrizione e modificatore non possono
 * essere cambiati dopo la costruzione.
 *
 * @author Nicolò Andreola
 */
@Getter
public class Campo {

    // Costanti usata per ricavare il percorso dell'immagine a partire dal nome del
    // campo: definite in modo che, se un giorno cambi cartella, modifichi una sola riga
    private static final String CARTELLA_IMMAGINI = "/immagini/campi/";
    private static final String ESTENSIONE_IMMAGINE = ".png";

    private final String nome;
    private final String descrizione;
    private final ModificatoreStatistiche modificatore;

    /**
     * Costruisce un campo da gioco associandogli il proprio modificatore
     *
     * @param nome nome identificativo del campo
     * @param descrizione descrizione del campo e del suo effetto
     * @param modificatore modificatore associato al campo
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     *
     * @throws IllegalArgumentException se nome o descrizione sono vuoti
     */
    public Campo(@NonNull String nome, @NonNull String descrizione, @NonNull ModificatoreStatistiche modificatore) {
        if (nome.isBlank())
            throw new IllegalArgumentException("Il nome del campo non può essere vuoto!");
        if (descrizione.isBlank())
            throw new IllegalArgumentException("La descrizione del campo non può essere vuota!");
        this.nome = nome;
        this.descrizione = descrizione;
        this.modificatore = modificatore;
    }

    /**
     * Applica l'effetto del campo alle statistiche ricevute.
     *
     * @param statistiche statistiche da modificare
     *
     * @return nuove statistiche modificate dall'effetto del campo
     *
     * @throws NullPointerException se le statistiche sono nulle
     */
    public StatisticheBase applicaEffetto(@NonNull StatisticheBase statistiche) {
        return this.modificatore.applica(statistiche);
    }

    /**
     * Restituisce il percorso dell'immagine associata al campo, permettendo
     * alla view di caricare la risorsa grafica senza inserire dipendenze
     * di JavaFx all interno del model. Tuttavia in questo modo, il
     * nome del file immagine deve corrispondere al {@link #nome} del campo
     *
     * @return percorso dell'immagine associata al campo
     */
    public String getPercorsoImmagine() {
        return CARTELLA_IMMAGINI + this.nome + ESTENSIONE_IMMAGINE;
    }

    /**
     * Confronta due campi basandosi esclusivamente sul loro nome.
     *
     * @param obj oggetto da confrontare
     *
     * @return {@code true} se i due campi hanno lo stesso nome,
     *         {@code false} altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Campo))
            return false;
        Campo other = (Campo) obj;
        return this.nome.equals(other.nome);
    }

    /**
     * Calcola l'hashcode del campo usando il suo nome.
     *
     * @return hashcode del campo
     */
    @Override
    public int hashCode() {
        return this.nome.hashCode();
    }
}
