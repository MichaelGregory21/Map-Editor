/**
 * An event bus is an objects that should be passed to any components that should communicate with each other. 
 * It stores a collection of lightweight classes which can be assigned a collection of subscribers and actions 
 * to undergo in the event that the class is published by any component. 
 *
 * PUBLISH EXAMPLE: bus.publish(new EVENT_BrushSizeSet(1)) / Tell everyone that the brush size should be set to 1
 * SUBSCRIBE EXAMPLE: bus.subscribe(EVENT_BrushSizeSet.class, e -> setBrushSize(e.size()))
 */
package Utility;

import Events.SuperEvent;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EventBus {

    private Queue<SuperEvent> eventQueue = new Queue<>();
    private HashMap<Class<? extends SuperEvent>, Set<Subscriber<? extends SuperEvent>>> subscribers = new HashMap<>();
    private Timer processor;

    private static final int PROCESS_INTERVAL = 5;

    public EventBus() {
        setupTimer();
    }

    /**
     * When a class wants to subscribe to an event, this interface is implemented as a lambda
     * and the function is called when the event occurs
     *
     * @param <E> The type of event you want to subscribe to. Enforces type safety for the subscriber
     */
    @FunctionalInterface
    public interface Subscriber<E extends SuperEvent> {
        void onEvent(E event);
    }

    /**
     * Set up the timer that periodically processes the next event in the queue
     */
    private void setupTimer() {
        processor = new Timer(PROCESS_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processNextEvent();
            }
        });
        processor.start();
    }

    /**
     * Publish the given event. If the same event has not been processed, do nothing.
     * If the publication is successful, all subscribers to the given event will react when it is processed
     *
     * @param event The event you want to publish
     */
    public void publish(SuperEvent event) {
        SuperEvent tail = eventQueue.peekTail();
        if (tail != null && tail.getClass() == event.getClass()) {
            // collapse/overwrite the tail event of the same type
            eventQueue.replaceTail(event);
        } else {
            eventQueue.enqueue(event);
        }
    }

    /**
     * Subscribe to the given event. Whenever the event is publish to this bus,
     * the function of the given subscriber is executed
     *
     * @param eventType  The type of event you want to subscribe to
     * @param subscriber The function to be executed whenever the event is processed
     */
    public <E extends SuperEvent> void subscribe(Class<E> eventType, Subscriber<E> subscriber) {
        Set<Subscriber<? extends SuperEvent>> subscriberSet = subscribers.get(eventType);
        if (subscriberSet == null) {
            subscriberSet = new HashSet<>();
        }
        subscriberSet.add(subscriber);
        subscribers.put(eventType, subscriberSet);
    }

    /**
     * Dequeue and process the next event in the queue. All subscribers of the event execute their associated function
     */
    private void processNextEvent() {
        if (eventQueue.isEmpty()) return;

        SuperEvent event = eventQueue.dequeue();
        Set<Subscriber<?>> subscriberSet = subscribers.get(event.getClass());

        if (subscriberSet != null) {
            for (Subscriber<?> subscriber : subscriberSet) {
                @SuppressWarnings("unchecked")
                Subscriber<SuperEvent> sub = (Subscriber<SuperEvent>) subscriber;
                sub.onEvent(event);
            }
        }
    }

    /**
     * Delete everything in the queue
     */
    public void clear() {
        eventQueue.clear();
    }
}
