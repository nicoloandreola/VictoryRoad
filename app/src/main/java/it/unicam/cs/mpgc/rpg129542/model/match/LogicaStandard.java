package it.unicam.cs.mpgc.rpg129542.model.match;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Implementazione standard della logica di risoluzione definita
 * dall'interfaccia {@link LogicaMatch}: gli attacchi diretti possono
 * ridurre la resistenza del difensore, mentre i dribbling vengono risolti
 * confrontando l'agilità dell'attaccante con il valore prodotto dalla risposta difensiva.
 *
 * Le formule utilizzate per determinare il danno e il successo
 * di un dribbling sono incapsulate in metodi privati, così da poter
 * essere modificate senza alterare la struttura generale della classe.
 *
 * @author Nicolò Andreola
 */
public class LogicaStandard implements LogicaMatch {

    /**
     * {@inheritDoc}
     *
     * Il danno viene determinato dalla differenza tra valore offensivo
     * e valore difensivo. Se il risultato non produce danno, l'attacco
     * viene respinto; altrimenti la resistenza del difensore viene ridotta.
     *
     * Se la resistenza raggiunge zero, l'attacco produce un gol.
     *
     * @return {@link EsitoTurno#ATTACCO_RESPINTO} se non viene inflitto danno,
     *         {@link EsitoTurno#DANNO_INFLITTO} se il difensore subisce danno
     *         ma conserva resistenza, oppure {@link EsitoTurno#GOL_SEGNATO}
     *         se la resistenza del difensore raggiunge zero
     */
    @Override
    public EsitoTurno risolviAttaccoDiretto(@NonNull Personaggio difensore, int valoreAttacco, int valoreDifesa) {
        int danno = this.calcolaDanno(valoreAttacco, valoreDifesa);
        if (danno == 0)
            return EsitoTurno.ATTACCO_RESPINTO;
        else
            difensore.subisciDanno(danno);
        return difensore.getResistenza() == 0 ? EsitoTurno.GOL_SEGNATO : EsitoTurno.DANNO_INFLITTO;
    }

    private int calcolaDanno(int valoreAttacco, int valoreDifesa) {
        return Math.max(0, valoreAttacco - valoreDifesa);
    }

    /**
     * {@inheritDoc}
     *
     * Il dribbling viene considerato riuscito quando il suo valore
     * è strettamente maggiore di quello prodotto dalla risposta
     * del difensore.
     */
    @Override
    public EsitoTurno risolviDribbling(int valoreDribbling, int valoreContrasto) {
        if (this.isDribblingRiuscito(valoreDribbling, valoreContrasto))
            return EsitoTurno.DRIBBLING_RIUSCITO;
        return EsitoTurno.PALLA_PERSA;
    }

    private boolean isDribblingRiuscito(int valoreDribbling, int valoreContrasto) {
        return valoreDribbling > valoreContrasto;
    }
}
