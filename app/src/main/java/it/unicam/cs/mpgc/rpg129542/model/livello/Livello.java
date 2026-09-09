package it.unicam.cs.mpgc.rpg129542.model.livello;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;


/**
 * Rappresenta un livello di gioco composto da un campo e dagli avversari che il
 * protagonista può affrontare.
 *
 * Per ogni avversario il livello conserva uno {@link StatoAvversario}, che
 * descrive se deve ancora essere affrontato, se è stato selezionato oppure se è
 * già stato sconfitto: per farlo utilizza una {@link Map} che associa
 * a ogni avversario (keys) il suo stato (values).
 *
 * Il livello è considerato completato solamente quando entrambi gli avversari risultano
 * sconfitti; tuttavia per sbloccare il livello successivo basta sconfiggerne uno solo.
 *
 * @author Nicolò Andreola
 */

public class Livello {
    @Getter
    private final int numero;
    @Getter
    private final Campo campo;
    private final Map<Avversario, StatoAvversario> avversari;

    /**
     * Crea un livello con un campo e due avversari inizialmente nello stato
     * {@link StatoAvversario#DA_SCONFIGGERE}.
     *
     * @param numero numero identificativo del livello
     * @param campo campo nel quale si svolgono i match
     * @param avversario1 primo avversario del livello
     * @param avversario2 secondo avversario del livello
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     *
     * @throws IllegalArgumentException se {@code numero} è negativo o se
     *                                  i due avversari sono uguali
     */
    public Livello(int numero, @NonNull Campo campo, @NonNull Avversario avversario1, @NonNull Avversario avversario2) {
        if (numero < 0)
            throw new IllegalArgumentException("Livello non può essere etichettato con un numero negativo!");
        if (avversario1.equals(avversario2))
            throw new IllegalArgumentException("I due avversari del livello devono essere differenti!");
        this.numero = numero;
        this.campo = campo;
        this.avversari = new HashMap<>();
        this.aggiungiAvversario(avversario1);
        this.aggiungiAvversario(avversario2);
    }

    /**
     * Metodo read-only che restituisce una copia non modificabile degli avversari del livello.
     *
     * @return mappa non modificabile degli avversari e dei relativi stati
     */
    public Map<Avversario, StatoAvversario> getAvversari() {
        return Map.copyOf(this.avversari);
    }

    /**
     * Inserisce un avversario nel livello assegnandogli lo stato iniziale
     * {@link StatoAvversario#DA_SCONFIGGERE}.
     *
     * Poiché gli avversari sono usati come chiavi della mappa, se
     * l'avversario è già presente il suo stato corrente viene sostituito con
     * {@code DA_SCONFIGGERE}, in accordo con il metodo {@link Map#put(Object, Object)}.
     *
     * @param avversario avversario da aggiungere
     *
     * @throws NullPointerException se {@code avversario} è {@code null}
     */
    public void aggiungiAvversario(@NonNull Avversario avversario) {
        this.avversari.put(avversario, StatoAvversario.DA_SCONFIGGERE);
    }

    /**
     * Restituisce lo stato attuale di un avversario all'interno del livello.
     *
     * @param avversario avversario del quale ottenere lo stato
     *
     * @return stato corrente dell'avversario nel livello
     *
     * @throws NullPointerException se {@code avversario} è {@code null}
     *
     * @throws IllegalArgumentException se l'avversario non appartiene al livello
     *
     */
    public StatoAvversario getStatoAvversario(@NonNull Avversario avversario) {
        StatoAvversario stato = this.avversari.get(avversario);
        if(stato == null)
            throw new IllegalArgumentException("L'avversario non appartiene a questo livello!");
        return stato;
    }

