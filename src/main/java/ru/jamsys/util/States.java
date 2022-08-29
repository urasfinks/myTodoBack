package ru.jamsys.util;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class States {

    public static class Item {
        String key;
        AtomicInteger count = new AtomicInteger(0);
        String data = "";

        public Item(String key) {
            this.key = key;
            this.data = Util.timestampToDate(System.currentTimeMillis() / 1000, "dd.MM.yyyy HH:mm");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return key.equals(item.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        public int getCount() {
            return count.getAndIncrement();
        }

        @Override
        public String toString() {
            return data + "; " + key + ": " + count;
        }
    }

    public static List<Item> list = new CopyOnWriteArrayList<>();
    public static int maxCount = 5;

    public static String show() {
        return "Size: " + list.size() + " \n" + list.stream()
                .map(Item::toString)
                .collect(Collectors.joining("\n"));
    }

    public static int get(String key) {
        if (key == null || "".equals(key)) {
            return 0;
        }
        Item newItem = new Item(key);
        if (!list.contains(newItem)) {
            list.add(newItem);
            if (list.size() >= maxCount) {
                list.remove(0);
            }
        } else {
            for (Item item : list) {
                if (item.equals(newItem)) {
                    item.data = Util.timestampToDate(System.currentTimeMillis() / 1000, "dd.MM.yyyy HH:mm");
                    return item.getCount();
                }
            }
        }
        return 0;
    }

}
