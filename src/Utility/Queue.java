package Utility;

public class Queue<T> {

	protected int size = 0;
	protected Node<T> tail;
	protected Node<T> head;

	public void enqueue(T data) {
		Node<T> node = new Node<>(data, null);
		if (tail == null) { 
			head = tail = node;
		} else {
			tail.next = node;
			tail = node; 
		}
		size++;
	}

	public T dequeue() {
		if (head == null)
			throw new IllegalStateException("Queue is empty");
		T value = head.data;
		head = head.next;
		if (head == null) {
			tail = null;
		}
		size--;
		return value;
	}

	public boolean isEmpty() {
		return tail == null;
	}

	public void clear() {
		tail = null;
	}
	
	public T peekTail() {
        return (tail == null) ? null : tail.data;
    }

    public void replaceTail(T data) {
        if (tail != null) tail.data = data;
    }

}
