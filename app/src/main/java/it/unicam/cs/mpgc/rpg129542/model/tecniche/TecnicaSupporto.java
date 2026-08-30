package it.unicam.cs.mpgc.rpg129542.model.tecniche;

import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneAttaccante;
import it.unicam.cs.mpgc.rpg129542.model.azioni.AzioneDifensore;
import it.unicam.cs.mpgc.rpg129542.model.azioni.DifesaDribbling;
import it.unicam.cs.mpgc.rpg129542.model.match.EsitoTurno;
import it.unicam.cs.mpgc.rpg129542.model.match.LogicaMatch;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.NonNull;

/**
 * Rappresenta una tecnica speciale che permette al personaggio di recuperare parte
 * della stamina durante una partita. Le tecniche di supporto non consumano
 * stamina, il costo di queste tecniche è sempre pari a 0: sono la rinuncia
 * ad attaccare e la perdita del possesso a rappresentare il "costo" della tecnica
 *
 * La tecnica può essere utilizzata dall'attaccante rinunciando alla
 * propria azione offensiva oppure dal difensore come risposta a un
 * dribbling, rinunciando in questo caso a tentare un contrasto.
 *
 * L'efficacia viene calcolata sommando la potenza della tecnica
 * al valore di stamina attuale del personaggio, quindi la logica
 * implementata da questa classe si può esprimere con la formula:
 *
 * <strong> effetto = stamina effettiva + potenza </strong>
 *
 * @author Nicolò Andreola
 */

public class TecnicaSupporto extends TecnicaSpeciale
        implements AzioneAttaccante, DifesaDribbling {
    /**
     * Costruisce una tecnica speciale di supporto.
     *
     * @param nome nome identificativo della tecnica
     * @param id id della tecnica
     * @param potenza valore aggiunto alla stamina del personaggio
     *
     * @throws NullPointerException se nome o id sono nulli
     *
     * @throws IllegalArgumentException se i parametri non rispettano
     *                                  i vincoli di {@link TecnicaSpeciale}
     */
    public TecnicaSupporto(String nome, String id, int potenza) {
        super(nome, id, potenza, 0);
    }

    /**
     * {@inheritDoc}
     *
     * @return sempre {@link TipoTecnica#SUPPORTO}
     */
    @Override
    public TipoTecnica getTipo() {
        return TipoTecnica.SUPPORTO;
    }

    /**
     * Calcola la quantità di stamina recuperata dal personaggio che la utilizza.
     *
     * @param personaggio personaggio su cui viene usata la tecnica
     *
     * @return quantità di stamina recuperata
     */
    @Override
    protected int calcolaEffetto(@NonNull Personaggio personaggio) {
        return this.getPotenza();
    }

    /**
     * Permette all'attaccante di utilizzare una tecnica di supporto al posto di
     * un'azione offensiva, recuperando stamina e lasciando il possesso all'avversario.
     *
     * @param attaccante personaggio che utilizza la tecnica
     * @param difensore personaggio avversario
     * @param difesa assenza di risposta, rappresentata da {@code null}
     * @param logicaMatch logica associata al match
     *
     * @return {@link EsitoTurno#STAMINA_RECUPERATA}
     *
     * @throws NullPointerException se uno dei parametri, eccetto "difesa" è {@code null}
     *
     * @throws IllegalArgumentException se viene fornita una risposta diversa da {@code null}
     */
    @Override
    public EsitoTurno esegui(@NonNull Personaggio attaccante, @NonNull Personaggio difensore,
            AzioneDifensore difesa, @NonNull LogicaMatch logicaMatch) {
        // Se l'attaccante utilizza una TECNICA di SUPPORTO, non è richiesta alcuna risposta
        // del difensore, poiché la palla passa a quest'ultimo e si procede con il turno successivo.
        if (difesa != null)
            throw new IllegalArgumentException("La tecnica di supporto non richiede una difesa!");
        int quantita = this.usa(attaccante);
        attaccante.recuperaStamina(quantita);
        return EsitoTurno.STAMINA_RECUPERATA;
    }

    /**
     * Permette al difensore di utilizzare la tecnica di supporto come alternativa
     * al contrasto, recuperando stamina ma rinunciando a opporsi al dribbling.
     *
     * Poiché il personaggio non effettua alcun contrasto, il metodo restituisce
     * {@code 0} come valore da opporre all'agilità dell'attaccante.
     *
     * @param difensore personaggio che utilizza la tecnica
     *
     * @return {@code 0}, poiché non viene effettuato alcun contrasto
     *
     * @throws NullPointerException se il difensore è {@code null}
     */
    @Override
    public int esegui(@NonNull Personaggio difensore) {
        int quantita = this.usa(difensore);
        difensore.recuperaStamina(quantita);
        return 0;
    }
}
