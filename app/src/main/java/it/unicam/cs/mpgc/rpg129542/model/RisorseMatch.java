package it.unicam.cs.mpgc.rpg129542.model;

import lombok.Getter;

@Getter
public class RisorseMatch {

    public static final int STAMINA_MASSIMA = 100;
    public static final int RESISTENZA_MASSIMA = 100;

    private int stamina;
    private int resistenza;

    public RisorseMatch() {
        this.stamina = STAMINA_MASSIMA;
        this.resistenza = RESISTENZA_MASSIMA;
    }

    public void consumaStamina(int quantita) {
        this.verificaQuantita(quantita);
        stamina = Math.max(0, stamina - quantita);
    }

    public void recuperaStamina(int quantita) {
        this.verificaQuantita(quantita);
        stamina = Math.min(STAMINA_MASSIMA, stamina + quantita);
    }

    public void riduciResistenza(int quantita) {
        this.verificaQuantita(quantita);
        resistenza = Math.max(0, resistenza - quantita);
    }

    public void recuperaResistenza(int quantita) {
        this.verificaQuantita(quantita);
        resistenza = Math.min(RESISTENZA_MASSIMA, resistenza + quantita);
    }

    public boolean haStamina(int quantita) {
        this.verificaQuantita(quantita);
        return this.stamina >= quantita;
    }

    public boolean haResistenza() {
        return !(resistenza == 0);
    }

    private void verificaQuantita(int quantita) {
        if (quantita < 0)
            throw new IllegalArgumentException("La quantità non può essere negativa!");
    }
}
