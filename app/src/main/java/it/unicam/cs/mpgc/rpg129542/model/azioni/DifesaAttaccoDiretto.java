package it.unicam.cs.mpgc.rpg129542.model.azioni;

import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaOffensiva;

/**
 * Specializza {@link AzioneDifensore} identificando le azioni che possono
 * essere utilizzate come risposta a un attacco diretto.
 *
 * Nel modello corrente sono considerati attacchi diretti sia un
 * {@link Tiro} sia l'utilizzo di una {@link TecnicaOffensiva}.
 *
 * Questa interfaccia non introduce nuovi comportamenti rispetto a
 * {@link AzioneDifensore}, ma permette di esprimere attraverso il sistema
 * dei tipi quali azioni difensive sono compatibili con questo tipo di attacco.
 *
 * @author Nicolò Andreola
 */
public interface DifesaAttaccoDiretto extends AzioneDifensore {
}
