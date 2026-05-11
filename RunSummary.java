/**
 * RunSummary — tracks and displays end-of-run statistics for one complete playthrough.
 *
 * Lifecycle:
 *   1. GameRunner creates one RunSummary at the start of the run.
 *   2. GameRunner passes it to each Battle (or calls record methods directly).
 *   3. Battle calls recordKill() when an enemy dies, recordDamage() after each
 *      player attack, and GameRunner calls recordStageCleared() between encounters.
 *   4. GameRunner calls printReport() when the run ends (player wins or dies).
 *
 * Integration pattern (minimal — does not modify existing logic):
 *
 *   // In GameRunner.main():
 *   RunSummary summary = new RunSummary();
 *   ...
 *   runBattle(player, goblin, summary);   // pass summary into helper
 *   summary.recordStageCleared();
 *   ...
 *   summary.printReport();
 *
 *   // In GameRunner.runBattle():
 *   private static void runBattle(Player player, Enemy enemy, RunSummary summary) {
 *       Battle battle = new Battle();
 *       battle.initializeBattle(player, enemy, summary);   // Battle stores ref
 *       ...
 *       // After loop, if player won:
 *       if ("player".equals(winner)) { summary.recordKill(); }
 *   }
 *
 *   // In Battle.performTurn(), after player damage is calculated:
 *   if (summary != null) { summary.recordDamage(playerDamage); }
 *
 * NOTE: Battle currently has no RunSummary field.  The integration only requires
 * adding one field + one setter to Battle, and two record() calls — no logic change.
 * FLAG for team: if Battle should not be modified at all, GameRunner can call
 * recordDamage() after runBattle() returns by comparing HP before/after — less
 * accurate but zero Battle changes needed.
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
    // Record methods — called during gameplay
    // -------------------------------------------------------------------------

    /** Call once each time the player kills an enemy. */
    public void recordKill() {
        enemiesKilled++;
    }

    /**
     * Call after every successful player attack with the damage value returned
     * by Battle.calculateTotalDamage() (or the actual damage dealt to the enemy).
     *
     * @param amount damage the player dealt this hit
     */
    public void recordDamage(int amount) {
        if (amount > 0) {
            totalDamageDealt += amount;
        }
    }

    /** Call once each time the player clears a stage / moves to the next encounter. */
    public void recordStageCleared() {
        stagesCleared++;
    }

    // -------------------------------------------------------------------------
    // Getters — for testing or external display
    // -------------------------------------------------------------------------

    public int getEnemiesKilled()    { return enemiesKilled;    }
    public int getTotalDamageDealt() { return totalDamageDealt; }
    public int getStagesCleared()    { return stagesCleared;    }

    // -------------------------------------------------------------------------
    // printReport()
    // -------------------------------------------------------------------------

    /**
     * Prints a formatted end-of-run summary to System.out.
     * Call once when the run ends (player death or final victory).
     */
    public void printReport() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║           RUN SUMMARY REPORT          ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf( "║  %-22s %12s  ║%n", "Enemies Defeated:",   pad(enemiesKilled));
        System.out.printf( "║  %-22s %12s  ║%n", "Total Damage Dealt:", pad(totalDamageDealt));
        System.out.printf( "║  %-22s %12s  ║%n", "Stages Cleared:",     pad(stagesCleared));
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /** Right-aligns an integer value for the report table. */
    private String pad(int value) {
        return String.valueOf(value);
    }
}