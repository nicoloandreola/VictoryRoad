package it.unicam.cs.mpgc.rpg129542.model.tecniche;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import lombok.Getter;
import lombok.NonNull;

/**
 * Rappresenta una tecnica speciale utilizzabile da un {@link Personaggio} durante un match.
 *
 * Poiché implementa solo la logica comune, cioè la definizione, la disponibilità e il suo
 * costo, questa classe è astratta e non può essere istanziata direttamente: sono le sottoclassi
 * a definire il tipo della tecnica e la sua logica, ovvero il modo in cui viene usata e i suoi effetti
 *
 * Le istanze sono immutabili: dopo la costruzione, le caratteristiche
 * della tecnica non possono essere modificate.
 *
 * @author Nicolò Andreola
 */
@Getter
public abstract class TecnicaSpeciale {
    private final String nome;
    private final String descrizione;
    private final int potenza;
    private final int costoStamina;

    /**
     * Costruisce una tecnica speciale.
     *
     * Il costruttore è {@code protected} perché una tecnica speciale
     * deve essere istanziata attraverso una sottoclasse concreta, come
     * {@link TecnicaOffensiva} o {@link TecnicaDifensiva}.
     *
     * @param nome nome identificativo della tecnica
     * @param descrizione descrizione dell'effetto della tecnica
     * @param potenza valore aggiunto alla statistica utilizzata
     * @param costoStamina stamina necessaria per utilizzare la tecnica
     *
     * @throws NullPointerException se nome o descrizione sono nulli
     *
     * @throws IllegalArgumentException se nome o descrizione sono vuoti,
     *                                  se la potenza non è positiva oppure
     *                                  se il costo in stamina è negativo
     */

    protected TecnicaSpeciale (@NonNull String nome, @NonNull String descrizione, int potenza, int costoStamina){
        if (nome.isBlank())
            throw new IllegalArgumentException("Il nome della tecnica non può essere vuoto!");
        if (descrizione.isBlank())
            throw new IllegalArgumentException("La descrizione tecnica non può essere vuota!");
        if (potenza <= 0)
            throw new IllegalArgumentException("La potenza della tecnica deve essere positiva!");
        if (costoStamina < 0)
            throw new IllegalArgumentException("Il costo in stamina non può essere negativo!");
        this.nome = nome;
        this.descrizione = descrizione;
        this.potenza = potenza;
        this.costoStamina = costoStamina;
    }

    /**
     * Restituisce la categoria (tipo specifico) della tecnica.
     *
     * Il tipo non è ricevuto dal costruttore, ma viene determinato
     * direttamente dalla sottoclasse. Questo impedisce la creazione di
     * combinazioni incoerenti, come una {@link TecnicaOffensiva}
     * classificata come {@link TipoTecnica#DIFENSIVA}.
     *
     * @return tipo della tecnica
     */
    public abstract TipoTecnica getTipo();

    /**
     * Permette di utilizzare la tecnica verificando preventivamente che, prima
     * il personaggio la possieda effettivamente tra quelle imparate, e poi
     * disponga della stamina necessaria per usarla: se la tecnica è
     * disponibile, consuma la stamina richiesta e poi calcola il suo
     * effetto con {@link #calcolaEffetto(Personaggio)}.
     *
     * @param personaggio personaggio che usa la tecnica
     *
     * @return l'effetto prodotto dalla tecnica speciale
     *
     * @throws NullPointerException se l'utilizzatore è nullo
     *
     * @throws IllegalStateException se il personaggio non possiede la tecnica o
     *                              se la stamina disponibile è minore di quella richiesta
     */
    public int usa(@NonNull Personaggio personaggio) {
        if (!personaggio.possiedeTecnica(this))
            throw new IllegalStateException("Il personaggio non possiede questa tecnica!");
        if (!isDisponibile(personaggio))
            throw new IllegalStateException("Stamina insufficiente per utilizzare la tecnica!");
        int effetto = calcolaEffetto(personaggio);
        personaggio.consumaStamina(this.costoStamina);
        return effetto;
    }

    /**
     * Verifica se il personaggio possiede stamina sufficiente per utilizzare la tecnica.
     *
     * @param personaggio personaggio che intende usare la tecnica
     * @return {@code true} se la stamina disponibile è maggiore o uguale
     *         al costo della tecnica, {@code false} altrimenti
     *
     * @throws NullPointerException se il personaggio passato è nullo
     */
    public boolean isDisponibile(@NonNull Personaggio personaggio) {
        return personaggio.getStamina() >= this.costoStamina;
    }

    /**
     * Descrive la logica di una tecnica speciale e restituisce il suo effetto.
     *
     * Il metodo è definito {@code protected} per evitare che possa essere
     * chiamato direttamente senza consumare stamina
     *
     * @param personaggio personaggio su cui applicare l'effetto
     *
     * @return valore di efficacia prodotto dalla tecnica
     *
     * @throws NullPointerException se il personaggio passato è nullo
     */
    protected abstract int calcolaEffetto(@NonNull Personaggio personaggio);

    /**
     * Confronta due tecniche in base al loro tipo e al nome: due tecniche con lo stesso
     * nome ma appartenenti a categorie differenti non sono considerate uguali.
     *
     * @param obj oggetto da confrontare
     *
     * @return {@code true} se le tecniche hanno lo stesso {@link TipoTecnica} e lo stesso nome
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TecnicaSpeciale))
            return false;
        TecnicaSpeciale other = (TecnicaSpeciale) obj;
        return this.nome.equals(other.nome) && this.getTipo() == other.getTipo();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.nome.hashCode();
        result = prime * result + this.getTipo().hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        return s.append("Nome: ").append(this.nome).append("\n")
                .append("Tipo: ").append(this.getTipo()).append("\n")
                .append("Descrizione: ").append(this.descrizione).append("\n")
                .append("Potenza = ").append(this.potenza).append("\n")
                .append("Costo = ").append(this.costoStamina).toString();
    }
}
