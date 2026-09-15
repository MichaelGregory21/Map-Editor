/**
 * For each point in the selected layer, if it contains a nonempty tile, overwrites the same point in the above layer with this tile. If this tile is empty, does nothing at this point. Then deletes the selected layer 
 */
package Commands;

import Panels.Canvas;
import Panels.Layer;

public class COMMAND_MergeLayer implements Command {
	
	private Canvas canvas;
	private int lowerIndex;
	private int upperIndex;
	private Layer lowerLayerCopy;
	private Layer upperLayerCopy;
	
	public COMMAND_MergeLayer(Canvas canvas, int lowerIndex, int upperIndex) {
		this.canvas = canvas;
		this.lowerIndex = lowerIndex;
		this.upperIndex = upperIndex;
	}
	
	@Override
	public void execute() {
		if (lowerIndex >= 0 && lowerIndex < canvas.getNumLayers() && upperIndex > 0 && upperIndex < canvas.getNumLayers() && upperIndex > lowerIndex) {
			lowerLayerCopy = canvas.getLayer(lowerIndex).clone();
			upperLayerCopy = canvas.getLayer(upperIndex).clone();
			canvas.mergeLayers(lowerIndex, upperIndex);
					
		}     
		
	}

	@Override
	public void undo() {
		if (lowerIndex >= 0 && lowerIndex < canvas.getNumLayers() + 1 && upperIndex > 0 && upperIndex < canvas.getNumLayers() + 1 && upperIndex > lowerIndex) {
			canvas.deleteLayer(lowerIndex);
			canvas.insertLayer(lowerLayerCopy, lowerIndex);
			canvas.insertLayer(upperLayerCopy, upperIndex);
			canvas.setCurrentLayer(upperIndex);
		}
    }

	@Override
	public boolean save() {
		return true;
	}

	

}
