package it.unicam.cs.mpgc.rpg129542.model.azioni;

import it.unicam.cs.mpgc.rpg129542.model.match.EsitoTurno;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSupporto;
import lombok.NonNull;

/**
 * Definisce il contratto comune delle azioni che un personaggio può
 * eseguire quando possiede la palla e ricopre il ruolo di attaccante.
 *
 * L'azione riceve i due personaggi coinvolti nel turno, la risposta scelta
 * dal difensore e la logica utilizzata per risolvere gli eventuali confronti
 * tra valori offensivi e difensivi.
 *
 * @author Nicolò Andreola
 */
public interface AzioneAttaccante {

    /**
     * Esegue l'azione dell'attaccante e ne determina l'esito.
     * Se questo metodo viene chiamato su un oggetto di tipo {@link TecnicaSupporto}
     * il valore del parametro "difesa" deve essere {@code null},
     * per questo tale parametro non è annotato con {@code @NonNull}.
     *
     * @param attaccante personaggio che esegue l'azione
     * @param difensore personaggio che risponde all'azione
     * @param difesa risposta scelta dal difensore; può essere {@code null}
     * @param logicaMatch logica utilizzata per risolvere il turno
     *
     * @return esito prodotto dall'esecuzione dell'azione
     *
     * @throws NullPointerException se uno dei parametri, eccetto "difesa", è {@code null}
     * @throws IllegalArgumentException se la risposta difensiva non è
     *                                  compatibile con l'azione eseguita
     */
    EsitoTurno esegui(@NonNull Personaggio attaccante, @NonNull Personaggio difensore,
                              AzioneDifensore difesa, @NonNull LogicaMatch logicaMatch);
    /**
     * Restituisce il nome con cui l'azione viene identificata e mostrata
     * all'interno dell'interfaccia di gioco.
     *
     * @return nome dell'azione offensiva
     */
    String getNome();
}