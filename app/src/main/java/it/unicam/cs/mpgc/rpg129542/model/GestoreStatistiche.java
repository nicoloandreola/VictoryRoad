package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;
import lombok.NonNull;

/**
 * Gestisce le statistiche di base di un personaggio, permettendo sia di migliorarle
 * permanentemente quando si raggiunge un nuovo livello, sia di modificarle
 * temporaneamente applicando un oggetto di tipo {@link ModificatoreStatistiche}.
 *
 * Ogni {@link Personaggio} utilizza un'istanza di questa classe.
 *
 * @author Nicolò Andreola
 */

@Getter
public class GestoreStatistiche {
    private StatisticheBase statisticheBase;
    private ModificatoreStatistiche modificatoreAttivo;

    /**
     * Crea un gestore per le statistiche partendo da un istanza di {@link StatisticheBase}.
     *
     * @param statisticheBase statistiche di base iniziali
     *
     * @throws NullPointerException se le statistiche sono nulle
     */
    public GestoreStatistiche(@NonNull StatisticheBase statisticheBase) {
        this.statisticheBase = statisticheBase;
    }

    /**
     * Permette di modificare TEMPORANEAMENTE le statistiche base del personaggio
     *
     * @param modificatore Qualsiasi oggetto che implementa l'interfaccia {@link ModificatoreStatistiche}
     *                     che determina come modificare le statistiche
     *
     * @throws NullPointerException se il modificatore passato à nullo
     */
    public void applicaModificatore(@NonNull ModificatoreStatistiche modificatore) {
        this.modificatoreAttivo = modificatore;
    }

    /**
     * Permette di rimuovere il modificatore alla fine di un match e di
     * ripristinare le statistiche base del personaggio
     */
    public void rimuoviModificatore() {
        this.modificatoreAttivo = null;
    }

    /**
     * Restituisce le statistiche correnti del personaggio, verificando
     * prima se è attivo qualche modificatore
     *
     * @return Le statistiche del personaggio in quel preciso momento
     */
    public StatisticheBase getStatisticheEffettive() {
        if (modificatoreAttivo == null) {
            return this.statisticheBase;
        }
        return modificatoreAttivo.applica(this.statisticheBase);
    }

    /**
     * Migliora PERMANENTEMENTE le statistiche di base del personaggio quando sale di livello.
     * Per farlo crea una nuova istanza della classe {@link StatisticheBase} con le statistiche
     * incrementate e la assegna alla variabile di istanza {@link #statisticheBase}.
     *
     * @param upgradeAttacco Valore che va incrementato alla statistica di attacco
     * @param upgradeDifesa Valore che va incrementato alla statistica di difesa
     * @param upgradeAgilita Valore che va incrementato alla statistica di agilità
     *
     * @throws IllegalArgumentException Se un valore passato è minore di 0
     */
    public void miglioraStatistiche(int upgradeAttacco, int upgradeDifesa, int upgradeAgilita) {
        if (upgradeAttacco < 0 || upgradeDifesa < 0 || upgradeAgilita < 0)
            throw new IllegalArgumentException("I miglioramenti non possono essere negativi!");
        this.statisticheBase = new StatisticheBase(
                this.statisticheBase.getAttacco() + upgradeAttacco,
                this.statisticheBase.getDifesa() + upgradeDifesa,
                this.statisticheBase.getAgilita() + upgradeAgilita);
    }
}
