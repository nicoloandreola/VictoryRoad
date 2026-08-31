package it.unicam.cs.mpgc.rpg129542.persistence;

import it.unicam.cs.mpgc.rpg129542.model.livello.Campo;
import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.statistiche.*;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementazione dell'interfaccia {@link Persistenza} basata sul formato XML.
 *
 * La classe si occupa di caricare i dati statici necessari
 * all'inizializzazione del gioco dai file XML presenti nella cartella
 * resources dell'applicazione e, successivamente, di gestire il salvataggio
 * e il caricamento della progressione del giocatore.
 *
 * Per l'analisi dei documenti XML utilizza la classe {@link DOMUtils},
 * che mette a disposizione operazioni basate sulle API DOM e XPath.
 * In questo modo questa classe mantiene la responsabilità di tradurre
 * i dati XML negli oggetti del model, delegando le operazioni generiche
 * sui documenti alla relativa classe di utilità.
 *
 * @author Nicolò Andreola
 */

public class PersistenzaXML implements Persistenza {

    // Costanti che rappresentano il percorso, relativo alla root del classpath,
    // dei file XML contenenti la configurazione iniziale delle tecniche speciali
    // dei personaggi e dei livelli del gioco
    private static final String FILE_TECNICHE = "persistence/tecniche.xml";
    private static final String FILE_PERSONAGGI = "persistence/personaggi.xml";
    private static final String FILE_LIVELLI = "persistence/livelli.xml";

    /**
     * {@inheritDoc}
     *
     * Il file delle tecniche viene caricato dalle resources e analizzato
     * tramite DOM e XPath. Ogni elemento {@code <tecnica>} del file viene quindi
     * trasformato nella corrispondente sottoclasse concreta di {@link TecnicaSpeciale}.
     *
     * @return insieme delle tecniche speciali definite nella configurazione
     *
     * @throws IOException se la risorsa non viene trovata oppure si verifica
     *                     un errore durante la lettura, il parsing o la
     *                     conversione dei dati XML
     */
    @Override
    public Set<TecnicaSpeciale> caricaTecniche() throws IOException {
        try {
            Document document = this.caricaDocumento(FILE_TECNICHE);
            NodeList nodiTecniche = DOMUtils.executeQuery(document, "/tecniche/tecnica");
            Set<TecnicaSpeciale> tecniche = new HashSet<>();
            for (int i = 0; i < nodiTecniche.getLength(); i++) {
                Element elemento = (Element) nodiTecniche.item(i);
                tecniche.add(this.creaTecnica(elemento));
            }
            return tecniche;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
            throw new IOException("Errore durante il caricamento delle tecniche", e);
        }
    }

    @Override
    public List<Protagonista> caricaProtagonisti() throws IOException {
        try {
            Document document = this.caricaDocumento(FILE_PERSONAGGI);
            NodeList nodiProtagonisti = DOMUtils.executeQuery(document, "/personaggi/protagonisti/protagonista");
            Set<TecnicaSpeciale> tecniche = this.caricaTecniche();
            List<Protagonista> protagonisti = new ArrayList<>();
            for (int i = 0; i < nodiProtagonisti.getLength(); i++) {
                Element elemento = (Element) nodiProtagonisti.item(i);
                protagonisti.add(this.creaProtagonista(elemento, tecniche));
            }
            return protagonisti;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
            throw new IOException("Errore durante il caricamento dei protagonisti", e);
        }
    }

    @Override
    public List<Livello> caricaLivelli() throws IOException {
        try {
            Document document = this.caricaDocumento(FILE_LIVELLI);
            NodeList nodiLivelli = DOMUtils.executeQuery(document, "/livelli/livello");
            List<Avversario> avversari = this.caricaAvversari();
            List<Livello> livelli = new ArrayList<>();
            for (int i = 0; i < nodiLivelli.getLength(); i++) {
                Element elemento = (Element) nodiLivelli.item(i);
                livelli.add(this.creaLivello(elemento, avversari));
            }
            return livelli;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
            throw new IOException("Errore durante il caricamento dei livelli", e);
        }
    }

    /**
     * Carica i dati relativi a una partita salvata precedentemente.
     *
     * @return dati della progressione salvata
     * @throws IOException se si verifica un errore durante la lettura del salvataggio
     */
    @Override
    public SalvataggioDati caricaPartita() throws IOException {
        return null;
    }

    /**
     * Salva lo stato attuale di una partita del giocatore.
     *
     * @param dati dati da rendere persistenti
     * @throws IOException se si verifica un errore durante la scrittura del salvataggio
     */
    @Override
    public void salvaPartita(SalvataggioDati dati) throws IOException {

    }

    /**
     * Verifica se è presente un salvataggio precedente dal quale ricostruire una partita.
     *
     * @return {@code true} se il salvataggio esiste, {@code false} altrimenti
     */
    @Override
    public boolean verificaSalvataggio() {
        return false;
    }

    private Document caricaDocumento(String file)
            throws IOException, ParserConfigurationException, SAXException {
        // Utilizzo il metodo getResource() e non getResourceAsStream() solo perché il metodo
        // DOMUtils.loadDomDocument prende una String che rappresenti un percorso, non un InputStream
        URL risorsa = PersistenzaXML.class.getClassLoader().getResource(file);
        if (risorsa == null)
            throw new IOException("Risorsa XML non trovata: " + file);
        // Trasformo l'URL in String per passarlo al metodo della classe DOMUtils
        String percorso = risorsa.toExternalForm();
        return DOMUtils.loadDomDocument(percorso);
    }

