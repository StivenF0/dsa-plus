package com.dsaplus.util;

public class HashMap<K, V> {

    public static class Entry<K, V> {
        private final K key;
        private V value;
        Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    private final Entry<K, V>[] buckets;
    private int count;

    @SuppressWarnings("unchecked")
    public HashMap(int capacity) {
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.count = 0;
    }

    public HashMap() {
        this(127);
    }

    private int hash(K key) {
        int h = 7;
        String s = String.valueOf(key);
        for (int i = 0; i < s.length(); i++) {
            h = h * 31 + s.charAt(i);
        }
        return Math.abs(h) % buckets.length;
    }

    public void put(K key, V value) {
        int index = hash(key);
        Entry<K, V> current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        Entry<K, V> entry = new Entry<>(key, value);
        entry.next = buckets[index];
        buckets[index] = entry;
        count++;
    }

    public V get(K key) {
        int index = hash(key);
        Entry<K, V> current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    @SuppressWarnings("unchecked")
    public Entry<K, V>[] entrySet() {
        Entry<K, V>[] entries = new Entry[count];
        int i = 0;
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> current = bucket;
            while (current != null) {
                entries[i++] = current;
                current = current.next;
            }
        }
        return entries;
    }
}
