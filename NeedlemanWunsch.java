/***
 *
 *  Diese Klasse hält Methoden bereit zum Alignment zweier Sequenzen mithilfe des
 *  NeedlemanWunsch-Algorithmus.
 *
 *  Bemerkung:
 *  Die einzelnen Schritte des Algorithmus wurden in separate Methoden verlagert,
 *  um besser getestet werden zu können.
 *
 */


@SuppressWarnings("ConstantConditions")
public class NeedlemanWunsch {

    /***
     * Liefert das Alignment zweier Sequenzen
     *
     */
    public static Alignment align(Alignment alignment) {
        alignment = initialize(alignment);
        alignment = calculate(alignment);
        alignment = reconstruct(alignment);
        return alignment;
    }

    public static Alignment align(String S1, String S2) {
        Alignment alignment=new Alignment(S1,S2);
        alignment.setX_Alignment(S1);
        alignment.setY_Alignment(S2);
        return align(alignment);
    }

    public static Alignment align(String S1, String S2, Scores scores) {
        Alignment alignment=new Alignment(S1,S2,scores);
        alignment.setX_Alignment(S1);
        alignment.setY_Alignment(S2);
        alignment.setScoreSettings(scores);
        return align(alignment);
    }

    private static Alignment align(String S1, String S2, Scores scores, char GAP_CHAR) {
        Alignment alignment=new Alignment();
        alignment.setX_Alignment(S1);
        alignment.setY_Alignment(S2);
        alignment.setScoreSettings(scores);
        alignment.setGapCharacter(GAP_CHAR);
        return align(alignment);
    }


    /***
     *
     * Initialisierung der Score-Matrix
     *
     *      Benötigt:
     *          - Sequenz X
     *          - Sequenz Y
     *          - Scores-Instanz
     *      Initialisierung der ersten Zeile/Spalte der ScoreMatrix
     *      Nachbedingungen:
     *          - Alignment enthält initialisierte ScoreMatrix
     *          - Alignment isInitialized() --> true
     */
    public static Alignment initialize(Alignment alignment) {
        Alignment a=alignment;

        int value=a.getScoreSettings().GAP;
        int[][] tab=alignment.getScoreMatrix();

        for(int i=1;i<tab.length;i++){

            tab[i][0]=value;
            if(i<tab[0].length)
                tab[0][i]=value;

            value+=a.getScoreSettings().GAP;
        }

        a.setScoreMatrix(tab);
        a.setInitialized(true);
        return a;
    }



    /***
     *
     *      Benötigt:
     *          - Sequenz X
     *          - Sequenz Y
     *          - ScoreMatrix (initialisiert)
     *      Resilienz: Noch nicht initialisiert? --> initialisiere Scorematrix
     *      Nachbedingungen:
     *          - Alignment enthält berechnete ScoreMatrix
     *          - Alignment isCalculated() --> true
     */
    public static Alignment calculate(Alignment alignment) {
        alignment=new Alignment(alignment.getX_Sequence(),alignment.getY_Sequence(),alignment.getScoreSettings());
        initialize(alignment);
        int res=0, left=0, top=0, topLeft=0, gap=alignment.getScoreSettings().GAP,max=0;
        String strX=alignment.getX_Alignment();
        String strY=alignment.getY_Alignment();
        int[][] tab=alignment.getScoreMatrix();


        for(int i=1;i<tab.length;i++){
            for(int j=1; j<tab[0].length;j++){
                left = tab[i][j - 1] ;
                top = tab[i - 1][j] ;
                topLeft = tab[i - 1][j - 1] ;
                max=Math.max(topLeft,Math.max(top,left));


                if(strX.charAt(i-1)==strY.charAt(j-1)){
                    tab[i][j]=topLeft+alignment.getScoreSettings().MATCH;
                }else if(max==left){
                    tab[i][j]=max+gap;
                }
                else if(max==top){
                    tab[i][j]=max+gap;
                }
                else if(max==topLeft){
                    tab[i][j]=max+alignment.getScoreSettings().MISMATCH;
                }
                else{}
            }
        }

        System.out.println("X est sur laxe des y="+alignment.getX_Sequence());
        System.out.println("Y est sur laxe des x="+alignment.getY_Sequence());
        System.out.println("Y est sur laxe des x="+alignment.getScoreMatrix().length);
        alignment.setScoreMatrix(tab);
        return alignment;
    }


