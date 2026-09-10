package it.unicam.cs.mpgc.rpg129542.model.azioni;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Rappresenta la risposta difensiva base utilizzabile contro un attacco
 * diretto, e infatti implementa l'interfaccia {@link DifesaAttaccoDiretto}.
 *
 * La sua efficacia corrisponde alla statistica di difesa effettiva
 * del personaggio che la esegue.
 *
 * @author Nicolò Andreola
 */
public class Parata implements DifesaAttaccoDiretto {

    /**
     * Calcola il valore della parata utilizzando la difesa effettiva
     * del personaggio.
     *
     * @param difensore personaggio che esegue la parata
     *
     * @return valore corrente della statistica di difesa
     *
     * @throws NullPointerException se il difensore è {@code null}
     */
    @Override
    public int esegui(@NonNull Personaggio difensore) {
        return difensore.getStatisticheEffettive().getDifesa();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNome() {
        return "Parata";
    }
}
