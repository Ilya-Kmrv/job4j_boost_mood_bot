package ru.job4j.bmb.tg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class VoiceHandle {
    public List<String> process(String message) {
        //var result = new ArrayList<String>();
        IntStream.range(0, 5).forEach(it -> {
            try {
                Thread.sleep(1000);
                //result.add(String.format("Message: %s", it));
                System.out.println(String.format("Message: %s", it));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //return result;
        return new ArrayList<>();
    }
}