package Events;

import Panels.Layer;

public record EVENT_RequestSetLayerVisible(Layer layer, boolean visible) implements SuperEvent {

}
