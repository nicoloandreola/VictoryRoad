package it.unicam.cs.mpgc.rpg129542.model.match;

/**
 * Rappresenta il tipo di esito prodotto da un'azione durante un match.
 *
 * {@link #ATTACCO_RESPINTO} quando l'azione offensiva non ha superato la difesa del personaggio bersaglio
 * {@link #DANNO_INFLITTO} quando l'azione offensiva ha ridotto la resistenza del personaggio bersaglio
 * {@link #STAMINA_RECUPERATA} quando l'azione ha permesso di recuperare stamina
 * {@link #RESISTENZA_RECUPERATA} quando l'azione ha permesso di recuperare resistenza
 * {@link #GOL} quando l'azione ha portato la resistenza del difensore a zero e ha assegnato un gol
 *
 * @author Nicolo Andreola
 */
public enum EsitoAzione {
    ATTACCO_RESPINTO, DANNO_INFLITTO, STAMINA_RECUPERATA, RESISTENZA_RECUPERATA, GOL
}
