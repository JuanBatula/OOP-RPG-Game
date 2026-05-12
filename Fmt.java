/**
 * Fmt — central ANSI colour/formatting constants and terminal-UI helpers.
 *
 * Colour assignments:
 *   Cyan        — narration, scene descriptions, player name in prose
 *   Green       — player actions, damage dealt by player, healing, success
 *   Red         — enemy actions, damage taken, danger
 *   Yellow      — UI headers, menu titles, prompts
 *   Gold (Br.Yellow) — gold earned, rare items, level-up highlights
 *   White       — stats, menu options, neutral info
 *   Magenta     — status effects (buffs / debuffs)
 *   Bright Red  — low-HP critical warning, invalid-input errors
 *   Dim (grey)  — divider chrome, secondary decoration
 *
 * To disable colours: pass --no-color as the first CLI arg, or set the
 * environment variable NO_COLOR to any value (no-color.org convention).
 */
public final class Fmt {

    // -------------------------------------------------------------------------
    // Global colour toggle — set once by GameRunner.main()
    // -------------------------------------------------------------------------
    public static boolean COLOR = true;

    // -------------------------------------------------------------------------
    // Raw ANSI escape codes
    // -------------------------------------------------------------------------
    public static final String RESET     = "\u001B[0m";
    public static final String BOLD      = "\u001B[1m";

    public static final String RED       = "\u001B[31m";
    public static final String GREEN     = "\u001B[32m";
    public static final String YELLOW    = "\u001B[33m";
    public static final String MAGENTA   = "\u001B[35m";
    public static final String CYAN      = "\u001B[36m";
    public static final String WHITE     = "\u001B[37m";
    public static final String DIM       = "\u001B[90m";   // bright-black / dark grey

    public static final String BR_RED    = "\u001B[91m";
    public static final String BR_GREEN  = "\u001B[92m";
    public static final String BR_YELLOW = "\u001B[93m";   // gold
    public static final String BR_CYAN   = "\u001B[96m";
    public static final String BR_WHITE  = "\u001B[97m";

    // Bold + colour composites
    public static final String B_RED     = "\u001B[1;31m";
    public static final String B_GREEN   = "\u001B[1;32m";
    public static final String B_YELLOW  = "\u001B[1;33m";
    public static final String B_CYAN    = "\u001B[1;36m";
    public static final String B_MAGENTA = "\u001B[1;35m";
    public static final String B_WHITE   = "\u001B[1;37m";
    public static final String B_BR_RED  = "\u001B[1;91m";
    public static final String B_BR_GREEN= "\u001B[1;92m";
    public static final String B_BR_YELLOW="\u001B[1;93m";

    // HP bar characters
    public static final String FILL  = "█";
    public static final String EMPTY = "░";

    // Layout constants
    static final int   DIV_WIDTH = 50;
    static final String INDENT   = "  ";

    private Fmt() {}   // utility class — no instances

    // -------------------------------------------------------------------------
    // Core colouring primitive
    // -------------------------------------------------------------------------

    /** Wraps text in an ANSI code + RESET, or returns plain text if COLOR=false. */
    public static String c(String code, String text) {
        return COLOR ? code + text + RESET : text;
    }

    // -------------------------------------------------------------------------
    // Structural dividers
    // -------------------------------------------------------------------------

    /** Thin dim line — use between sub-sections within a screen. */
    public static void printDivider() {
        StringBuilder sb = new StringBuilder(INDENT);
        if (COLOR) sb.append(DIM);
        for (int i = 0; i < DIV_WIDTH; i++) sb.append('─');
        if (COLOR) sb.append(RESET);
        System.out.println(sb);
    }

    /** Heavy double-rule — use at top/bottom of major screen boxes. */
    public static void printMajorDivider() {
        StringBuilder sb = new StringBuilder(INDENT);
        if (COLOR) sb.append(DIM);
        for (int i = 0; i < DIV_WIDTH; i++) sb.append('═');
        if (COLOR) sb.append(RESET);
        System.out.println(sb);
    }

    // -------------------------------------------------------------------------
    // Section heading
    // -------------------------------------------------------------------------

    /**
     * Prints a bold-yellow heading centred between two major dividers.
     *
     * ══════════════════════════════════════════════════
     *                    MAIN MENU
     * ══════════════════════════════════════════════════
     */
    public static void printHeading(String title) {
        System.out.println();
        printMajorDivider();
        int pad = Math.max(0, (DIV_WIDTH - title.length()) / 2);
        String centred = INDENT + " ".repeat(pad) + title;
        System.out.println(COLOR ? B_YELLOW + centred + RESET : centred);
        printMajorDivider();
        System.out.println();
    }

