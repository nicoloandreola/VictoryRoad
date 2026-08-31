package it.unicam.cs.mpgc.rpg129542.persistence;

import it.unicam.cs.mpgc.rpg129542.model.tecniche.*;
import lombok.NonNull;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathExpressionException;

/**
 * Responsabile della trasformazione di un elemento XML {@code <tecnica>} nella
 * corrispondente sottoclasse concreta di {@link TecnicaSpeciale}.
 *
 * @author Nicolò Andreola
 */
public class DeserializzatoreTecnicheXML {

    /**
     * Costruisce una tecnica speciale a partire dal relativo elemento XML:
     * la sottoclasse concreta da istanziare viene determinata attraverso
     * l'attributo {@code tipo} dell'elemento {@code <tecnica>}.
     *
     * @param elemento Il nodo {@code <tecnica>} da mappare.
     *
     * @return L'istanza della classe {@link TecnicaSpeciale} corrispondente.
     *
     * @throws XPathExpressionException Se si verifica un errore durante la lettura dei campi.
     *
     * @throws IllegalArgumentException se un dato obbligatorio è mancante, non è convertibile nel
     *                                  tipo previsto oppure non rispetta i vincoli definiti dal model
     *
     * @throws NullPointerException se {@code elemento} è {@code null}
     */
    public static TecnicaSpeciale creaTecnica(@NonNull Element elemento) throws XPathExpressionException {
        String id = elemento.getAttribute("id");
        TipoTecnica tipo = TipoTecnica.valueOf(elemento.getAttribute("tipo"));
        String nome = DOMUtils.readTextNode(elemento, "nome");
        int potenza = Integer.parseInt(DOMUtils.readTextNode(elemento, "potenza"));

        return switch (tipo) {
            case OFFENSIVA -> new TecnicaOffensiva(nome, id, potenza,
                    Integer.parseInt(DOMUtils.readTextNode(elemento, "costoStamina")));

            case DIFENSIVA -> new TecnicaDifensiva(nome, id, potenza,
                    Integer.parseInt(DOMUtils.readTextNode(elemento, "costoStamina")));

            case SUPPORTO -> new TecnicaSupporto(nome, id, potenza);
        };
    }
}
