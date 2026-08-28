package it.unicam.cs.mpgc.rpg129542.model.tecniche;

import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneAttaccante;
import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneDifensore;
import it.unicam.cs.mpgc.rpg129542.model.azioni.DifesaAttaccoDiretto;
import it.unicam.cs.mpgc.rpg129542.model.match.EsitoTurno;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Rappresenta una tecnica speciale offensiva utilizzabile
 * direttamente come {@link AzioneAttaccante}.
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
public class TecnicaOffensiva extends TecnicaSpeciale implements AzioneAttaccante {

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
     * @param personaggio personaggio su cui viene usata la tecnica
     *
     * @return attacco effettivo più potenza della tecnica
     */
    @Override
    protected int calcolaEffetto(@NonNull Personaggio personaggio) {
        int attaccoAttuale = personaggio.getStatisticheEffettive().getAttacco();
        return attaccoAttuale + this.getPotenza();
    }

    /**
     * Utilizza la tecnica come azione offensiva del turno.
     * L'utilizzo e il conseguente consumo di stamina sono delegati
     * al metodo {@link #usa(Personaggio)} della super-classe.
     *
     * @param attaccante  personaggio che utilizza la tecnica
     * @param difensore   personaggio che difende
     * @param difesa      risposta scelta dal difensore
     * @param logicaMatch logica utilizzata per risolvere l'attacco
     *
     * @return esito prodotto dal confronto tra attacco e difesa
     *
     * @throws NullPointerException     se uno dei parametri, eccetto "difesa" è {@code null}
     *
     * @throws IllegalArgumentException se la risposta scelta non implementa {@link DifesaAttaccoDiretto}
     *
     * @throws IllegalStateException se il personaggio non possiede la tecnica o non
     *                                   dispone della stamina necessaria per usarla
     */
    @Override
    public EsitoTurno esegui(@NonNull Personaggio attaccante, @NonNull Personaggio difensore,
                             AzioneDifensore difesa, @NonNull LogicaMatch logicaMatch) {
        if (!(difesa instanceof DifesaAttaccoDiretto risposta))
            throw new IllegalArgumentException("Risposta non valida contro una tecnica offensiva!");
        int valoreAttacco = this.usa(attaccante);
        int valoreDifesa = risposta.esegui(difensore);
        return logicaMatch.risolviAttaccoDiretto(difensore, valoreAttacco, valoreDifesa);
    }
}
