package it.unicam.cs.mpgc.rpg129542.model;

/**
 * Enumerazione che classifica le tecniche speciali in base al loro tipo.
 *
 * Il tipo è un'informazione descrittiva utile per filtrare,
 * visualizzare e serializzare le tecniche. Il comportamento concreto
 * è invece definito dalle sottoclassi di {@link TecnicaSpeciale}
 * (una sottoclasse per ogni tipo definito in questa enum).
 *
 * @author Nicolò Andreola
 */
public enum TipoTecnica {
    OFFENSIVA, DIFENSIVA, SUPPORTO
}
