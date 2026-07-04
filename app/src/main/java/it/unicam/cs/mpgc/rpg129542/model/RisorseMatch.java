package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;

/**
 * Rappresenta le risorse temporanee possedute da un qualsiasi
 * {@link Personaggio} durante un match. Le risorse gestite sono: <br>
 *
 * {@link #resistenza}: rappresenta la capacità del personaggio di
 * rimanere in combattimento. Diminuisce quando l'avversario sferra
 * un attacco, e quando raggiunge 0 il personaggio subisce gol. <br>
 *
 * {@link #stamina}: rappresenta l'energia utilizzata dal
 * personaggio per eseguire le tecniche speciali. Una tecnica può
 * essere utilizzata solamente se è disponibile una quantità di
 * stamina almeno pari al suo costo. <br>
 *
 * Le operazioni di consumo e recupero mantengono sempre i valori
 * nell'intervallo compreso tra zero e il massimo consentito.
 *
 * @author Nicolò Andreola
 */

@Getter
public class RisorseMatch {

    public static final int STAMINA_MASSIMA = 100;
    public static final int RESISTENZA_MASSIMA = 100;

    private int stamina;
    private int resistenza;

    /**
     * Crea le risorse di un nuovo match inizializzandole ai valori massimi
     * utilizzando le costanti {@link #STAMINA_MASSIMA} e {@link #RESISTENZA_MASSIMA}.
     */
    public RisorseMatch() {
        this.stamina = STAMINA_MASSIMA;
        this.resistenza = RESISTENZA_MASSIMA;
    }

    /**
     * Riduce la stamina della quantità specificata, senza scendere sotto zero.
     *
     * @param quantita stamina da consumare
     *
     * @throws IllegalArgumentException se la quantità è negativa
     */
    public void consumaStamina(int quantita) {
        this.verificaQuantita(quantita);
        this.stamina = Math.max(0, this.stamina - quantita);
    }


    /**
     * Recupera stamina della quantità specificata, senza superare il valore massimo.
     *
     * @param quantita stamina da recuperare
     *
     * @throws IllegalArgumentException se la quantità è negativa
     */
    public void recuperaStamina(int quantita) {
        this.verificaQuantita(quantita);
        this.stamina = Math.min(STAMINA_MASSIMA, this.stamina + quantita);
    }

    /**
     * Riduce la resistenza della quantità specificata, senza scendere sotto zero.
     *
     * @param quantita resistenza da sottrarre
     *
     * @throws IllegalArgumentException se la quantità è negativa
     */
    public void riduciResistenza(int quantita) {
        this.verificaQuantita(quantita);
        this.resistenza = Math.max(0, this.resistenza - quantita);
    }

    /**
     * Recupera la resistenza della quantità specificata, senza superare il valore massimo.
     *
     * @param quantita resistenza da recuperare
     * @throws IllegalArgumentException se la quantità è negativa
     */
    public void recuperaResistenza(int quantita) {
        this.verificaQuantita(quantita);
        this.resistenza = Math.min(RESISTENZA_MASSIMA, this.resistenza + quantita);
    }

    /**
     * Verifica se è disponibile almeno la quantità di stamina richiesta.
     *
     * @param quantita quantità richiesta
     *
     * @return {@code true} se la stamina disponibile è sufficiente, {@code false} altrimenti
     *
     * @throws IllegalArgumentException se la quantità è negativa
     */
    public boolean haStamina(int quantita) {
        this.verificaQuantita(quantita);
        return this.stamina >= quantita;
    }

    /**
     * Verifica se il personaggio dispone ancora di resistenza.
     *
     * @return {@code true} se la resistenza è maggiore di zero, {@code false} altrimenti
     */
    public boolean haResistenza() {
        return !(resistenza == 0);
    }

    private void verificaQuantita(int quantita) {
        if (quantita < 0)
            throw new IllegalArgumentException("La quantità non può essere negativa!");
    }
}
