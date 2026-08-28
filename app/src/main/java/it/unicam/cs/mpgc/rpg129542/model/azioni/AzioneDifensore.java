package it.unicam.cs.mpgc.rpg129542.model.azioni;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import lombok.NonNull;

/**
 * Definisce il contratto comune delle azioni che un personaggio può
 * eseguire quando ricopre il ruolo di difensore durante un turno.
 *
 * Ogni azione difensiva produce un valore che viene utilizzato per
 * risolvere l'interazione con l'azione scelta dall'attaccante.
 *
 * Le interfacce più specifiche {@link DifesaAttaccoDiretto} e
 * {@link DifesaDribbling} permettono invece di distinguere in quali
 * situazioni una determinata azione difensiva può essere utilizzata.
 *
 * @author Nicolò Andreola
 */
public interface AzioneDifensore {

    /**
     * Esegue l'azione difensiva e ne restituisce il valore prodotto.
     *
     * Il significato del valore dipende dall'implementazione concreta:
     * può rappresentare, ad esempio, il valore di una {@link Parata}, di un
     * {@link Contrasto} oppure l'efficacia di una {@link TecnicaSpeciale}.
     *
     * @param difensore personaggio che esegue l'azione difensiva
     *
     * @return valore prodotto dall'azione
     *
     * @throws NullPointerException se il difensore è {@code null}
     */
    int esegui(@NonNull Personaggio difensore);
}
