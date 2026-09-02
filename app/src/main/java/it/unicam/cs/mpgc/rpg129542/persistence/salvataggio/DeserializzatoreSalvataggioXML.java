package it.unicam.cs.mpgc.rpg129542.persistence.salvataggio;

import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.persistence.utils.DOMUtils;
import it.unicam.cs.mpgc.rpg129542.persistence.configurazione.DeserializzatorePersonaggiXML;
import lombok.NonNull;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashSet;
import java.util.Set;

/**
 * Responsabile della trasformazione del contenuto XML di un salvataggio
 * nella corrispondente istanza di {@link SalvataggioDati}.
 *
 * La classe ricostruisce esclusivamente i dati dinamici della progressione.
 * I riferimenti ai dati statici, come protagonista, tecniche e avversari,
 * vengono mantenuti sotto forma dei rispettivi identificativi.
 *
 * @author Nicolò Andreola
 */
public class DeserializzatoreSalvataggioXML {

    /**
     * Costruisce i dati di una partita salvata a partire
     * dall'elemento XML {@code <salvataggio>}.
     *
     * @param elemento elemento radice del salvataggio
     *
     * @return dati dinamici ricostruiti dal documento XML
     *
     * @throws XPathExpressionException se si verifica un errore durante
     *                                  l'esecuzione delle query XPath
     *
     * @throws IllegalArgumentException se un elemento obbligatorio è
     *                                  mancante o contiene dati non validi
     *
     * @throws NullPointerException se {@code elemento} è {@code null}
     */
    public static SalvataggioDati creaSalvataggio(@NonNull Element elemento) throws XPathExpressionException {
        if (!elemento.getTagName().equals("salvataggio"))
            throw new IllegalArgumentException("L'elemento radice deve essere <salvataggio>!");

        Element protagonista = DOMUtils.readElement(elemento, "protagonista");
        String idProtagonista = protagonista.getAttribute("ref");
        if (idProtagonista.isBlank())
            throw new IllegalArgumentException("Riferimento al protagonista mancante!");

        StatisticheBase statistiche = DeserializzatorePersonaggiXML.creaStatistiche(protagonista);
        Element elementoTecniche = DOMUtils.readElement(protagonista, "tecniche");
        Set<String> tecniche = leggiRiferimenti(elementoTecniche, "tecnicaRef");
        Element elementoAvversari = DOMUtils.readElement(elemento, "avversariSconfitti");
        Set<String> avversariSconfitti = leggiRiferimenti(elementoAvversari, "avversarioRef");

        return new SalvataggioDati(idProtagonista, statistiche, tecniche, avversariSconfitti);
    }

    // Legge gli identificativi contenuti negli attributi "ref" degli elementi specificati.
    private static Set<String> leggiRiferimenti(Element elemento, String tag) throws XPathExpressionException {
        NodeList nodi = DOMUtils.executeQuery(elemento, "./" + tag);
        Set<String> riferimenti = new HashSet<>();
        for (int i = 0; i < nodi.getLength(); i++) {
            Element riferimento = (Element) nodi.item(i);
            String id = riferimento.getAttribute("ref");
            if (id.isBlank())
                throw new IllegalArgumentException("Riferimento XML privo di identificativo!");
            riferimenti.add(id);
        }
        return riferimenti;
    }
}
