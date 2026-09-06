package it.unicam.cs.mpgc.rpg129542.persistence.configurazione;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.StatisticheBase;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import it.unicam.cs.mpgc.rpg129542.persistence.utils.DOMUtils;
import it.unicam.cs.mpgc.rpg129542.persistence.salvataggio.DeserializzatoreSalvataggioXML;
import lombok.NonNull;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashSet;
import java.util.Set;

/**
 * Responsabile della trasformazione degli elementi XML {@code <protagonista>} e
 * {@code <avversario>} nei corrispondenti oggetti del model, comprese le
 * statistiche base e le tecniche referenziate.
 *
 * Le tecniche non vengono ricreate durante la deserializzazione dei
 * personaggi: i riferimenti presenti nel documento XML vengono risolti
 * utilizzando l'insieme delle tecniche precedentemente caricate, per
 * questo i metodi prendono come argomento un {@code Set<TecnicaSpeciale>}.
 *
 * @author Nicolò Andreola
 */
public class DeserializzatorePersonaggiXML {

    /**
     * Costruisce un {@link Protagonista} a partire dal relativo elemento XML,
     * risolvendo la tecnica referenziata all'interno dell'insieme fornito.
     *
     * @param elemento Il nodo {@code <protagonista>} da mappare.
     * @param tecniche L'insieme completo delle tecniche già caricate, usato per risolvere il riferimento.
     *
     * @return L'istanza della classe {@link Protagonista} corrispondente.
     *
     * @throws XPathExpressionException Se si verifica un errore durante la lettura dei campi
     *
     * @throws IllegalArgumentException se un dato obbligatorio è mancante, non è valido oppure
     *                                  il riferimento alla tecnica non può essere risolto
     *
     * @throws NullPointerException se {@code elemento} o {@code tecniche} sono {@code null}
     */
    public static Protagonista creaProtagonista(@NonNull Element elemento, @NonNull Set<TecnicaSpeciale> tecniche)
            throws XPathExpressionException {
        String id = elemento.getAttribute("id");
        String nome = DOMUtils.readTextNode(elemento, "nome");
        StatisticheBase statistiche = creaStatistiche(elemento);

        Element tecnicaRef = DOMUtils.readElement(elemento, "tecniche/tecnicaRef");
        TecnicaSpeciale tecnica = trovaTecnica(tecniche, tecnicaRef.getAttribute("ref"));

        return new Protagonista(nome, id, statistiche, tecnica);
    }


    /**
     * Costruisce un {@link Avversario} a partire dal relativo elemento XML,
     * risolvendo tutte le tecniche referenziate all'interno dell'insieme fornito.
     *
     * @param elemento Il nodo {@code <avversario>} da mappare.
     * @param tecniche L'insieme completo delle tecniche già caricate, usato per risolvere i riferimenti.
     *
     * @return L'istanza della classe {@link Avversario} corrispondente.
     *
     * @throws XPathExpressionException Se si verifica un errore durante la lettura dei campi.
     *
     * @throws IllegalArgumentException se un dato obbligatorio è mancante, non è valido oppure
     *                                  il riferimento alla tecnica non può essere risolto
     *
     * @throws NullPointerException se {@code elemento} o {@code tecniche} sono {@code null}
     */
    public static Avversario creaAvversario(@NonNull Element elemento, @NonNull Set<TecnicaSpeciale> tecniche)
            throws XPathExpressionException {
        String id = elemento.getAttribute("id");
        String nome = DOMUtils.readTextNode(elemento, "nome");
        StatisticheBase statistiche = creaStatistiche(elemento);

        NodeList nodiTecniche = DOMUtils.executeQuery(elemento, "./tecniche/tecnicaRef");
        Set<TecnicaSpeciale> tecnicheAvversario = new HashSet<>();
        for (int i = 0; i < nodiTecniche.getLength(); i++) {
            Element tecnicaRef = (Element) nodiTecniche.item(i);
            tecnicheAvversario.add(trovaTecnica(tecniche, tecnicaRef.getAttribute("ref")));
        }

        return new Avversario(nome, id, statistiche, tecnicheAvversario);
    }

    // Cerca nell'insieme la tecnica associata all'identificativo ricevuto.
    private static TecnicaSpeciale trovaTecnica(Set<TecnicaSpeciale> tecniche, String id) {
        return tecniche.stream().filter(tecnica -> tecnica.getId().equals(id))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Tecnica non trovata: " + id));
    }

    /**
     * Costruisce un'istanza di {@link StatisticheBase} leggendo i valori
     * di attacco, difesa e agilità contenuti nell'elemento XML specificato.
     *
     * Il metodo è definito {@code public} esclusivamente perché viene utilizzato
     * non soltanto in questa classe ma anche in {@link DeserializzatoreSalvataggioXML}
     * durante il caricamento delle statistiche permanenti di un protagonista salvato,
     * altrimenti sarebbe stato tranquillamente {@code private}
     *
     * @param elemento elemento XML contenente il nodo {@code <statistiche>}
     *
     * @return statistiche base ricostruite dai valori presenti nell'elemento
     *
     * @throws XPathExpressionException se si verifica un errore durante
     *                                  la lettura dei valori tramite XPath
     *
     * @throws IllegalArgumentException se un valore obbligatorio è mancante,
     *                                  non è convertibile nel tipo previsto
     *                                  oppure non rispetta i vincoli
     *                                  definiti da {@link StatisticheBase}
     *
     * @throws NullPointerException se {@code elemento} è {@code null}
     */
    public static StatisticheBase creaStatistiche(@NonNull Element elemento) throws XPathExpressionException{
        int attacco = Integer.parseInt(DOMUtils.readTextNode(elemento, "statistiche/attacco"));
        int difesa = Integer.parseInt(DOMUtils.readTextNode(elemento, "statistiche/difesa"));
        int agilita = Integer.parseInt(DOMUtils.readTextNode(elemento, "statistiche/agilita"));
        return new StatisticheBase(attacco, difesa, agilita);
    }
}
