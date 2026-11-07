public enum TransactionResult {
    SUCCESS("Transaction complete!"),
    NOT_IN_STOCK("The market doesn't have that item."),
    NOT_OWNED("You don't own that item."),
    LEVEL_TOO_LOW("You aren't a high enough level to buy that."),
    NOT_ENOUGH_GOLD("You don't have enough gold."),
    NOT_REPAIRABLE("That item can't be repaired."),
    ALREADY_FULL("That item doesn't need repairs.");

    private final String message;

    TransactionResult(String message) { this.message = message; }

    public String getMessage() { return message; }
}