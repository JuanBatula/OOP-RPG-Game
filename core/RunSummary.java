package core;
/**
 * RunSummary — tracks and displays end-of-run statistics.
 *
 * printReport() is called by GameRunner at the end of every run.
 * All output routed through Fmt so colours stay consistent.
 */
public class RunSummary {

    private int enemiesKilled;
    private int totalDamageDealt;
    private int stagesCleared;

    public RunSummary() {
        this.enemiesKilled    = 0;
        this.totalDamageDealt = 0;
        this.stagesCleared    = 0;
    }

    // -------------------------------------------------------------------------
    // Record methods
    // -------------------------------------------------------------------------

    /** Call once each time the player kills an enemy. */
    public void recordKill() { enemiesKilled++; }

    /** Call after every player attack with the damage dealt. */
    public void recordDamage(int amount) {
        if (amount > 0) totalDamageDealt += amount;
    }

    /** Call once each time the player clears a stage. */
    public void recordStageCleared() { stagesCleared++; }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    public int getEnemiesKilled()    { return enemiesKilled;    }
    public int getTotalDamageDealt() { return totalDamageDealt; }
    public int getStagesCleared()    { return stagesCleared;    }

    // -------------------------------------------------------------------------
    // printReport  — formatted with ANSI colours via Fmt
    // -------------------------------------------------------------------------

    /**
     * Prints a gold-accented end-of-run summary box.
     *
     *   ╔════════════════════════════════════════════════╗
     *         RUN SUMMARY
     *   ╠════════════════════════════════════════════════╣
     *     Enemies Defeated     ·················    2
     *     Total Damage Dealt   ·················  148
     *     Stages Cleared       ·················    2
     *   ╚════════════════════════════════════════════════╝
     */
    public void printReport() {
        int w = 48; // inner width (between ║ characters)

        String topBar  = "╔" + "═".repeat(w) + "╗";
        String midBar  = "╠" + "═".repeat(w) + "╣";
        String botBar  = "╚" + "═".repeat(w) + "╝";

        String titleRow  = buildRow("  RUN SUMMARY", "",         w, false);
        String row1      = buildRow("  Enemies Defeated",    String.valueOf(enemiesKilled),    w, true);
        String row2      = buildRow("  Total Damage Dealt",  String.valueOf(totalDamageDealt), w, true);
        String row3      = buildRow("  Stages Cleared",      String.valueOf(stagesCleared),    w, true);

        System.out.println();

        String boxColor = Fmt.BR_YELLOW;
        String valColor = Fmt.WHITE;
        String rst      = Fmt.RESET;

        if (Fmt.COLOR) {
            System.out.println(Fmt.INDENT + boxColor + topBar + rst);
            System.out.println(Fmt.INDENT + boxColor + "║" + rst
                + Fmt.B_BR_YELLOW + titleRow + rst + boxColor + "║" + rst);
            System.out.println(Fmt.INDENT + boxColor + midBar + rst);
            printDataRow("  Enemies Defeated",    enemiesKilled,    w, boxColor, valColor);
            printDataRow("  Total Damage Dealt",  totalDamageDealt, w, boxColor, valColor);
            printDataRow("  Stages Cleared",      stagesCleared,    w, boxColor, valColor);
            System.out.println(Fmt.INDENT + boxColor + botBar + rst);
        } else {
            System.out.println(Fmt.INDENT + topBar);
            System.out.println(Fmt.INDENT + "║" + titleRow + "║");
            System.out.println(Fmt.INDENT + midBar);
            System.out.println(Fmt.INDENT + "║" + row1 + "║");
            System.out.println(Fmt.INDENT + "║" + row2 + "║");
            System.out.println(Fmt.INDENT + "║" + row3 + "║");
            System.out.println(Fmt.INDENT + botBar);
        }
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a padded row string of exactly {@code width} chars (no border chars).
     * If {@code rightAlign} is true, the value is right-aligned with dot leaders.
     */
    private static String buildRow(String label, String value, int width, boolean rightAlign) {
        if (!rightAlign) {
            // Title row: left-align label, pad right
            int spaces = width - label.length();
            return label + " ".repeat(Math.max(0, spaces));
        }
        // Data row: label ... dots ... value
        int dotsAndValue = width - label.length();
        String dots = " " + ".".repeat(Math.max(0, dotsAndValue - value.length() - 2)) + " ";
        String row  = label + dots + value;
        int pad     = width - row.length();
        return row + " ".repeat(Math.max(0, pad));
    }

    /** Prints one coloured data row between the box borders. */
    private static void printDataRow(String label, int value, int width,
                                     String boxColor, String valColor) {
        String valStr    = String.valueOf(value);
        int usable       = width - label.length() - valStr.length() - 2; // 2 spaces margin
        String dots      = Fmt.DIM + " " + ".".repeat(Math.max(1, usable)) + " " + Fmt.RESET;
        String colLabel  = Fmt.c(Fmt.WHITE, label);
        String colVal    = Fmt.c(valColor, valStr);

        System.out.print(Fmt.INDENT + boxColor + "║" + Fmt.RESET);
        System.out.print(colLabel + dots + colVal);

        // Trailing space before closing border (calculate visible length)
        int visibleLen = label.length() + 1 + Math.max(1, usable) + 1 + valStr.length();
        int trailing   = Math.max(0, width - visibleLen);
        System.out.print(" ".repeat(trailing));
        System.out.println(boxColor + "║" + Fmt.RESET);
    }
}