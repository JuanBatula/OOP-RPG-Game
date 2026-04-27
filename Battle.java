public class Battle {
    private Player player;
    private Enemy enemy;
    private boolean battleActive;

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
        enemy.takeDamage(playerDamage);

        if (enemy.isAlive()) {
            int enemyDamage = calculateTotalDamage(enemy, player);
            player.takeDamage(enemyDamage);
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