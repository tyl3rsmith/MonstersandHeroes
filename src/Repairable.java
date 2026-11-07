/**
 * Used to define the type of Objects which are Repairable
 * Armor and Weapons are repairable
 * Repairs have a cost: purchase price / 2
 */


public interface Repairable {
    boolean needsRepair();
    double getRepairCost();
    void repair();
}