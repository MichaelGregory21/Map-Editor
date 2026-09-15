/**
 * This command only saves a previous location for the purposes of undo. After moving the selection mask, execute this command by passing in the previous location
 */
package Commands;

import UI.SelectionMask;

public class COMMAND_MoveSelectionMask implements Command {
    private final SelectionMask mask;
    private final int oldShiftX, oldShiftY;

    public COMMAND_MoveSelectionMask(SelectionMask mask, int oldShiftX, int oldShiftY) {
        this.mask = mask;
        this.oldShiftX = oldShiftX;
        this.oldShiftY = oldShiftY;
    }

    @Override
    public void execute() {}

    @Override
    public void undo() {
    	mask.setShift(oldShiftX, oldShiftY);
        
    }

	@Override
	public boolean save() {
		return true;
	}
}
