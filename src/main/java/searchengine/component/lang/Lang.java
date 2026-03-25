package searchengine.component.lang;

import java.util.regex.Pattern;

public enum Lang {

    RU("[а-яА-Я]+"),
    EN("[a-zA-Z]+");

    private final Pattern pattern;

    Lang(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public boolean matches(String word) {
        return pattern.matcher(word).matches();
    }
}
