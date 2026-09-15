package Utility;

class Node <T> {
	T data;
	Node <T> next;

	public Node(T data, Node <T> next) {
		this.data = data;
		this.next = next;
	}

	public T getData() {
		return data;
	}

	public Node <T> getNext() {
		return next;
	}
}