    /**
     * Prints a combat-turn sub-heading.
     *
     *   ── TURN 3 ────────────────────────────────────────
     */
    public static void printTurnHeading(int turn) {
        String label = " TURN " + turn + " ";
        int trailing = DIV_WIDTH - label.length() - 2;
        StringBuilder sb = new StringBuilder(INDENT);
        if (COLOR) sb.append(YELLOW);
        sb.append("──").append(label);
        for (int i = 0; i < Math.max(0, trailing); i++) sb.append('─');
        if (COLOR) sb.append(RESET);
        System.out.println();
        System.out.println(sb);
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // HP bar
    // -------------------------------------------------------------------------

    /**
     * Prints one HP bar line:
     *   Hero          ████████░░  80/100
     *
     * Bar is 10 chars wide; colour shifts green → yellow → red by %.
     * When HP ≤ 25%, the numeric value also pulses in bright-red.
     */
    public static void printHpBar(String label, int current, int max) {
        int safeMax = Math.max(1, max);
        int safeCur = Math.max(0, Math.min(current, safeMax));
        double pct  = (double) safeCur / safeMax;
        int filled  = (int) Math.round(pct * 10);
        int empty   = 10 - filled;

        String barColor = "";
        if (COLOR) {
            if      (pct > 0.50) barColor = B_GREEN;
            else if (pct > 0.25) barColor = B_YELLOW;
            else                 barColor = B_BR_RED;
        }

        StringBuilder bar = new StringBuilder();
        if (COLOR) bar.append(barColor);
        for (int i = 0; i < filled; i++) bar.append(FILL);
        if (COLOR) bar.append(DIM);
        for (int i = 0; i < empty;  i++) bar.append(EMPTY);
        if (COLOR) bar.append(RESET);

        String numeric = (COLOR && pct <= 0.25)
                ? B_BR_RED + safeCur + "/" + safeMax + RESET
                : (COLOR ? WHITE + safeCur + "/" + safeMax + RESET : safeCur + "/" + safeMax);

        String paddedLabel = String.format("%-13s", label);
        if (COLOR) paddedLabel = WHITE + paddedLabel + RESET;

        System.out.println(INDENT + paddedLabel + bar + "  " + numeric);
    }

    // -------------------------------------------------------------------------
    // Named shorthand print methods — use these everywhere in GameRunner
    // -------------------------------------------------------------------------

    /** Cyan — story narration, scene descriptions. */
    public static void narrate(String text) {
        System.out.println(INDENT + c(CYAN, text));
    }

    /** Green — player success: damage dealt, healing, good news. */
    public static void success(String text) {
        System.out.println(INDENT + c(GREEN, text));
    }

    /** Red — enemy action, damage taken, threat. */
    public static void danger(String text) {
        System.out.println(INDENT + c(RED, text));
    }

    /** Bright Red — critical warnings, invalid input. */
    public static void warn(String text) {
        System.out.println(INDENT + c(BR_RED, text));
    }

    /** Bright Yellow — gold, rare drops, rewards. */
    public static void gold(String text) {
        System.out.println(INDENT + c(BR_YELLOW, text));
    }

    /** White — neutral stats, item info, menu body text. */
    public static void info(String text) {
        System.out.println(INDENT + c(WHITE, text));
    }

    /** Magenta — status effect announcements and ticks. */
    public static void status(String text) {
        System.out.println(INDENT + c(MAGENTA, text));
    }

    /** Yellow — UI chrome, prompts, sub-headings. */
    public static void ui(String text) {
        System.out.println(INDENT + c(YELLOW, text));
    }

    /** Dim — secondary chrome, muted supplementary text. */
    public static void dim(String text) {
        System.out.println(INDENT + c(DIM, text));
    }

    /** Blank line — avoids bare System.out.println() calls. */
    public static void blank() {
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Compact numbered menu
    // -------------------------------------------------------------------------

    /**
     * Prints a heading and a numbered list of options.
     * Each option: "[N] label"
     */
    public static void printMenu(String title, String[] options) {
        printHeading(title);
        for (int i = 0; i < options.length; i++) {
            String num   = c(B_YELLOW, "[" + (i + 1) + "]");
            String label = c(WHITE,    " " + options[i]);
            System.out.println(INDENT + num + label);
        }
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Special banners
    // -------------------------------------------------------------------------

    /** Green victory box. */
    public static void printVictoryBanner(String enemyName) {
        box(B_BR_GREEN, "  ✦  VICTORY!  ✦", "  " + enemyName + " has been defeated!");
    }

    /** Red defeat box. */
    public static void printDefeatBanner() {
        box(B_BR_RED, "  ✗  GAME OVER  ✗", "  You have fallen in battle...");
    }

    /** Gold level-up box. */
    public static void printLevelUpBanner(int oldLvl, int newLvl) {
        box(B_BR_YELLOW, "  ★  LEVEL UP!  ★", "  Level " + oldLvl + "  →  Level " + newLvl);
    }

    /** Magenta ability-unlocked box. */
    public static void printAbilityUnlocked(String name) {
        box(B_MAGENTA, "  ✦  NEW ABILITY UNLOCKED  ✦", "  " + name);
    }

    // -------------------------------------------------------------------------
    // Private box builder
    // -------------------------------------------------------------------------

    private static void box(String code, String line1, String line2) {
        int w   = DIV_WIDTH + 2;
        String t = "╔" + "═".repeat(w) + "╗";
        String b = "╚" + "═".repeat(w) + "╝";
        String r1 = "║" + pad(line1, w) + "║";
        String r2 = "║" + pad(line2, w) + "║";
        blank();
        if (COLOR) {
            System.out.println(INDENT + code + t  + RESET);
            System.out.println(INDENT + code + r1 + RESET);
            System.out.println(INDENT + code + r2 + RESET);
            System.out.println(INDENT + code + b  + RESET);
        } else {
            System.out.println(INDENT + t);
            System.out.println(INDENT + r1);
            System.out.println(INDENT + r2);
            System.out.println(INDENT + b);
        }
        blank();
    }

    /** Centre-pad a string to exactly {@code width} visible characters. */
    private static String pad(String text, int width) {
        // Strip ANSI sequences for length calculation
        int visible = text.replaceAll("\u001B\\[[;\\d]*m", "").length();
        int spaces  = Math.max(0, width - visible);
        int left    = spaces / 2;
        int right   = spaces - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }
}