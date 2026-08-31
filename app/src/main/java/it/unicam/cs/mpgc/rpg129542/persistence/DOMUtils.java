package it.unicam.cs.mpgc.rpg129542.persistence;

import lombok.NonNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

/**
 * Classe di utilità che fornisce diversi metodi per la gestione di
 * documenti XML tramite le API DOM e XPath.
 *
 * @author Lorenzo Rossi
 * @author Nicolò Andreola
 */

public class DOMUtils {

    /**
     * Carica un documento XML dal percorso specificato e lo restituisce come oggetto
     * DOM {@code Document}.
     *
     * @param path Il percorso del file XML da caricare.
     *
     * @return documento DOM ottenuto dal parsing del contenuto XML.
     *
     * @throws ParserConfigurationException Se non è possibile creare un {@code DocumentBuilder}
     *         che soddisfi la configurazione richiesta.
     *
     * @throws IOException Se si verificano errori di I/O durante la lettura del file.
     *
     * @throws SAXException Se si verificano errori di parsing durante l'analisi del file XML.
     */
    public static Document loadDomDocument(String path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(path);
    }

    /**
     * Scrive il contenuto di un documento DOM su un file XML al percorso specificato.
     * Il documento viene formattato utilizzando l'indentazione per rendere
     * più leggibile il contenuto prodotto.
     *
     * @param dom Il {@code Document} che rappresenta la struttura DOM da scrivere su file.
     * @param path Il percorso del file in cui scrivere il contenuto XML.
     *
     * @throws TransformerException Se si verifica un errore durante il processo di trasformazione.
     */
    public static void writeDomDocument(Document dom, String path) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();

        // Impostazioni per rendere l'XML leggibile (indentazione)
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(dom);
        StreamResult result = new StreamResult(new File(path));

        transformer.transform(source, result);
    }

    /**
     * Stampa ricorsivamente la struttura e il contenuto di un {@code Node} DOM,
     * includendone il nome, gli attributi (per i nodi di tipo {@code Element}) e il
     * contenuto testuale (per i nodi di tipo {@code Text}).
     *
     * @param n Il {@code Node} da stampare. Può essere un {@code Element}, un {@code Text}
     *          o un altro tipo di nodo.
     * @param spaces Una stringa che rappresenta il livello di indentazione corrente, usata
     *               per formattare l'output e visualizzare graficamente la gerarchia dei nodi.
     */
    public static void print(Node n, String spaces) {
        if (n == null)
            return;
        if (n instanceof Element) {
            String s = spaces + n.getNodeName() + " (";
            NamedNodeMap map = n.getAttributes();
            if (map != null)
                for (int i = 0; i < map.getLength(); i++)
                    s += map.item(i).getNodeName() + "=" + map.item(i).getNodeValue();
            s += ")";
            System.out.println(s);
        } else  if (n instanceof Text)
            System.out.println(spaces + n.getNodeValue());
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
            print(children.item(i), spaces + "– ");
    }

    /**
     * Esegue una query XPath sul {@code Node} DOM specificato e restituisce i nodi
     * corrispondenti come {@code NodeList}.
     *
     * @param n Il {@code Node} su cui valutare la query XPath.
     * @param xPathQuery La stringa della query XPath usata per selezionare i nodi.
     *
     * @return Una {@code NodeList} contenente i nodi che corrispondono alla query XPath
     *         specificata, oppure {@code null} se il nodo in input o la query sono {@code null}.
     *
     * @throws XPathExpressionException Se si verifica un errore durante la valutazione della query XPath.
     */
    public static NodeList executeQuery(Node n, String xPathQuery) throws XPathExpressionException {
        if (n == null || xPathQuery == null)
            return null;

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        return (NodeList) xpath.evaluate(xPathQuery, n, XPathConstants.NODESET);
    }

    /**
     * Cerca, tramite XPath, un elemento figlio con il tag specificato e ne restituisce
     * il contenuto testuale. Il parametro {@code tag} può rappresentare sia il nome di un
     * figlio direttamente, che un percorso relativo
     *
     * @param elemento L'elemento XML in cui cercare il figlio.
     * @param tag Il percorso XPath relativo del figlio da cercare.
     *
     * @return Il contenuto testuale del primo figlio corrispondente al tag specificato.
     *
     * @throws XPathExpressionException Se si verifica un errore durante la valutazione della query XPath.
     *
     * @throws IllegalArgumentException Se non viene trovato nessun figlio corrispondente al tag specificato.
     *
     * @throws NullPointerException se {@code elemento} è {@code null}
     */
    public static String readTextNode(@NonNull Element elemento, String tag) throws XPathExpressionException {
        NodeList nodi = DOMUtils.executeQuery(elemento, "./" + tag);
        if (nodi.getLength() == 0)
            throw new IllegalArgumentException("Elemento XML mancante: " + tag);
        return nodi.item(0).getTextContent();
    }

}
