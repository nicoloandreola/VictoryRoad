package it.unicam.cs.mpgc.rpg129542.persistence;

import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Rappresenta i dati dinamici necessari per salvare e ripristinare
 * la progressione di una partita non ancora terminata.
 *
 * La classe non contiene logica di lettura o scrittura: si limita
 * a raccogliere le informazioni che devono essere rese persistenti.
 *
 * I dati statici del gioco, come la configurazione iniziale dei
 * personaggi, delle tecniche e dei livelli, non vengono duplicati
 * nel salvataggio ma sono ricavati dai relativi file di configurazione.
 *
 * @author Nicolò Andreola
 */

public class SalvataggioDati {
    @Getter
    private final String idProtagonista;
    @Getter
    private final StatisticheBase statisticheProtagonista;
    private final Set<String> tecnicheImparate;
    private final Set<String> avversariSconfitti;

    /**
     * Crea una rappresentazione dei dati da salvare o appena caricati.
     *
     * @param idProtagonista identificativo del protagonista scelto
     * @param statisticheProtagonista statistiche permanenti attuali del protagonista
     * @param tecnicheImparate identificativi delle tecniche possedute dal protagonista
     * @param avversariSconfitti identificativi degli avversari già sconfitti
     *
     * @throws NullPointerException se uno dei parametri è {@code null}
     *
     * @throws IllegalArgumentException se l'identificativo del protagonista è vuoto
     */
    public SalvataggioDati(@NonNull String idProtagonista, @NonNull StatisticheBase statisticheProtagonista,
                           @NonNull Set<String> tecnicheImparate, @NonNull Set<String> avversariSconfitti) {
        if (idProtagonista.isBlank())
            throw new IllegalArgumentException("L'identificativo del protagonista non può essere vuoto!");

        if (tecnicheImparate.contains(null) || avversariSconfitti.contains(null))
            throw new NullPointerException("Le collezioni del salvataggio non possono contenere valori nulli!");
        this.idProtagonista = idProtagonista;
        this.statisticheProtagonista = statisticheProtagonista;
        this.tecnicheImparate = new HashSet<>(tecnicheImparate);
        this.avversariSconfitti = new HashSet<>(avversariSconfitti);
    }

    /**
     * Restituisce una copia non modificabile di tutti gli ID delle
     * tecniche possedute fino a quel momento della partita.
     *
     * @return insieme non modificabile delle tecniche speciali
     *          rappresentate con il rispettivo identificativo
     */
    public Set<String> getTecnicheImparate() {
        return Set.copyOf(this.tecnicheImparate);
    }

    /**
     * Restituisce una copia non modificabile di tutti gli ID degli
     * avversari sconfitti fino a quel momento della partita.
     *
     * @return insieme non modificabile degli avversari
     *          rappresentati con il rispettivo identificativo
     */
    public Set<String> getAvversariSconfitti() {
        return Set.copyOf(this.avversariSconfitti);
    }
}
