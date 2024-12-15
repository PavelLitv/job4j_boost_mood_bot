package ru.job4j.bmb.telegram;

import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.telergam.SentContent;

import java.util.ArrayList;
import java.util.List;

public class SentContentFake implements SentContent {
    private final List<Content> result = new ArrayList<>();

    @Override
    public void sent(Content content) {
        result.add(content);
    }

    public List<Content> getResult() {
        return result;
    }
}
