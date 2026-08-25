package it.unicam.cs.mpgc.rpg129542.model.match;

/**
 * Rappresenta il tipo di esito prodotto da un'azione durante un match. <br>
 *
 * {@link #DRIBBLING_RIUSCITO} quando il giocatore riesce a mantenere il possesso per il turno successivo <br>
 * {@link #PALLA_PERSA} quando il contrasto del difensore ferma il dribbling dell'attaccante <br>
 * {@link #ATTACCO_RESPINTO} quando l'azione offensiva non ha superato la difesa dell'avversario e non ha prodotto danno <br>
 * {@link #DANNO_INFLITTO} quando l'azione offensiva ha ridotto la resistenza del difensore <br>
 * {@link #STAMINA_RECUPERATA} quando l'azione ha permesso di recuperare stamina <br>
 * {@link #GOL_SEGNATO} quando l'azione ha portato la resistenza del difensore a zero e ha assegnato un gol
 *
 * @author Nicolo Andreola
 */
public enum EsitoTurno {
    DRIBBLING_RIUSCITO, PALLA_PERSA, ATTACCO_RESPINTO, DANNO_INFLITTO, STAMINA_RECUPERATA, GOL_SEGNATO
}
