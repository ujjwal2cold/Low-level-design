class Node<K, V> {
   private final K key;
   private V value;
   Node<K, V> next;
   Node<K, V> prev;

   Node(K key, V value) {
      this.key = key;
      this.value = value;
      this.next = null;
      this.prev = null;
   }

   public void setNext(Node<K, V> n) {
      this.next = n;
   }

   public void setPrev(Node<K, V> n) {
      this.prev = n;
   }

   public V getValue() {
      return this.value;
   }

   public K getKey() {
      return this.key;
   }

   public void setValue(V value) {
      this.value = value;
   }

   public void moveToFront(Node<K, V> head) {
      Node<K, V> nextToHead = head.next;
      head.next = this;
      nextToHead.prev = this;
      this.prev = head;
      this.next = nextToHead;
   }

   public void pop() {
      Node<K, V> prevNode = this.prev;
      Node<K, V> nextNode = this.next;
      prevNode.next = nextNode;
      nextNode.prev = prevNode;
   }

   public K evictPrev() {
      Node<K, V> toEvict = this.prev;
      Node<K, V> prevToEvict = toEvict.prev;
      prevToEvict.next = this;
      this.prev = prevToEvict;
      return toEvict.getKey();
   }

   @Override
   public String toString() {
      return "Node{" +
              "key=" + key +
              ", value=" + value +
              '}';
   }
}