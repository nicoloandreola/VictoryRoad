package it.unicam.cs.mpgc.rpg129542.persistence;

import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Interfaccia che definisce il contratto per la gestione della persistenza dei dati
 * dell'applicazione: le modalità concrete con cui i dati vengono memorizzati
 * e recuperati non fanno parte di questo contratto ma sono demandate alle relative
 * implementazioni, come {@link PersistenzaXML}.
 *
 * L'interfaccia permette sia di caricare i dati iniziali necessari alla configurazione
 * del gioco, sia di salvare e ripristinare la progressione di un'intera partita.
 *
 * @author Nicolò Andreola
 */
public interface Persistenza {
    /**
     * Carica tutte le tecniche speciali disponibili nel gioco.
     *
     * @return insieme delle tecniche configurate
     *
     * @throws IOException se si verifica un errore durante il caricamento dei dati
     */
    Set<TecnicaSpeciale> caricaTecniche() throws IOException;

    /**
     * Carica i protagonisti disponibili per la scelta iniziale.
     *
     * @return lista dei protagonisti configurati
     *
     * @throws IOException se si verifica un errore durante il caricamento dei dati
     */
    List<Protagonista> caricaProtagonisti() throws IOException;

    /**
     * Carica tutti i livelli che compongono il gioco.
     *
     * @return lista dei livelli configurati
     *
     * @throws IOException se si verifica un errore durante il caricamento dei dati
     */
    List<Livello> caricaLivelli() throws IOException;

    /**
     * Carica i dati relativi a una partita salvata precedentemente.
     *
     * @return dati della progressione salvata
     *
     * @throws IOException se si verifica un errore durante la lettura del salvataggio
     */
    SalvataggioDati caricaPartita() throws IOException;

    /**
     * Salva lo stato attuale di una partita del giocatore.
     *
     * @param dati dati da rendere persistenti
     *
     * @throws IOException se si verifica un errore durante la scrittura del salvataggio
     */
    void salvaPartita(SalvataggioDati dati) throws IOException;


    /**
     * Verifica se è presente un salvataggio precedente dal quale ricostruire una partita.
     *
     * @return {@code true} se il salvataggio esiste, {@code false} altrimenti
     */
    boolean verificaSalvataggio();
}
