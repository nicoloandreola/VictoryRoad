package it.unicam.cs.mpgc.rpg129542.model.match;

import it.unicam.cs.mpgc.rpg129542.model.personaggio.Personaggio;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaDifensiva;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaOffensiva;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSpeciale;
import it.unicam.cs.mpgc.rpg129542.model.tecniche.TecnicaSupporto;
import lombok.NonNull;

/**
 * Prima implementazione standard delle regole definite da {@link LogicaMatch}.
 *
 * La classe si occupa di validare le combinazioni tra azioni offensive
 * e difensive, calcolare l'efficacia delle azioni e applicarne gli effetti
 * alle risorse dei personaggi.
 *
 * Non gestisce invece lo stato complessivo della partita, come punteggio,
 * possesso e conclusione del match, che rimangono responsabilità di {@link Match}.
 *
 * @author Nicolò Andreola
 */
public class LogicaStandard implements LogicaMatch {

    /**
     * {@inheritDoc}
     */
    @Override
    public EsitoTurno risolviAzione(@NonNull Personaggio attaccante, @NonNull AzioneAttaccante attacco,
                                    @NonNull Personaggio difensore, @NonNull AzioneDifensore difesa,
                                    TecnicaSpeciale tecnicaAttaccante, TecnicaSpeciale tecnicaDifensore) {
        if (!this.isRispostaValida(attacco, difesa))
            throw new IllegalArgumentException("La risposta difensiva non è compatibile con l'attacco!");

        return switch (attacco) {

            case TIRO ->
                    this.eseguiTiro(attaccante, difensore, difesa, tecnicaDifensore);

            case DRIBBLING ->
                    this.eseguiDribbling(attaccante, difensore, difesa, tecnicaDifensore);

            case TECNICA_OFFENSIVA ->
                    this.eseguiTecnicaOffensiva(attaccante, tecnicaAttaccante, difensore, difesa, tecnicaDifensore);

            case TECNICA_SUPPORTO ->
                    this.eseguiSupporto(attaccante, tecnicaAttaccante);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRispostaValida(@NonNull AzioneAttaccante attacco, @NonNull AzioneDifensore difesa) {
        return switch (attacco) {

            case TIRO, TECNICA_OFFENSIVA ->
                    difesa == AzioneDifensore.PARATA || difesa == AzioneDifensore.TECNICA_DIFENSIVA;

            case DRIBBLING ->
                    difesa == AzioneDifensore.CONTRASTO || difesa == AzioneDifensore.TECNICA_DIFENSIVA
                            || difesa == AzioneDifensore.TECNICA_SUPPORTO;

            // Se l'attaccante utilizza una TECNICA di SUPPORTO, non è richiesta alcuna risposta
            // del difensore, poiché la palla passa a quest'ultimo e si procede con il turno successivo.
            case TECNICA_SUPPORTO -> difesa == AzioneDifensore.NESSUNA;
        };
    }

    private EsitoTurno eseguiTiro(Personaggio attaccante, Personaggio difensore, AzioneDifensore difesa, TecnicaSpeciale tecnicaDifensore) {
        int valoreAttacco = attaccante.getStatisticheEffettive().getAttacco();
        int valoreDifesa = this.calcolaValoreDifesa(difensore, difesa, tecnicaDifensore);
        return this.applicaAttacco(difensore, valoreAttacco, valoreDifesa);
    }

    private EsitoTurno eseguiTecnicaOffensiva(Personaggio attaccante, TecnicaSpeciale tecnicaAttaccante,
                                              Personaggio difensore, AzioneDifensore difesa, TecnicaSpeciale tecnicaDifensore) {
        if (!(tecnicaAttaccante instanceof TecnicaOffensiva))
            throw new IllegalArgumentException("È richiesta una tecnica offensiva!");
        int valoreAttacco = tecnicaAttaccante.usa(attaccante);
        int valoreDifesa = this.calcolaValoreDifesa(difensore, difesa, tecnicaDifensore);
        return this.applicaAttacco(difensore, valoreAttacco, valoreDifesa);
    }

    private EsitoTurno eseguiDribbling(Personaggio attaccante, Personaggio difensore,
                                       AzioneDifensore difesa, TecnicaSpeciale tecnicaDifensore) {
        int valoreDribbling = attaccante.getStatisticheEffettive().getAgilita();
        int valoreContrasto;

        // Se il difensore sceglie supporto, rinuncia al contrasto e recupera stamina.
        if (difesa == AzioneDifensore.TECNICA_SUPPORTO) {
            this.recuperaStamina(difensore, tecnicaDifensore);
            valoreContrasto = 0;
        }
        else if (difesa == AzioneDifensore.CONTRASTO)
            valoreContrasto = difensore.getStatisticheEffettive().getAgilita();
        else {
            if (!(tecnicaDifensore instanceof TecnicaDifensiva))
                throw new IllegalArgumentException("È richiesta una tecnica difensiva!");
            valoreContrasto = tecnicaDifensore.usa(difensore);
        }

        return this.isDribblingRiuscito(valoreDribbling, valoreContrasto)
                ? EsitoTurno.DRIBBLING_RIUSCITO
                : EsitoTurno.PALLA_PERSA;
    }

    private EsitoTurno eseguiSupporto(Personaggio personaggio, TecnicaSpeciale tecnica) {
        this.recuperaStamina(personaggio, tecnica);
        return EsitoTurno.STAMINA_RECUPERATA;
    }

    private void recuperaStamina(Personaggio personaggio, TecnicaSpeciale tecnicaSupporto) {
        if (!(tecnicaSupporto instanceof TecnicaSupporto))
            throw new IllegalArgumentException("È richiesta una tecnica di supporto!");
        int quantitaRecuperata = tecnicaSupporto.usa(personaggio);
        personaggio.recuperaStamina(quantitaRecuperata);
    }

    private EsitoTurno applicaAttacco(Personaggio difensore, int valoreAttacco, int valoreDifesa) {
        int danno = this.calcolaDanno(valoreAttacco, valoreDifesa);
        if (danno == 0)
            return EsitoTurno.ATTACCO_RESPINTO;
        else {
            difensore.subisciDanno(danno);
            if (difensore.getResistenza() == 0)
                return EsitoTurno.GOL_SEGNATO;
            return EsitoTurno.DANNO_INFLITTO;
        }
    }

    private int calcolaValoreDifesa(Personaggio difensore, AzioneDifensore difesa, TecnicaSpeciale tecnicaDifensore) {
        if(difesa == AzioneDifensore.PARATA)
            return difensore.getStatisticheEffettive().getDifesa();
        else if (!(tecnicaDifensore instanceof TecnicaDifensiva))
            throw new IllegalArgumentException("È richiesta una tecnica difensiva!");
        else return tecnicaDifensore.usa(difensore);
    }

    private int calcolaDanno(int valoreAttacco, int valoreDifesa) {
        return Math.max(0, valoreAttacco - valoreDifesa);
    }


    private boolean isDribblingRiuscito(int valoreDribbling, int valoreContrasto) {
        return valoreDribbling > valoreContrasto;
    }
}
