package it.unicam.cs.mpgc.rpg129542.model.azioni;

import it.unicam.cs.mpgc.rpg129542.model.match.EsitoTurno;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Rappresenta l'azione offensiva base con cui un personaggio tenta di
 * segnare utilizzando la propria statistica di attacco.
 *
 * Il tiro può essere contrastato esclusivamente da un'azione che implementa
 * {@link DifesaAttaccoDiretto}. Il confronto tra il valore di attacco
 * e quello prodotto dalla difesa viene delegato a {@link LogicaMatch}.
 *
 * @author Nicolò Andreola
 */
public class Tiro implements AzioneAttaccante {

    /**
     * Esegue un tiro utilizzando come valore offensivo l'attacco
     * effettivo del personaggio.
     *
     * @param attaccante  personaggio che esegue il tiro
     * @param difensore   personaggio che difende
     * @param difesa      risposta scelta dal difensore
     * @param logicaMatch logica utilizzata per risolvere l'attacco
     *
     * @return esito prodotto dal confronto tra attacco e difesa
     *
     * @throws NullPointerException     se uno dei parametri, eccetto "difesa" è {@code null}
     * @throws IllegalArgumentException se la risposta scelta non implementa
     *                                  {@link DifesaAttaccoDiretto} o è {@code null}
     */
    @Override
    public EsitoTurno esegui(@NonNull Personaggio attaccante, @NonNull Personaggio difensore,
                             AzioneDifensore difesa, @NonNull LogicaMatch logicaMatch) {
        if (!(difesa instanceof DifesaAttaccoDiretto risposta))
            throw new IllegalArgumentException("La risposta difensiva non è valida contro un tiro!");
        int valoreAttacco = attaccante.getStatisticheEffettive().getAttacco();
        int valoreDifesa = risposta.esegui(difensore);
        return logicaMatch.risolviAttaccoDiretto(difensore, valoreAttacco, valoreDifesa);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNome() {
        return "Tiro";
    }
}