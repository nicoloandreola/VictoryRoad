package it.unicam.cs.mpgc.rpg129542.model.azioni;

import it.unicam.cs.mpgc.rpg129542.model.match.EsitoTurno;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Rappresenta l'azione con cui l'attaccante tenta di superare il difensore
 * sfruttando la propria statistica di agilità.
 *
 * Il dribbling può ricevere esclusivamente risposte che implementano
 * {@link DifesaDribbling}. Il confronto tra il valore del dribbling
 * e quello della risposta difensiva viene delegato a {@link LogicaMatch}.
 *
 * Se il dribbling riesce, l'attaccante mantiene il possesso della palla;
 * in caso contrario il possesso passa al difensore.
 *
 * @author Nicolò Andreola
 */
public class Dribbling implements AzioneAttaccante {

    /**
     * Esegue il dribbling utilizzando l'agilità effettiva dell'attaccante.
     *
     * @param attaccante personaggio che tenta il dribbling
     * @param difensore personaggio che prova a contrastarlo
     * @param difesa risposta scelta dal difensore
     * @param logicaMatch logica utilizzata per risolvere il confronto
     *
     * @return {@link EsitoTurno#DRIBBLING_RIUSCITO} se l'attaccante mantiene
     *         il possesso, {@link EsitoTurno#PALLA_PERSA} altrimenti
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     * @throws IllegalArgumentException se la risposta scelta non implementa
     *                              {@link DifesaDribbling} o è {@code null}
     */
    @Override
    public EsitoTurno esegui(@NonNull Personaggio attaccante, @NonNull Personaggio difensore,
                             AzioneDifensore difesa, @NonNull LogicaMatch logicaMatch) {
        if (!(difesa instanceof DifesaDribbling risposta))
            throw new IllegalArgumentException("La risposta difensiva non è valida contro un dribbling!");
        int valoreDribbling = attaccante.getStatisticheEffettive().getAgilita();
        int valoreContrasto = risposta.esegui(difensore);
        return logicaMatch.risolviDribbling(valoreDribbling, valoreContrasto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNome() {
        return "Dribbling";
    }
}
