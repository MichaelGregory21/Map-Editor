package Events;

public record EVENT_MousePosition(int tileX, int tileY, int trueX, int trueY) implements SuperEvent {}
