package com.drawwdev.raffle.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class ListUtil {

    private static Integer getChance(Integer size) {
        return ThreadLocalRandom.current().nextInt(size);
    }

    public static <T> T getList(List<T> list) {
        if (list.size() == 0) {
            return null;
        }
        int rand = getChance(list.size());
        if (list instanceof List) {
            return (list).get(rand);
        } else {
            Iterator<T> itr = list.iterator();
            IntStream.range(1, list.size()).forEach(i -> itr.next());
            return itr.next();
        }
    }

    public static <T> T getCollection(Collection<T> collection) {
        if (collection.size() == 0) {
            return null;
        }
        int rand = getChance(collection.size());
        if (collection instanceof List) {
            return ((List<T>) collection).get(rand);
        } else {
            Iterator<T> itr = collection.iterator();
            IntStream.range(1, collection.size()).forEach(i -> itr.next());
            return itr.next();
        }
    }

}
