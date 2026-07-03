package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;

@Getter
public class GestoreStatistiche {
    private StatisticheBase statisticheBase;
    private ModificatoreStatistiche modificatoreAttivo;

    public GestoreStatistiche(StatisticheBase statisticheBase) {
        if(statisticheBase == null)
            throw new IllegalArgumentException("Statistiche non possono essere nulle!");
        this.statisticheBase = statisticheBase;
    }

    /**
     * Permette di modificare TEMPORANEAMENTE le statistiche base del personaggio
     *
     * @param modificatore Qualsiasi oggetto che implementa l'interfaccia {@link ModificatoreStatistiche}
     *                     che determina come modificare le statistiche
     */
    public void applicaModificatore(ModificatoreStatistiche modificatore) {
        if(modificatore == null)
            throw new IllegalArgumentException("Modificatore non può essere nullo!");
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

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof GestoreStatistiche))
            return false;
        GestoreStatistiche other = (GestoreStatistiche) obj;
        return this.statisticheBase.equals(other.statisticheBase);
    }

    @Override
    public int hashCode() {
        return this.statisticheBase.hashCode();
    }
}
