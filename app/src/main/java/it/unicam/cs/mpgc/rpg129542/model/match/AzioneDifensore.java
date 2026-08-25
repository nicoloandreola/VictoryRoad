package it.unicam.cs.mpgc.rpg129542.model.match;

/**
 * Rappresenta le azioni che un personaggio può eseguire quando difende: <br>
 *
 * L'azione {@link #CONTRASTO} prova a togliere il possesso all'avversario quando esegue un dribbling <br>
 * L'azione {@link #PARATA} permette di limitare i danni in base alla statistica di difesa <br>
 * L'azione {@link #TECNICA_DIFENSIVA} utilizza una tecnica speciale difensiva <br>
 * L'azione {@link #TECNICA_SUPPORTO} permette di recuperare stamina e può essere usata invece di contrastare un dribbling <br>
 * L'azione {@link #NESSUNA} viene usata quando l'avversario usa {@link AzioneAttaccante#TECNICA_SUPPORTO}
 *
 * @author Nicolò Andreola
 **/
public enum  AzioneDifensore {
    CONTRASTO, PARATA, TECNICA_DIFENSIVA, TECNICA_SUPPORTO, NESSUNA
}
