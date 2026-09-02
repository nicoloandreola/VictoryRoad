package it.unicam.cs.mpgc.rpg129542.persistence.configurazione;

import it.unicam.cs.mpgc.rpg129542.model.livello.Campo;
import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.BonusStatistiche;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.MalusStatistiche;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.ModificatoreStatistiche;
import it.unicam.cs.mpgc.rpg129542.persistence.utils.DOMUtils;
import lombok.NonNull;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.List;

/**
 * Responsabile della trasformazione degli elementi XML {@code <livello>} e
 * {@code <campo>} nei corrispondenti oggetti del model.
 *
 * Durante la costruzione ricostruisce anche il {@link Campo} associato al
 * livello e il relativo {@link ModificatoreStatistiche}, oltre a risolvere
 * i riferimenti agli avversari precedentemente caricati.
 *
 * @author Nicolò Andreola
 */
public class DeserializzatoreLivelliXML {

    /**
     * Costruisce un {@link Livello} a partire dal relativo elemento XML,
     * risolvendo i due avversari referenziati all'interno della lista fornita.
     *
     * @param elemento Il nodo {@code <livello>} da mappare.
     *
     * @param avversari La lista completa degli avversari già caricati, usata per risolvere i riferimenti.
     *
     * @return L'istanza della classe {@link Livello} corrispondente.
     *
     * @throws XPathExpressionException Se si verifica un errore durante la lettura dei campi.
     *
     * @throws IllegalArgumentException se un livello non contiene esattamente 2 avversari, se un
     *                                  riferimento non può essere risolto o se i dati XML
     *                                  non rispettano i vincoli definiti dal model
     *
     * @throws NullPointerException se {@code elemento} o {@code avversari} è {@code null}
     */

    public static Livello creaLivello(@NonNull Element elemento, @NonNull List<Avversario> avversari) throws XPathExpressionException {
        int numero = Integer.parseInt(elemento.getAttribute("numero"));
        Element elementoCampo = DOMUtils.readElement(elemento, "campo");
        Campo campo = creaCampo(elementoCampo);
        NodeList riferimentiAvversari = DOMUtils.executeQuery(elemento, "./avversari/avversarioRef");

        if (riferimentiAvversari.getLength() != 2)
            throw new IllegalArgumentException("Ogni livello deve contenere due avversari!");

        Element ref1 = (Element) riferimentiAvversari.item(0);
        Element ref2 = (Element) riferimentiAvversari.item(1);
        Avversario avversario1 = trovaAvversario(avversari, ref1.getAttribute("ref"));
        Avversario avversario2 = trovaAvversario(avversari, ref2.getAttribute("ref"));

        return new Livello(numero, campo, avversario1, avversario2);
    }

    private static Campo creaCampo(Element elemento) throws XPathExpressionException {
        String nome = DOMUtils.readTextNode(elemento, "nome");
        String descrizione = DOMUtils.readTextNode(elemento, "descrizione");
        Element elementoModificatore = DOMUtils.readElement(elemento, "modificatore");
        ModificatoreStatistiche modificatore = creaModificatore(elementoModificatore);
        return new Campo(nome, descrizione, modificatore);
    }

    private static ModificatoreStatistiche creaModificatore(Element elemento) throws XPathExpressionException {
        int attacco = Integer.parseInt(DOMUtils.readTextNode(elemento, "attacco"));
        int difesa = Integer.parseInt(DOMUtils.readTextNode(elemento, "difesa"));
        int agilita = Integer.parseInt(DOMUtils.readTextNode(elemento, "agilita"));

        String tipo = elemento.getAttribute("tipo");
        return switch (tipo) {

            case "BONUS" -> new BonusStatistiche(attacco, difesa, agilita);

            case "MALUS" -> new MalusStatistiche(attacco, difesa, agilita);

            default -> throw new IllegalArgumentException("Tipo di modificatore non valido: " + tipo);
        };
    }

    // Cerca nella lista l'avversario associato all'identificativo ricevuto.
    private static Avversario trovaAvversario(List<Avversario> avversari, String id) {
        return avversari.stream().filter(avversario -> avversario.getId().equals(id)).
                findFirst().orElseThrow(() -> new IllegalArgumentException("Avversario non trovato: " + id));
    }
}
