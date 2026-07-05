package it.unicam.cs.mpgc.rpg129542.model;

import lombok.NonNull;

/**
 * Rappresenta una tecnica speciale difensiva.
 *
 * L'efficacia viene calcolata sommando la potenza della tecnica
 * al valore di difesa effettivo dell'utilizzatore, quindi la logica
 * implementata da questa classe si può esprimere con la formula:
 *
 * <strong> effetto = difesa effettivo + potenza </strong>
 *
 *
 * @author Nicolò Andreola
 */
public class TecnicaDifensiva extends TecnicaSpeciale {

    /**
     * Costruisce una tecnica speciale difensiva.
     *
     * @param nome nome identificativo della tecnica
     * @param descrizione descrizione dell'effetto
     * @param potenza valore aggiunto alla difesa dell'utilizzatore
     * @param costoStamina stamina necessaria per utilizzare la tecnica
     *
     * @throws NullPointerException se nome o descrizione sono nulli
     * @throws IllegalArgumentException se i parametri non rispettano
     *                                  i vincoli di {@link TecnicaSpeciale}
     */
    public TecnicaDifensiva(String nome, String descrizione, int potenza, int costoStamina) {
        super(nome, descrizione, potenza, costoStamina);
    }

    /**
     * {@inheritDoc}
     *
     * @return sempre {@link TipoTecnica#DIFENSIVA}
     */
    @Override
    public TipoTecnica getTipo() {
        return TipoTecnica.DIFENSIVA;
    }

    /**
     * Calcola l'efficacia sommando la potenza della tecnica
     * alla difesa effettiva del personaggio che la utilizza.
     *
     * @param personaggio personaggio su cui viene usata la tecnica
     *
     * @return difesa effettiva più potenza della tecnica
     */
    @Override
    public int calcolaEffetto(@NonNull Personaggio personaggio) {
        int difesaAttuale = personaggio.getStatisticheEffettive().getDifesa();
        return difesaAttuale + getPotenza();
    }
}
