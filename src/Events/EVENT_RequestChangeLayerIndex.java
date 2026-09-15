package Events;

public record EVENT_RequestChangeLayerIndex(int oldIndex, int newIndex) implements SuperEvent {

}
