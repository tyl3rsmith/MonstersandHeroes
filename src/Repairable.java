public interface Repairable {
    boolean needsRepair();
    double getRepairCost();
    void repair();
}