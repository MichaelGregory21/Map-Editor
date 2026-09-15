package Events;

import Panels.Layer;

public record EVENT_NewLayerCreated(Layer layer, int index) implements SuperEvent {

}
