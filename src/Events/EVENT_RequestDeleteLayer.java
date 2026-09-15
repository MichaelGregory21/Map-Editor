package Events;

import Panels.Layer;

public record EVENT_RequestDeleteLayer(Layer layer) implements SuperEvent {

}
