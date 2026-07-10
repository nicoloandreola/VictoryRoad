package it.unicam.cs.mpgc.rpg129542.model;

/**
 * Rappresenta lo stato generale di un match.
 *
 * Un match può essere ancora in corso oppure concluso con la vittoria
 * del protagonista ({@code VITTORIA}) o dell'avversario ({@code SCONFITTA}).
 *
 * @author Nicolò Andreola
 */
public enum StatoMatch {
    IN_CORSO, VITTORIA, SCONFITTA
}
