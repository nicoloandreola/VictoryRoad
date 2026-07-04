package it.unicam.cs.mpgc.rpg129542.model;

/**
 * Rappresenta una tecnica speciale offensiva.
 *
 * L'efficacia viene calcolata sommando la potenza della tecnica
 * al valore di attacco effettivo dell'utilizzatore, quindi la logica
 * implementata da questa classe si può esprimere con la formula:
 *
 * <strong> effetto = attacco effettivo + potenza </strong>
 *
 *
 * @author Nicolò Andreola
 */
public class TecnicaOffensiva extends TecnicaSpeciale {

    /**
     * Costruisce una tecnica speciale offensiva.
     *
     * @param nome nome identificativo della tecnica
     * @param descrizione descrizione dell'effetto
     * @param potenza valore aggiunto all'attacco dell'utilizzatore
     * @param costoStamina stamina necessaria per utilizzare la tecnica
     *
     * @throws NullPointerException se nome o descrizione sono nulli
     * @throws IllegalArgumentException se i parametri non rispettano
     *                                  i vincoli di {@link TecnicaSpeciale}
     */
    public TecnicaOffensiva(String nome, String descrizione, int potenza, int costoStamina) {
        super(nome, descrizione, potenza, costoStamina);
    }

    /**
     * {@inheritDoc}
     *
     * @return sempre {@link TipoTecnica#OFFENSIVA}
     */
    @Override
    public TipoTecnica getTipo() {
        return TipoTecnica.OFFENSIVA;
    }

    /**
     * Calcola l'efficacia sommando la potenza della tecnica
     * all'attacco effettivo del personaggio che la utilizza.
     *
     * @param statistiche statistiche effettive dell'utilizzatore
     * @return attacco effettivo più potenza della tecnica
     */
    @Override
    public int calcolaEffetto(StatisticheBase statistiche) {
        return statistiche.getAttacco() + this.getPotenza();
    }
}