    // Costruisce l'istanza concreta di una tecnica basandosi
    // sull'attributo "tipo" del nodo <tecnica> del file XML
    private TecnicaSpeciale creaTecnica(Element elemento) throws XPathExpressionException{
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

    private Protagonista creaProtagonista(Element elemento, Set<TecnicaSpeciale> tecniche)
            throws XPathExpressionException {
        String id = elemento.getAttribute("id");
        String nome = DOMUtils.readTextNode(elemento, "nome");
        StatisticheBase statistiche = this.creaStatistiche(elemento);
        Element tecnicaRef = (Element) DOMUtils.executeQuery(elemento, "./tecniche/tecnicaRef").item(0);
        String idTecnica = tecnicaRef.getAttribute("ref");
        TecnicaSpeciale tecnica = this.trovaTecnica(tecniche, idTecnica);
        return new Protagonista(nome, id, statistiche, tecnica);
    }

    private StatisticheBase creaStatistiche(Element elemento) throws XPathExpressionException{
        int attacco = Integer.parseInt(DOMUtils.readTextNode(elemento, "statistiche/attacco"));
        int difesa = Integer.parseInt(DOMUtils.readTextNode(elemento, "statistiche/difesa"));
        int agilita = Integer.parseInt(DOMUtils.readTextNode(elemento, "statistiche/agilita"));
        return new StatisticheBase(attacco, difesa, agilita);
    }

    private List<Avversario> caricaAvversari()
            throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Document document = this.caricaDocumento(FILE_PERSONAGGI);
        NodeList nodiAvversari = DOMUtils.executeQuery(document, "/personaggi/avversari/avversario");
        Set<TecnicaSpeciale> tecniche = this.caricaTecniche();
        List<Avversario> avversari = new ArrayList<>();
        for (int i = 0; i < nodiAvversari.getLength(); i++) {
            Element elemento = (Element) nodiAvversari.item(i);
            avversari.add(this.creaAvversario(elemento, tecniche));
        }
        return avversari;
    }

    private Avversario creaAvversario(Element elemento, Set<TecnicaSpeciale> tecniche)
            throws XPathExpressionException {
        String id = elemento.getAttribute("id");
        String nome = DOMUtils.readTextNode(elemento, "nome");
        StatisticheBase statistiche = this.creaStatistiche(elemento);
        NodeList nodiTecniche = DOMUtils.executeQuery(elemento, "./tecniche/tecnicaRef");
        Set<TecnicaSpeciale> tecnicheAvversario = new HashSet<>();
        for (int i = 0; i < nodiTecniche.getLength(); i++) {
            Element tecnicaRef = (Element) nodiTecniche.item(i);
            String idTecnica = tecnicaRef.getAttribute("ref");
            tecnicheAvversario.add(this.trovaTecnica(tecniche, idTecnica));
        }
        return new Avversario(nome, id, statistiche, tecnicheAvversario);
    }

    private TecnicaSpeciale trovaTecnica(Set<TecnicaSpeciale> tecniche, String id) {
        return tecniche.stream().filter(tecnica -> tecnica.getId().equals(id))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Tecnica non trovata: " + id));
    }

    private Livello creaLivello(Element elemento, List<Avversario> avversari) throws XPathExpressionException {
        int numero = Integer.parseInt(elemento.getAttribute("numero"));

        Element elementoCampo =
                (Element) DOMUtils.executeQuery(
                        elemento,
                        "./campo"
                ).item(0);

        Campo campo =
                this.creaCampo(elementoCampo);

        NodeList riferimentiAvversari =
                DOMUtils.executeQuery(
                        elemento,
                        "./avversari/avversarioRef"
                );

        if (riferimentiAvversari.getLength() != 2)
            throw new IllegalArgumentException(
                    "Ogni livello deve contenere due avversari!"
            );

        Element ref1 =
                (Element) riferimentiAvversari.item(0);

        Element ref2 =
                (Element) riferimentiAvversari.item(1);

        Avversario avversario1 =
                this.trovaAvversario(
                        avversari,
                        ref1.getAttribute("ref")
                );

        Avversario avversario2 =
                this.trovaAvversario(
                        avversari,
                        ref2.getAttribute("ref")
                );

        return new Livello(
                numero,
                campo,
                avversario1,
                avversario2
        );
    }

    private Campo creaCampo(Element elemento) throws XPathExpressionException {
        String nome = DOMUtils.readTextNode(elemento, "nome");
        String descrizione = DOMUtils.readTextNode(elemento, "descrizione");
        Element elementoModificatore = (Element) DOMUtils.executeQuery(elemento, "./modificatore").item(0);
        int attacco = Integer.parseInt(DOMUtils.readTextNode(elementoModificatore, "attacco"));
        int difesa = Integer.parseInt(DOMUtils.readTextNode(elementoModificatore, "difesa"));
        int agilita = Integer.parseInt(DOMUtils.readTextNode(elementoModificatore, "agilita"));
        String tipo = elementoModificatore.getAttribute("tipo");
        ModificatoreStatistiche modificatore = switch (tipo) {

                    case "BONUS" -> new BonusStatistiche(attacco, difesa, agilita);

                    case "MALUS" -> new MalusStatistiche(attacco, difesa, agilita);

                    default -> throw new IllegalArgumentException("Tipo di modificatore non valido: " + tipo);
                };
        return new Campo(nome, descrizione, modificatore);
    }

    private Avversario trovaAvversario(List<Avversario> avversari, String id) {
        return avversari.stream().filter(avversario -> avversario.getId().equals(id)).
                findFirst().orElseThrow(() -> new IllegalArgumentException("Avversario non trovato: " + id));
    }
}