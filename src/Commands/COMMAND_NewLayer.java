/**
 * Creates a new, completely blank layer
 */
package Commands;

import Panels.Canvas;
import Panels.Layer;

public class COMMAND_NewLayer implements Command {

	Canvas canvas;
	Layer layer;

	public COMMAND_NewLayer(Canvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void execute() {
		layer = new Layer(canvas.getNumCols(), canvas.getNumRows(), canvas.getNewLayerName(), Canvas.TILE_SIZE);
		canvas.newLayer(layer);

	}

	@Override
	public void undo() {
		int index = canvas.getIndexOfLayer(layer);
		if (index >= 0 && index < canvas.getNumLayers()) {
			canvas.deleteLayer(layer);
		}
		if (canvas.getNumLayers() == 0) {
			canvas.setCurrentLayer(-1);
		}
	}

	@Override
	public boolean save() {
		return true;
	}

}
