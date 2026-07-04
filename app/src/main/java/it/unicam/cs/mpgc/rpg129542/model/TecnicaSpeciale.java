package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;
import lombok.NonNull;

/**
 * Rappresenta una tecnica speciale utilizzabile durante un match.
 * <p>
 * Una tecnica può essere offensiva o difensiva, ha una potenza e richiede
 * un certo costo in stamina per essere utilizzata.
 *
 * @author Nicolò Andreola
 */
@Getter
public abstract class TecnicaSpeciale {
    private final String nome;
    private final String descrizione;
    private final TipoTecnica tipo;
    private final int potenza;
    private final int costoStamina;

    public TecnicaSpeciale (@NonNull String nome, @NonNull String descrizione, @NonNull TipoTecnica tipo, int potenza, int costoStamina){
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
        this.tipo = tipo;
        this.potenza = potenza;
        this.costoStamina = costoStamina;
    }

    public boolean isAvailable(@NonNull Personaggio personaggio) {
        return personaggio.getStamina() >= this.costoStamina;
    }

    public abstract void effettoTecnica();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TecnicaSpeciale))
            return false;
        TecnicaSpeciale other = (TecnicaSpeciale) obj;
        return this.nome.equals(other.nome) && this.tipo == other.tipo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.nome.hashCode();
        result = prime * result + this.tipo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        return s.append(this.nome).append(" | ").append(this.tipo).append("\n")
                .append(this.descrizione).toString();
    }
}
