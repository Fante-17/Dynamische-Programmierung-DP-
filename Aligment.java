/***
 * Diese Klasse hält alle Daten die ein Alignment beschreiben (Eingabe, Be-
 * arbeitung, Ausgabe). Instanzen dieser Klasse sind also "Passing-Objects",
 * die durch unterschiedliche Methoden durchgereicht, bearbeitet und wieder
 * zurückgegeben werden.
 *
 * (Bemerkung: Diese Entwurfsentscheidung kann schnell zu Seiteneffekten
 * führen und wurde hier primär zur Aufteilung der Tests der einzelnen
 * Bearbeitungsschritte und nicht aus algorithmischer Bewertung heraus
 * gefällt.)
 *
 * In unserem Fall soll das Alignment-Objekt folgende Schritte durchlaufen:
 *
 *  1) Eingabedaten per Konstruktor
 *      - Sequenz X,
 *      - Sequenz Y,
 *      - Scores-Instanz (match, mismatch, gap) oder Default-Scores
 *
 *  2) Übergabe an die NeedlemanWunsch-Initialisierung der ScoreMatrix
 *      Benötigt:
 *          - Sequenz X
 *          - Sequenz Y
 *          - Scores-Instanz
 *      Initialisierung der ersten Zeile/Spalte der ScoreMatrix
 *      Nachbedingungen:
 *          - Alignment enthält initialisierte ScoreMatrix
 *          - Alignment isInitialized() --> true
 *
 *  3) Übergabe an die NeedlemanWunsch-Berechnung der ScoreMatrix
 *      Benötigt:
 *          - Sequenz X
 *          - Sequenz Y
 *          - ScoreMatrix (initialisiert)
 *      Resilienz: Noch nicht initialisiert? --> initialisiere Scorematrix
 *      Nachbedingungen:
 *          - Alignment enthält berechnete ScoreMatrix
 *          - Alignment enthält gespeicherte BacktrackDirections
 *          - Alignment enthält gespeicherte AlignmentOperations
 *          - Alignment isCalculated() --> true
 *
 *  4) Übergabe an die NeedlemanWunsch-Rekonstruktion der Alignment-Strings
 *      Benötigt:
 *          - Sequenz X,
 *          - Sequenz Y,
 *          - Scores-Instanz
 *          - ScoreMatrix (initialisiert und berechnet)
 *      Nachbedingungen:
 *          - Alignment enthält rekonstruierten X_Alignment-String
 *          - Alignment enthält rekonstruierten Y_Alignment-String
 *          - Alignment enthält rekostruierten Operations-String ('_', '|', '*')
 *
 */

public class Alignment {

    public static char DEFAULT_GAP_CHAR = '_';

    /**
     * Eingabe-Daten:
     */
    // Die Sequenzen die aufeinander ausgerichtet werden sollen
    private String X_Sequence;
    private String Y_Sequence;

    // Die Score-Werte-Einstellungen für die Scoring-Funktion w (weight)
    private Scores scores;

    // Das verwendete GAP-Zeichen
    private char GAP_CHAR = '_';

    /**
     * Ausgabe-Daten
     */
    // Die aufeinander ausgerichteten Ergebnis-Sequenzen
    private String X_Alignment;
    private String Y_Alignment;

    // Dieser String hält bei der Rekonstruktion der Alignments (aus der Score-Matrix)
    // die aufgetretenen Fälle fest (MATCH (*), MISMATCH (|), Einfügen eines GAPs in x oder y (_))
    private String operationsString;

    // Die Ergebnis-ScoreMatrix hält die vom NeedlemanWunsch-Algorithmus berechneten Score-Werte fest
    private int[][] scoreMatrix;

    // Flags für die Bearbeitungsschritte
    private boolean initialized;
    private boolean calculated;
    private boolean reconstructed;


    /**
     * Constructors
     */
    public Alignment() {
        this.X_Sequence="";
        this.Y_Sequence="";
        this.scores=new Scores();
        this.X_Alignment="";
        this.Y_Alignment="";
        this.operationsString="";
        this.scoreMatrix= new int[0][0];


    }

    public Alignment(String X_Sequence, String Y_Sequence) {
        this();
        this.X_Sequence=X_Sequence;
        this.Y_Sequence=Y_Sequence;
        this.X_Alignment=X_Sequence;
        this.Y_Alignment=Y_Sequence;
        this.scores=new Scores();
        this.scoreMatrix=new int[X_Sequence.length()+1][Y_Sequence.length()+1];

    }

    public Alignment(String X_Sequence, String Y_Sequence, Scores scores) {
        this(X_Sequence, Y_Sequence);
        this.X_Sequence=X_Sequence;
        this.Y_Sequence=Y_Sequence;
        this.X_Alignment=X_Sequence;
        this.Y_Alignment=Y_Sequence;
        this.scores=scores;
    }

    public Alignment(String X_Sequence, String Y_Sequence, Scores scores, char GAP_CHAR) {
        this(X_Sequence, Y_Sequence, scores);
        this.X_Sequence=X_Sequence;
        this.Y_Sequence=Y_Sequence;
        this.X_Alignment=X_Sequence;
        this.Y_Alignment=Y_Sequence;
        this.scores=scores;
        this.GAP_CHAR=GAP_CHAR;
    }
    /** end Constructors */

    /**
     * Getter/Setter
     */
    public String getX_Sequence() {
        return X_Sequence;
    }

    public void setX_Sequence(String x_Sequence) {
        X_Sequence = x_Sequence;
    }

    public String getY_Sequence() {
        return Y_Sequence;
    }

    public void setY_Sequence(String y_Sequence) {
        Y_Sequence = y_Sequence;
    }

    public Scores getScoreSettings() {
        return scores;
    }

    public void setScoreSettings(Scores scores) {
        this.scores = scores;
    }

    public char getGapCharacter() {
        return GAP_CHAR;
    }

    public void setGapCharacter(char GAP_CHAR) {
        this.GAP_CHAR = GAP_CHAR;
    }

    public String getX_Alignment() {
        return X_Alignment;
    }

    public void setX_Alignment(String x_Alignment) {
        X_Alignment = x_Alignment;
    }

    public String getY_Alignment() {
        return Y_Alignment;
    }

    public void setY_Alignment(String y_Alignment) {
        Y_Alignment = y_Alignment;
    }

    public String getOperationsString() {
        return operationsString;
    }

    public void setOperationsString(String operationsString) {
        this.operationsString = operationsString;
    }

    public void setScoreMatrix(int[][] scoreMatrix) {
        this.scoreMatrix = scoreMatrix;
    }

    public int[][] getScoreMatrix() {
        return scoreMatrix;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public boolean isReconstructed() {
        return reconstructed;
    }

    public void setReconstructed(boolean reconstructed) {
        this.reconstructed = reconstructed;
    }

    public int getScore() {
        return scoreMatrix[X_Sequence.length()][Y_Sequence.length()];
    }

    /**
     * end Getter/Setter
     */
}