    /**
     * Permette di selezionare l'avversario che il protagonista intende affrontare.
     *
     * Un avversario già sconfitto non può essere selezionato nuovamente.
     * L'eventuale avversario selezionato in precedenza deve tornare nello stato
     * {@link StatoAvversario#DA_SCONFIGGERE}, così che nel livello sia presente
     * al massimo un avversario selezionato.
     *
     * @param avversario avversario da selezionare
     *
     * @throws NullPointerException se {@code avversario} è {@code null}
     *
     * @throws IllegalArgumentException se l'avversario non appartiene al livello
     *
     * @throws IllegalStateException se l'avversario è già stato sconfitto
     */
    public void selezionaAvversario(@NonNull Avversario avversario) {
        StatoAvversario stato = this.avversari.get(avversario);
        if (stato == null)
            throw new IllegalArgumentException("L'avversario non appartiene al livello");
        if (stato == StatoAvversario.SCONFITTO)
            throw new IllegalStateException("Questo avversario è gia stato sconfitto");

        // Deseleziona un eventuale avversario selezionato precedentemente
        for (Avversario a : this.avversari.keySet()) {
            if(this.getStatoAvversario(a) == StatoAvversario.SELEZIONATO)
                this.avversari.put(a, StatoAvversario.DA_SCONFIGGERE);
        }
        this.avversari.put(avversario, StatoAvversario.SELEZIONATO);
    }

    /**
     * Verifica se nel livello è presente un avversario attualmente selezionato.
     *
     * @return {@code true} se uno degli avversari si trova nello stato
     *         {@link StatoAvversario#SELEZIONATO}, {@code false} altrimenti
     */
    public boolean isAvversarioSelezionato() {
        return this.avversari.containsValue(StatoAvversario.SELEZIONATO);
    }

    /**
     * Cerca e restituisce l'avversario attualmente selezionato nel livello.
     *
     * @return l'avversario selezionato
     *
     * @throws IllegalStateException se nessun avversario è attualmente selezionato
     */
    public Avversario getAvversarioSelezionato() {
        return this.avversari.entrySet().stream()
                .filter(entry -> entry.getValue() == StatoAvversario.SELEZIONATO)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nessun avversario selezionato!"));
    }

    /**
     * Registra la vittoria del protagonista contro l'avversario selezionato,
     * modificandone lo stato in {@link StatoAvversario#SCONFITTO}.
     *
     * @param avversario avversario contro il quale è stata ottenuta la vittoria
     *
     * @throws NullPointerException se {@code avversario} è {@code null}
     *
     * @throws IllegalArgumentException se l'avversario non è quello attualmente
     *                                  selezionato (ciò comprende anche il caso
     *                                  in cui non appartenga al livello)
     */
    public void registraVittoria(@NonNull Avversario avversario) {
        if (this.avversari.get(avversario) != StatoAvversario.SELEZIONATO)
            throw new IllegalArgumentException("L'avversario non è quello attualmente selezionato");
        this.avversari.put(avversario, StatoAvversario.SCONFITTO);
    }

    /**
     * Verifica se il protagonista può avanzare al livello successivo.
     *
     * L'avanzamento viene sbloccato non appena almeno uno degli
     * avversari del livello viene sconfitto, non è necessario
     * sconfiggerli entrambi (quello solo se lo si vuole completare).
     *
     * @return {@code true} se almeno un avversario è stato sconfitto,
     *         {@code false} altrimenti
     */
    public boolean isLivelloSuccessivoSbloccato() {
        return this.avversari.containsValue(StatoAvversario.SCONFITTO);
    }

    /**
     * Verifica se il livello è stato completato interamente.
     *
     * Un livello è considerato completato quando tutti gli avversari
     * presenti risultano sconfitti.
     *
     * @return {@code true} se tutti gli avversari sono stati sconfitti,
     *         {@code false} altrimenti
     */
    public boolean isCompletato() {
        for (StatoAvversario stato : this.avversari.values())
            if(stato != StatoAvversario.SCONFITTO)
                return false;
        return true;
    }

    /**
     * Confronta 2 livelli basandosi esclusivamente sul loro numero
     *
     * @param obj oggetto da confrontare
     *
     * @return {@code true} se i due livelli hanno lo stesso numero,
     *         {@code false} altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Livello))
            return false;

        Livello other = (Livello) obj;
        return this.numero == other.numero;
    }

    /**
     * Calcola l'hashcode del livello partendo dal suo numero
     *
     * @return hashcode del livello
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(this.numero);
    }

}
