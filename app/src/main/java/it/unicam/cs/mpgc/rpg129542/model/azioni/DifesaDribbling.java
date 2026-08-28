package it.unicam.cs.mpgc.rpg129542.model.azioni;

/**
 * Specializza {@link AzioneDifensore} identificando le azioni che possono
 * essere utilizzate come risposta a un {@link Dribbling}.
 *
 * Questa interfaccia non introduce nuovi comportamenti rispetto a
 * {@link AzioneDifensore}, ma permette di esprimere attraverso il sistema
 * dei tipi quali azioni difensive sono compatibili con un tentativo di dribbling.
 *
 * @author Nicolò Andreola
 */
public interface DifesaDribbling extends AzioneDifensore {
}
