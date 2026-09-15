package Utility;

public class Stack <T>{

	protected int height = 0;
	protected Node <T> top;
	
	public void push(T data) {
		height++;
		if (top == null) {
			top = new Node <T> (data, null);
		}
		else {
			top = new Node <T> (data, top);
		}
	}
	
	public T pop() {
	    if (top == null) throw new IllegalStateException("Stack is empty");
	    height--;
	    Node <T> poppedNode = top;
	    top = poppedNode.getNext();
	    return poppedNode.getData();
	}
	
	public boolean isEmpty() {
		return top == null;
	}
	
	public void clear() {
		top = null;
	}
	
}
