package net.chickalo.bugsbugsandmorebugs.entity.snail;

/** Stable network IDs; duration in ticks. Zero is a continuous pose. */
public enum SnailPose {
    IDLE(0), CRAWL(0), RETRACT(10), SHELLED(0), EMERGE(25), CURIOUS(0), EATING(40), PEEK(40), SLEEP(80), SHAKE(20), SLIME_DEPOSIT(28), HAPPY(24);
    public final int ticks;
    SnailPose(int ticks) { this.ticks = ticks; }
    public boolean protectedInShell() { return this == RETRACT || this == SHELLED || this == PEEK || this == EMERGE; }
    public boolean locksMovement() { return protectedInShell() || ticks > 0; }
    public static SnailPose byId(int id) { return id >= 0 && id < values().length ? values()[id] : IDLE; }
}
