package it.gov.fermitivoli.model;

/**
 * Descrizione delle foto
 */
public interface IndirizziStudioDescription {
    public static class IndirizziStudioDescription_CategoriaLivello1 implements IndirizziStudioDescription {
        public final String categoria;

        public IndirizziStudioDescription_CategoriaLivello1(String categoria) {
            this.categoria = categoria;
        }
    }

    public static class IndirizziStudioDescription_CorsoLivello2 implements IndirizziStudioDescription {
        public final String nomeCorso;
        public final Integer idDescrizioneCorso;
        public final Integer idImmagine;

        public IndirizziStudioDescription_CorsoLivello2(String nomeCorso, Integer idDescrizioneCorso, Integer idImmagine) {
            this.nomeCorso = nomeCorso;
            this.idDescrizioneCorso = idDescrizioneCorso;
            this.idImmagine = idImmagine;
        }
    }

    public static class IndirizziStudioDescription_RisorsaCorsoLivello3 implements IndirizziStudioDescription {
        public final String nomeIndirizzo;
        public final String nomeRisorsa;
        public final Integer idRisorsa;

        public IndirizziStudioDescription_RisorsaCorsoLivello3(String nomeIndirizzo, String nomeRisorsa, Integer idRisorsa) {
            this.nomeIndirizzo = nomeIndirizzo;
            this.nomeRisorsa = nomeRisorsa;
            this.idRisorsa = idRisorsa;
        }
    }


}
