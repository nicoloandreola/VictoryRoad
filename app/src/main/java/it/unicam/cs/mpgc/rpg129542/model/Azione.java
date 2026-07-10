package it.unicam.cs.mpgc.rpg129542.model;

/**
 * Rappresenta le azioni che un personaggio può eseguire durante il proprio turno:
 *
 * L'azione {@link #TIRO} infligge danno in base alle statistiche di attacco del protagonista e difesa dell'avversario
 * L’azione {@link #PARATA} permette di recuperare parte della resistenza del personaggio in base alla sua statistica di difesa
 * L'azione {@link #DRIBBLING} sfrutta la statistica di agilità per recuperare parte della stamina del personaggio
 * L'azione {@link #TECNICA_SPECIALE} apre un menu con tutte le tecniche sbloccate, sia quelle difensive che quelle offensive
 *
 * @author Nicolò Andreola
 */
public enum Azione {
    TIRO, PARATA, DRIBBLING, TECNICA_SPECIALE
}
