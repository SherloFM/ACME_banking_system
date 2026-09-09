public interface iLockable {
    boolean checkLockedStatus();
    void incrementFailedAttempts();
    void resetFailedAttempts();

    boolean isCurrentlyLocked();
}
