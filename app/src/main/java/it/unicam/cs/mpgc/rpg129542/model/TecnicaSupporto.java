package it.unicam.cs.mpgc.rpg129542.model;

import lombok.NonNull;

/**
 * Rappresenta una tecnica speciale che permette di recuperare parte
 * della resistenza durante una partita.
 *
 * L'efficacia viene calcolata sommando la potenza della tecnica
 * al valore di resistenza attuale del personaggio, quindi la logica
 * implementata da questa classe si può esprimere con la formula:
 *
 * <strong> effetto = resistenza effettiva + potenza </strong>
 *
 * @author Nicolò Andreola
 */

public class TecnicaSupporto extends TecnicaSpeciale {
    /**
     * Costruisce una tecnica speciale di supporto.
     *
     * @param nome nome identificativo della tecnica
     * @param descrizione descrizione dell'effetto
     * @param potenza valore aggiunto alla resistenza del personaggio
     * @param costoStamina stamina necessaria per utilizzare la tecnica
     *
     * @throws NullPointerException se nome o descrizione sono nulli
     * @throws IllegalArgumentException se i parametri non rispettano
     *                                  i vincoli di {@link TecnicaSpeciale}
     */
    public TecnicaSupporto(String nome, String descrizione, int potenza, int costoStamina) {
        super(nome, descrizione, potenza, costoStamina);
    }

    /**
     * {@inheritDoc}
     *
     * @return sempre {@link TipoTecnica#SUPPORTO}
     */
    @Override
    public TipoTecnica getTipo() {
        return TipoTecnica.SUPPORTO;
    }

    /**
     * Calcola l'efficacia sommando la potenza della tecnica
     * alla resistenza effettiva del personaggio che la utilizza.
     *
     * @param personaggio personaggio su cui viene usata la tecnica
     *
     * @return nuovo valore della resistenza aumentato, sempre entro
     *         il limite massimo stabilito da {@link RisorseMatch#RESISTENZA_MASSIMA}
     */
    @Override
    public int calcolaEffetto(@NonNull Personaggio personaggio) {
        return Math.min(personaggio.getResistenza() + getPotenza(), RisorseMatch.RESISTENZA_MASSIMA);
    }
}
