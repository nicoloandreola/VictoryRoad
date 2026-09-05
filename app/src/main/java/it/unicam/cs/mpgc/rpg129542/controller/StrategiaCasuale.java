package it.unicam.cs.mpgc.rpg129542.controller;

import it.unicam.cs.mpgc.rpg129542.model.azioni.*;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.RisorseMatch;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaOffensiva;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSupporto;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementazione di {@link StrategiaAvversario} che sceglie casualmente
 * le azioni dell'avversario tra quelle utilizzabili nel turno corrente.
 *
 * Durante un attacco vengono sempre considerate le azioni base
 * {@link Tiro} e {@link Dribbling}, alle quali vengono aggiunte
 * le eventuali tecniche speciali possedute dall'avversario
 * per le quali è disponibile stamina sufficiente.
 *
 * Durante la difesa vengono invece considerate solamente le risposte
 * compatibili con l'attacco ricevuto: una {@link Parata} e le eventuali
 * tecniche compatibili contro un attacco diretto, oppure un
 * {@link Contrasto} e le eventuali tecniche compatibili contro
 * un dribbling.
 *
 * Sebbene la scelta rimanga prevalentemente casuale, per aggiungere un filo
 * di logica, le tecniche di supporto vengono considerate solamente quando la
 * stamina dell'avversario è minore o uguale a {@value SOGLIA_UTILIZZO_SUPPORTO},
 * evitando di utilizzarle quando il recupero non risulta necessario.
 *
 * La classe determina esclusivamente quale azione utilizzare, senza
 * eseguirla né calcolarne gli effetti, che rimangono responsabilità
 * delle classi del model.
 *
 * @author Nicolò Andreola
 */
public class StrategiaCasuale implements StrategiaAvversario {

    // Soglia sotto la quale le tecniche di supporto entrano tra le possibili scelte.
    private static final int SOGLIA_UTILIZZO_SUPPORTO = RisorseMatch.STAMINA_MASSIMA / 2;
    private final Random random;

    /**
     * Crea una strategia che utilizza un generatore casuale
     * per selezionare le azioni dell'avversario.
     */
    public StrategiaCasuale() {
        this.random = new Random();
    }

    /**
     * {@inheritDoc}
     *
     * Il tiro e il dribbling sono sempre disponibili. A queste due
     * azioni vengono aggiunte tutte le tecniche possedute dall'avversario
     * che implementano {@link AzioneAttaccante} e dispongono della
     * stamina necessaria per essere utilizzate.
     *
     * Le tecniche di supporto vengono considerate solamente quando
     * la stamina dell'avversario è minore o uguale alla soglia definita
     * da {@link #SOGLIA_UTILIZZO_SUPPORTO}.
     */
    @Override
    public AzioneAttaccante scegliAttacco(@NonNull Avversario avversario) {
        List<AzioneAttaccante> azioniDisponibili = new ArrayList<>();
        azioniDisponibili.add(new Tiro());
        azioniDisponibili.add(new Dribbling());
        for (TecnicaSpeciale tecnica : avversario.getTecnicheSpeciali())
            if (tecnica instanceof AzioneAttaccante azione && tecnica.isDisponibile(avversario)
                    && this.isTecnicaConveniente(tecnica, avversario))
                azioniDisponibili.add(azione);
        return this.scegliCasualmente(azioniDisponibili);
    }

    /**
     * {@inheritDoc}
     *
     * Contro un {@link Tiro} o una {@link TecnicaOffensiva} vengono
     * considerate una {@link Parata} e tutte le tecniche possedute
     * dall'avversario che implementano {@link DifesaAttaccoDiretto}.
     *
     * Contro un {@link Dribbling} vengono invece considerate un
     * {@link Contrasto} e tutte le tecniche che implementano
     * {@link DifesaDribbling}.
     *
     * Le tecniche di supporto vengono considerate solamente quando
     * la stamina dell'avversario è minore o uguale alla soglia definita
     * da {@link #SOGLIA_UTILIZZO_SUPPORTO}.
     *
     * Se viene utilizzata una {@link TecnicaSupporto}, non è richiesta
     * alcuna risposta difensiva e viene quindi restituito {@code null}.
     *
     * @throws IllegalArgumentException se viene ricevuta un'azione
     *                                  offensiva non riconosciuta
     */
    @Override
    public AzioneDifensore scegliDifesa(@NonNull Avversario avversario, @NonNull AzioneAttaccante attacco) {
        if (attacco instanceof TecnicaSupporto)
            return null;
        else if (attacco instanceof Tiro || attacco instanceof TecnicaOffensiva)
            return this.scegliDifesaAttaccoDiretto(avversario);
        else if (attacco instanceof Dribbling)
            return this.scegliDifesaDribbling(avversario);
        else
            throw new IllegalArgumentException("Azione di attacco non riconosciuta!");
    }

    private AzioneDifensore scegliDifesaAttaccoDiretto(Avversario avversario) {
        List<AzioneDifensore> difeseDisponibili = new ArrayList<>();
        difeseDisponibili.add(new Parata());
        for (TecnicaSpeciale tecnica : avversario.getTecnicheSpeciali())
            if (tecnica instanceof DifesaAttaccoDiretto difesa
                    && tecnica.isDisponibile(avversario))
                difeseDisponibili.add(difesa);
        return this.scegliCasualmente(difeseDisponibili);
    }

    private AzioneDifensore scegliDifesaDribbling(Avversario avversario) {
        List<AzioneDifensore> difeseDisponibili = new ArrayList<>();
        difeseDisponibili.add(new Contrasto());
        for (TecnicaSpeciale tecnica : avversario.getTecnicheSpeciali())
            if (tecnica instanceof DifesaDribbling difesa && tecnica.isDisponibile(avversario)
                    && this.isTecnicaConveniente(tecnica, avversario))
                difeseDisponibili.add(difesa);
        return this.scegliCasualmente(difeseDisponibili);
    }

    // Permette di evitare l'utilizzo delle tecniche di supporto quando la stamina è ancora elevata.
    private boolean isTecnicaConveniente(TecnicaSpeciale tecnica, Avversario avversario) {
        // Se la tecnica non è di supporto ritorna subito true
        if(!(tecnica instanceof TecnicaSupporto))
            return true;
        // Altrimenti valuta in base alla stamina del personaggio
        return avversario.getStamina() <= SOGLIA_UTILIZZO_SUPPORTO;
    }

    // Il metodo è generico per evitare di avere due metodi praticamente identici
    // (stesso meccanismo viene utilizzato sia per le azioni di attacco sia per quelle di difesa).
    private <T> T scegliCasualmente(List<T> elementi) {
        return elementi.get(this.random.nextInt(elementi.size()));
    }
}
