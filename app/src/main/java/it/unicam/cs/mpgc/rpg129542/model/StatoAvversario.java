package it.unicam.cs.mpgc.rpg129542.model;

/**
 * Enumerazione che classifica i possibili stati in cui si può trovare un avversario: <br>
 * {@code DA_SCONFIGGERE} quando viene appena aggiunto a un livello e non è stato ancora battuto <br>
 * {@code SELEZIONATO} quando il giocatore lo seleziona per affrontarlo <br>
 * {@code SCONFITTO} dopo che è stato battuto dal giocatore e non può essere più affrontato
 */
public enum StatoAvversario {
    DA_SCONFIGGERE, SELEZIONATO, SCONFITTO
}
