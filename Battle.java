public class Battle {
    private Player player;
    private Enemy enemy;
    private boolean battleActive;

    private RunSummary runSummary;
    
    public void setRunSummary(RunSummary summary) {
        this.runSummary = summary;
    }

    public void initializeBattle(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.battleActive = true;
    }

    public int calculateTotalDamage(Character attacker, Character defender) {
        int damage = attacker.attackPower - (defender instanceof Player ? 0 : 0);
        return Math.max(1, damage);
    }

      public void performTurn() {
        if (!battleActive) return;

        int playerDamage = calculateTotalDamage(player, enemy);
        // Record player damage in RunSummary
        if (runSummary != null) runSummary.recordDamage(playerDamage);
        enemy.takeDamage(playerDamage);

        if (enemy.isAlive()) {
            int enemyDamage = calculateTotalDamage(enemy, player);
            player.takeDamage(enemyDamage);
        }

        // Record kill if enemy is defeated
        if (!enemy.isAlive()) {
            if (runSummary != null) runSummary.recordKill();
        }

        if (checkBattleOver() != null) {
            battleActive = false;
        }
    }


    public String checkBattleOver() {
        if (!enemy.isAlive()) return "player";
        if (!player.isAlive()) return "enemy";
        return null;
    }
}