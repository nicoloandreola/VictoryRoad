package it.unicam.cs.mpgc.rpg129542.model.match;

/**
 * Rappresenta le azioni che un personaggio può eseguire quando attacca: <br>
 *
 * L'azione {@link #TIRO} infligge danno in base alle statistiche di attacco del protagonista e difesa dell'avversario <br>
 * L'azione {@link #DRIBBLING} sfrutta la statistica di agilità per provare a mantenere il possesso nel turno successivo <br>
 * L'azione {@link #TECNICA_OFFENSIVA} utilizza una tecnica speciale offensiva <br>
 * L'azione {@link #TECNICA_SUPPORTO} permette di recuperare stamina e può essere usata al posto di attaccare
 *
 * @author Nicolò Andreola
 */
public enum AzioneAttaccante {
    TIRO, DRIBBLING, TECNICA_OFFENSIVA, TECNICA_SUPPORTO
}
