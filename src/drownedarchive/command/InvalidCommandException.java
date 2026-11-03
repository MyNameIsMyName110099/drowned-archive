package drownedarchive.command;

/*
 * Thrown when the player types something the parser can't turn into a Command.
 * The message is written for the player, so the game can print it as-is and keep going.
 */
public class InvalidCommandException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidCommandException(String messageForPlayer) {
        super(messageForPlayer);
    }
}
