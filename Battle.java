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
    
        // Randomly decide turn order each round
        boolean playerGoesFirst = Math.random() < 0.5;
    
        if (playerGoesFirst) {
            resolvePlayerAttack();
            if (enemy.isAlive()) resolveEnemyAttack();
        } else {
            System.out.println("[Turn order: " + enemy.getName() + " moves first!]");
            resolveEnemyAttack();
            if (player.isAlive()) resolvePlayerAttack();
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

    private void resolvePlayerAttack() {
        int playerDamage = calculateTotalDamage(player, enemy);
        if (runSummary != null) runSummary.recordDamage(playerDamage);
        enemy.takeDamage(playerDamage);
        if (!enemy.isAlive() && runSummary != null) runSummary.recordKill();
    }
 
    private void resolveEnemyAttack() {
        int enemyDamage = calculateTotalDamage(enemy, player);
        player.takeDamage(enemyDamage);
    }
}