    /***
     *     ______         ______                                __                    __
     *    |__    |       |   __ \.-----.----.-----.-----.-----.|  |_.----.--.--.----.|  |_
     *    |__    |__     |      <|  -__|  __|  _  |     |__ --||   _|   _|  |  |  __||   _|
     *    |______|__|    |___|__||_____|____|_____|__|__|_____||____|__| |_____|____||____|
     *
     *  NeedlemanWunsch-Rekonstruktion der Alignment-Strings
     *      Benötigt:
     *          - Sequenz X,
     *          - Sequenz Y,
     *          - Scores-Instanz
     *          - ScoreMatrix (initialisiert und berechnet)
     *      Nachbedingungen:
     *          - Alignment enthält rekonstruierten X_Alignment-String
     *          - Alignment enthält rekonstruierten Y_Alignment-String
     *          - Alignment enthält rekonstruierten Operations-String
     *              - MATCH     -->  '*'
     *              - MISMATCH  -->  '|'
     *              - GAP       -->  '_'
     */
    private static Alignment reconstruct(Alignment alignment) {
        initialize(alignment);
        /**
         * variable definition
         */
        int[][] tab=alignment.getScoreMatrix();
        String strX=alignment.getX_Sequence();
        String strY=alignment.getY_Alignment();
        int line=tab.length-1;
        int column=tab[0].length-1;
        String strSeqX="";
        String strSeqY="";
        String operation="";
        int top=0,left=0,topLeft=0,max=0;
        /**
         * End of variable definition
         */
        while(column>=0 && line>=0  ){
            if(line==0){
                if(column==0)
                    break;
                strSeqX+="_";
                strSeqY+=strY.charAt(column-1);
                operation+="_";
                column=column-1;

            }
            if(column==0){
                if(line==0)
                    break;
                strSeqX+=strX.charAt(line-1);
                strSeqY+="_";
                operation+="_";
                line=line-1;
            }
            if(line>0 && column>0) {
                left = tab[line][column - 1];
                top = tab[line - 1][column];
                topLeft = tab[line - 1][column - 1];
                max = Math.max(topLeft, Math.max(top, left));

                /**
                 * if Match
                 */

                if (strX.charAt(line - 1) == strY.charAt(column - 1)) {
                    strSeqX += strX.charAt(line - 1);
                    strSeqY += strY.charAt(column - 1);
                    operation+="*";
                    line = line - 1;
                    column = column - 1;
                } else {
                    /**
                     * if no match
                     */
                    if (max == topLeft) {
                        strSeqX += strX.charAt(line - 1);
                        strSeqY += strY.charAt(column - 1);
                        operation+="|";
                        line = line - 1;
                        column = column - 1;
                    } else {
                        if (max == left) {
                            strSeqX += "_";
                            strSeqY += strY.charAt(column - 1);
                            operation+="_";
                            column = column - 1;
                        }
                        if (max == top) {
                            strSeqX += strX.charAt(line - 1);
                            strSeqY += "_";
                            operation+="_";
                            line = line - 1;
                        }
                    }
                }
            }

        }
        /**
         * String reverse
         */
        StringBuilder sX=new StringBuilder(strSeqX);
        StringBuilder sY=new StringBuilder(strSeqY);
        StringBuilder opReverse=new StringBuilder(operation);
        sX.reverse();
        sY.reverse();
        opReverse.reverse();
        alignment.setY_Alignment(sY.toString());
        alignment.setX_Alignment(sX.toString());
        alignment.setOperationsString(opReverse.toString());

        return alignment;
    }

}