package it.unicam.cs.mpgc.rpg129542.view;

import it.unicam.cs.mpgc.rpg129542.controller.ControllerGioco;

/**
 * Definisce il contratto comune dei controller associati alle schermate
 * dell'interfaccia JavaFX.
 *
 * Ogni controller FXML riceve il {@link ControllerGioco}, attraverso il
 * quale può leggere e modificare lo stato della partita, e il
 * {@link GestoreSchermate}, utilizzato esclusivamente per richiedere
 * il passaggio a un'altra schermata.
 *
 * In questo modo i controller delle singole schermate non devono creare
 * autonomamente le proprie dipendenze e condividono la stessa istanza
 * di {@code ControllerGioco} per tutta la durata dell'applicazione.
 *
 * @author Nicolò Andreola
 */
public interface ControllerSchermata {

    /**
     * Fornisce al controller della schermata le dipendenze necessarie
     * per interagire con l'applicazione e gestire la navigazione.
     *
     * Il metodo viene chiamato da {@link GestoreSchermate} subito dopo
     * il caricamento del relativo file FXML.
     *
     * @param controllerGioco controller principale dell'applicazione
     * @param gestoreSchermate gestore utilizzato per cambiare schermata
     */
    void configura(ControllerGioco controllerGioco, GestoreSchermate gestoreSchermate);
}
