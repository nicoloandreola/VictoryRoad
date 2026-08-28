package it.unicam.cs.mpgc.rpg129542.model.azioni;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Rappresenta la risposta difensiva base utilizzabile contro un dribbling,
 * per questo implementa l'interfaccia {@link DifesaDribbling}.
 *
 * La sua efficacia viene determinata dall'agilità effettiva del difensore,
 * che viene confrontata con quella dell'attaccante per stabilire se
 * quest'ultimo mantiene il possesso della palla.
 *
 * @author Nicolò Andreola
 */
public class Contrasto implements DifesaDribbling {

    /**
     * Calcola il valore del contrasto utilizzando l'agilità
     * effettiva del difensore.
     *
     * @param difensore personaggio che tenta il contrasto
     *
     * @return valore corrente della statistica di agilità
     *
     * @throws NullPointerException se il difensore è {@code null}
     */
    @Override
    public int esegui(@NonNull Personaggio difensore) {
        return difensore.getStatisticheEffettive().getAgilita();
    }
}
