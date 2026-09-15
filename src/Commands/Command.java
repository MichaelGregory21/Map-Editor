/**
 * A Command is a decoupled method call which comes equipped with an undo method. All mutating requests given by the user should be encapsulated in a command.
 */

package Commands;

public interface Command {

	/**
	 * This is the primary function of this command
	 */
	public void execute();

	/**
	 * Reverse whatever effects were implemented
	 */
	public void undo();

	/**
	 * @return true if you want this command to be saved to the stack for potential
	 *         undoing later. False, otherwise
	 */
	public boolean save();

}
