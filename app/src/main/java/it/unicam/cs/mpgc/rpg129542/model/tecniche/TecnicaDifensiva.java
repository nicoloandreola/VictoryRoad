package it.unicam.cs.mpgc.rpg129542.model.tecniche;

import it.unicam.cs.mpgc.rpg129542.model.azioni.DifesaAttaccoDiretto;
import it.unicam.cs.mpgc.rpg129542.model.azioni.DifesaDribbling;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Rappresenta una tecnica speciale difensiva utilizzabile sia come
 * {@link DifesaAttaccoDiretto} che come {@link DifesaDribbling}.
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
public class TecnicaDifensiva extends TecnicaSpeciale
        implements DifesaAttaccoDiretto, DifesaDribbling {

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
    protected int calcolaEffetto(@NonNull Personaggio personaggio) {
        int difesaAttuale = personaggio.getStatisticheEffettive().getDifesa();
        return difesaAttuale + getPotenza();
    }

    /**
     * Utilizza la tecnica e restituisce il valore difensivo prodotto.
     * L'utilizzo e il conseguente consumo di stamina sono delegati
     * al metodo {@link #usa(Personaggio)} della super-classe.
     *
     * @param difensore personaggio che utilizza la tecnica
     *
     * @return valore difensivo prodotto dalla tecnica
     *
     * @throws NullPointerException se il difensore è {@code null}
     * @throws IllegalStateException se il personaggio non possiede la tecnica o non
     *                               dispone della stamina necessaria per usarla
     */
    @Override
    public int esegui(@NonNull Personaggio difensore) {
        return this.usa(difensore);
    }
}
