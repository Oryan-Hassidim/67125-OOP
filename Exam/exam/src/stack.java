class Stack {
    private Node head;

    static class Node {
        private int value;
        private Node next;
    }

    void add(int val) {
        class A {}
        Node newNode = new Node();
        newNode.value = val;
        newNode.next = head;
        // if (head == null) {
        // head = newNode;
        // return;
        // }
        head = newNode;
    }

    public class Iterator implements java.util.Iterator {
        private Node current;

        Iterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return current != null && current.next != null;
        }

        @Override
        public Object next() {
            int value = current.value;
            current = current.next;
            return value;
        }
    }
}