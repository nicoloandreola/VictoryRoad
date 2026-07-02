package it.unicam.cs.mpgc.rpg129542.model;

public class GestoreStatistiche {
    private final StatisticheBase statistiche;
    private ModificatoreStatistiche modificatoreAttivo;

    public GestoreStatistiche(StatisticheBase statistiche) {
        if(statistiche == null)
            throw new IllegalArgumentException("Statistiche non possono essere nulle!");
        this.statistiche = statistiche;
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
            return this.statistiche;
        }
        return modificatoreAttivo.applica(this.statistiche);
    }

    /**
     * Migliora PERMANENTEMENTE le statistiche di base del personaggio quando sale di livello.
     * Per farlo crea una nuova istanza della classe Statistiche base
     *
     * @param upgradeAttacco Valore che va incrementato alla statistica di attacco
     * @param upgradeDifesa Valore che va incrementato alla statistica di difesa
     * @param upgradeAgilita Valore che va incrementato alla statistica di agilità
     *
     * @return Le nuove statistiche aggiornate
     *
     * @throws IllegalArgumentException Se un valore passato è minore di 0
     */
    public StatisticheBase miglioraStatistiche(int upgradeAttacco, int upgradeDifesa, int upgradeAgilita) {
        if (upgradeAttacco < 0 || upgradeDifesa < 0 || upgradeAgilita < 0)
            throw new IllegalArgumentException("I miglioramenti non possono essere negativi!");
        return new StatisticheBase(this.statistiche.getAttacco() + upgradeAttacco,
                this.statistiche.getDifesa() + upgradeDifesa,
                this.statistiche.getAgilita() + upgradeAgilita);
    }
}
