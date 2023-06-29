package com.inmaytide.orbit.uaa;

import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author inmaytide
 * @since 2023/6/27
 */
public class ArrayListAddPerformanceTester {

    public static void main(String... args) {
        List<String> list = new ArrayList<>();
        StopWatch stopWatch = StopWatch.createStarted();
        for (int i = 0; i < 100000000; i++) {
            list.add(String.valueOf(i));
        }
        Collections.reverse(list);
        stopWatch.stop();
        System.out.printf("add(e) 耗时 %d 毫秒 \r\n", stopWatch.getTime(TimeUnit.MILLISECONDS));


        list = new LinkedList<>();
        stopWatch = StopWatch.createStarted();
        for (int i = 0; i < 100000000; i++) {
            list.add(0, String.valueOf(i));
        }
        stopWatch.stop();
        System.out.printf("add(index, e) 耗时 %d 毫秒 \n", stopWatch.getTime(TimeUnit.MILLISECONDS));

    }

}
