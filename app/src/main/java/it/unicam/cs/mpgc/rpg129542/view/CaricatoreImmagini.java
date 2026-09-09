package it.unicam.cs.mpgc.rpg129542.view;

import javafx.scene.image.Image;

import java.net.URL;

/**
 * Classe di utilità responsabile del caricamento delle immagini utilizzate
 * dall'interfaccia grafica in JavaFX.
 *
 * Centralizza il recupero delle risorse grafiche dal classpath, evitando
 * di duplicare la stessa logica nei diversi controller FXML.
 *
 * La classe non mantiene alcuno stato e non può essere istanziata:
 * il caricamento delle immagini avviene esclusivamente tramite
 * il metodo statico {@link #carica(String)}.
 *
 * @author Nicolò Andreola
 */
public class CaricatoreImmagini {

    /**
     * Carica un'immagine a partire dal percorso della relativa risorsa.
     *
     * @param percorso percorso dell'immagine nel classpath
     *
     * @return immagine caricata dalla risorsa indicata
     *
     * @throws NullPointerException se il percorso è {@code null}
     *
     * @throws IllegalArgumentException se non esiste alcuna immagine
     *                                  associata al percorso indicato
     */
    public static Image carica(String percorso) {
        URL risorsa = CaricatoreImmagini.class.getClassLoader().getResource(percorso);
        if (risorsa == null)
            throw new IllegalArgumentException("Immagine non trovata: " + percorso);
        return new Image(risorsa.toExternalForm());
    }
}
