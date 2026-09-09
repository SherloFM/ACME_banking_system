public interface iLimitEnforcer {
//    boolean checkLimit(Enum<transactionType>transactionTypeEnum, double requestedAmount);
//    void recordUsage(Enum<transactionType> transactionTypeEnum, double transactionAmount);
    void resetDailyLimitsIfNewDay();

}
