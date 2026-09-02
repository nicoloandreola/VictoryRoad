package it.unicam.cs.mpgc.rpg129542.persistence.salvataggio;

import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.persistence.utils.DOMUtils;
import it.unicam.cs.mpgc.rpg129542.persistence.PersistenzaXML;
import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Set;

/**
 * Responsabile della trasformazione dei dati dinamici di una partita
 * in una rappresentazione DOM utilizzabile per il salvataggio XML.
 *
 * La classe serializza esclusivamente le informazioni contenute in
 * {@link SalvataggioDati}; i dati statici già presenti nei file di
 * configurazione vengono rappresentati attraverso i relativi identificativi.
 *
 * La scrittura effettiva del documento su file non è responsabilità
 * di questa classe, ma viene gestita da {@link PersistenzaXML}.
 *
 * @author Nicolò Andreola
 */
public class SerializzatoreSalvataggioXML {

    /**
     * Costruisce un documento DOM contenente i dati della progressione
     * da rendere persistenti.
     *
     * @param dati dati della partita da serializzare
     *
     * @return documento DOM rappresentante il salvataggio
     *
     * @throws NullPointerException se {@code dati} è {@code null}
     *
     * @throws ParserConfigurationException se non è possibile creare il documento DOM
     */
    public static Document creaDocumento(@NonNull SalvataggioDati dati) throws ParserConfigurationException {
        Document document = DOMUtils.createDomDocument();
        Element radice = document.createElement("salvataggio");
        document.appendChild(radice);
        aggiungiProtagonista(document, radice, dati);
        aggiungiAvversariSconfitti(document, radice, dati.getAvversariSconfitti());
        return document;
    }

    // Inserisce nel documento l'identificativo del protagonista, le sue statistiche permanenti
    // chiamando aggiungiStatistiche() e le tecniche possedute chiamando aggiungiTecniche().
    private static void aggiungiProtagonista(Document document, Element salvataggio, SalvataggioDati dati) {
        Element protagonista = document.createElement("protagonista");
        protagonista.setAttribute("ref", dati.getIdProtagonista());
        salvataggio.appendChild(protagonista);
        aggiungiStatistiche(document, protagonista, dati.getStatisticheProtagonista());
        aggiungiTecniche(document, protagonista, dati.getTecnicheImparate());
    }

    private static void aggiungiStatistiche(Document document, Element protagonista, StatisticheBase statistiche) {
        Element elementoStatistiche = document.createElement("statistiche");
        protagonista.appendChild(elementoStatistiche);
        aggiungiElementoTesto(document, elementoStatistiche, "attacco", statistiche.getAttacco());
        aggiungiElementoTesto(document, elementoStatistiche, "difesa", statistiche.getDifesa());
        aggiungiElementoTesto(document, elementoStatistiche, "agilita", statistiche.getAgilita());
    }

    private static void aggiungiTecniche(Document document, Element protagonista, Set<String> tecniche) {
        Element elementoTecniche = document.createElement("tecniche");
        protagonista.appendChild(elementoTecniche);
        for (String id : tecniche) {
            Element tecnicaRef = document.createElement("tecnicaRef");
            tecnicaRef.setAttribute("ref", id);
            elementoTecniche.appendChild(tecnicaRef);
        }
    }

    private static void aggiungiAvversariSconfitti(Document document, Element salvataggio, Set<String> avversariSconfitti) {
        Element elementoAvversari = document.createElement("avversariSconfitti");
        salvataggio.appendChild(elementoAvversari);
        for (String id : avversariSconfitti) {
            Element avversarioRef = document.createElement("avversarioRef");
            avversarioRef.setAttribute("ref", id);
            elementoAvversari.appendChild(avversarioRef);
        }
    }

    // Crea un elemento XML contenente un valore numerico.
    private static void aggiungiElementoTesto(Document document, Element padre, String nome, int valore) {
        Element elemento = document.createElement(nome);
        elemento.setTextContent(Integer.toString(valore));
        padre.appendChild(elemento);
    }
}
