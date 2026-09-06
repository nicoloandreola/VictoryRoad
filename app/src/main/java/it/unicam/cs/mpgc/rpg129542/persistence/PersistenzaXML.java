package it.unicam.cs.mpgc.rpg129542.persistence;

import it.unicam.cs.mpgc.rpg129542.model.livello.Livello;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Avversario;
import it.unicam.cs.mpgc.rpg129542.model.personaggio.Protagonista;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import it.unicam.cs.mpgc.rpg129542.persistence.configurazione.DeserializzatoreLivelliXML;
import it.unicam.cs.mpgc.rpg129542.persistence.configurazione.DeserializzatorePersonaggiXML;
import it.unicam.cs.mpgc.rpg129542.persistence.configurazione.DeserializzatoreTecnicheXML;
import it.unicam.cs.mpgc.rpg129542.persistence.salvataggio.DeserializzatoreSalvataggioXML;
import it.unicam.cs.mpgc.rpg129542.persistence.salvataggio.SalvataggioDati;
import it.unicam.cs.mpgc.rpg129542.persistence.salvataggio.SerializzatoreSalvataggioXML;
import it.unicam.cs.mpgc.rpg129542.persistence.utils.DOMUtils;
import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URL;
import java.io.File;
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
 *
 * La trasformazione dei singoli elementi XML negli oggetti del model è invece
 * affidata ai deserializzatori specifici {@link DeserializzatoreTecnicheXML},
 * {@link DeserializzatorePersonaggiXML} e {@link DeserializzatoreLivelliXML}.
 * In questo modo la classe mantiene principalmente la responsabilità di
 * coordinare le diverse operazioni necessarie alla persistenza, senza
 * conoscere i dettagli di costruzione di ogni oggetto del dominio.
 *
 * Infine, le tecniche speciali già caricate vengono salvate in {@link #tecnicheCache},
 * poiché sono utilizzate come riferimenti durante la costruzione dei personaggi:
 * in questo modo si evita di rifare il parsing di tecniche.xml ogni volta.
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

    // Costanti che rappresentano il percorso del file XML contenente una partita salvata
    private static final File CARTELLA_SALVATAGGIO = new File(System.getProperty("user.home"), "VictoryRoad");
    private static final File FILE_SALVATAGGIO = new File(CARTELLA_SALVATAGGIO, "salvataggio.xml");

    private Set<TecnicaSpeciale> tecnicheCache;

    /**
     * {@inheritDoc}
     *
     * Il file delle tecniche viene caricato dalle resources e analizzato
     * tramite DOM e XPath. Ogni elemento {@code <tecnica>} del file viene quindi
     * trasformato nella corrispondente sottoclasse concreta di {@link TecnicaSpeciale}.
     *
     * @return insieme delle tecniche speciali definite nella configurazione
     *
     * @throws IOException se la risorsa non viene trovata oppure si verifica un errore
     *                      durante la lettura, il parsing o la conversione dei dati XML
     */
    @Override
    public Set<TecnicaSpeciale> caricaTecniche() throws IOException {
        if (this.tecnicheCache != null)
            return this.tecnicheCache;
        try {
            Document document = this.caricaDomDaResources(FILE_TECNICHE);
            NodeList nodiTecniche = DOMUtils.executeQuery(document, "/tecniche/tecnica");
            Set<TecnicaSpeciale> tecniche = new HashSet<>();
            for (int i = 0; i < nodiTecniche.getLength(); i++) {
                Element elemento = (Element) nodiTecniche.item(i);
                tecniche.add(DeserializzatoreTecnicheXML.creaTecnica(elemento));
            }
            // Utilizzo Set.copyOf per proteggere il campo e favorire l'incapsulamento
            this.tecnicheCache = Set.copyOf(tecniche);
            return this.tecnicheCache;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException | IllegalArgumentException e) {
            throw new IOException("Errore durante il caricamento delle tecniche", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * I protagonisti vengono caricati dal relativo file XML e costruiti tramite
     * {@link DeserializzatorePersonaggiXML}. I riferimenti alle tecniche speciali
     * possedute vengono risolti utilizzando l'insieme restituito da {@link #caricaTecniche()}.
     *
     * @return lista dei protagonisti definiti nella configurazione
     *
     * @throws IOException se la risorsa non viene trovata oppure si verifica un errore
     *                      durante la lettura, il parsing o la conversione dei dati XML
     */
    @Override
    public List<Protagonista> caricaProtagonisti() throws IOException {
        try {
            Document document = this.caricaDomDaResources(FILE_PERSONAGGI);
            NodeList nodiProtagonisti = DOMUtils.executeQuery(document, "/personaggi/protagonisti/protagonista");
            Set<TecnicaSpeciale> tecniche = this.caricaTecniche();
            List<Protagonista> protagonisti = new ArrayList<>();
            for (int i = 0; i < nodiProtagonisti.getLength(); i++) {
                Element elemento = (Element) nodiProtagonisti.item(i);
                protagonisti.add(DeserializzatorePersonaggiXML.creaProtagonista(elemento, tecniche));
            }
            return protagonisti;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException | IllegalArgumentException e) {
            throw new IOException("Errore durante il caricamento dei protagonisti", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * I livelli vengono caricati dal relativo file XML e costruiti tramite
     * {@link DeserializzatoreLivelliXML}. Prima della loro creazione vengono
     * caricati gli avversari definiti nella configurazione, necessari per
     * risolvere i riferimenti presenti all'interno di ciascun livello.
     *
     * @return lista dei livelli definiti nella configurazione
     *
     * @throws IOException se la risorsa non viene trovata oppure si verifica un errore
     *                      durante la lettura, il parsing o la conversione dei dati XML
     */
    @Override
    public List<Livello> caricaLivelli() throws IOException {
        try {
            Document document = this.caricaDomDaResources(FILE_LIVELLI);
            NodeList nodiLivelli = DOMUtils.executeQuery(document, "/livelli/livello");
            List<Avversario> avversari = this.caricaAvversari();
            List<Livello> livelli = new ArrayList<>();
            for (int i = 0; i < nodiLivelli.getLength(); i++) {
                Element elemento = (Element) nodiLivelli.item(i);
                livelli.add(DeserializzatoreLivelliXML.creaLivello(elemento, avversari));
            }
            return livelli;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException | IllegalArgumentException e) {
            throw new IOException("Errore durante il caricamento dei livelli", e);
        }
    }

    // Permette a caricaLivelli() di caricare gli avversari definiti in personaggi.xml
    private List<Avversario> caricaAvversari()
            throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Document document = this.caricaDomDaResources(FILE_PERSONAGGI);
        NodeList nodiAvversari = DOMUtils.executeQuery(document, "/personaggi/avversari/avversario");
        Set<TecnicaSpeciale> tecniche = this.caricaTecniche();
        List<Avversario> avversari = new ArrayList<>();
        for (int i = 0; i < nodiAvversari.getLength(); i++) {
            Element elemento = (Element) nodiAvversari.item(i);
            avversari.add(DeserializzatorePersonaggiXML.creaAvversario(elemento, tecniche));
        }
        return avversari;
    }

    private Document caricaDomDaResources(String file)
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

    /**
     * {@inheritDoc}
     *
     * A differenza dei metodi {@link #caricaTecniche()}, {@link #caricaProtagonisti()}
     * {@link #caricaLivelli()}, per caricare i dati non passa per {@link #caricaDomDaResources(String)}
     * poiché il file di salvataggio non è situato nella cartella resources come
     * gli altri, quindi chiama direttamente {@link DOMUtils#loadDomDocument(String)}
     */
    @Override
    public SalvataggioDati caricaPartita() throws IOException {
        if (!this.verificaSalvataggio())
            throw new IOException("Nessun salvataggio disponibile!");
        try {
            Document document = DOMUtils.loadDomDocument(FILE_SALVATAGGIO.getPath());
            return DeserializzatoreSalvataggioXML.creaSalvataggio(document.getDocumentElement());
        } catch (ParserConfigurationException | SAXException | XPathExpressionException | IllegalArgumentException e) {
            throw new IOException("Errore durante il caricamento del salvataggio", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void salvaPartita(@NonNull SalvataggioDati dati) throws IOException {
        if (!CARTELLA_SALVATAGGIO.exists() && !CARTELLA_SALVATAGGIO.mkdirs())
            throw new IOException("Impossibile creare la cartella di salvataggio!");
        try {
            Document document = SerializzatoreSalvataggioXML.creaDocumento(dati);
            DOMUtils.writeDomDocument(document, FILE_SALVATAGGIO.getPath());

        } catch (ParserConfigurationException | TransformerException e) {
            throw new IOException("Errore durante il salvataggio della partita", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verificaSalvataggio() {
        return FILE_SALVATAGGIO.exists();
    }
}