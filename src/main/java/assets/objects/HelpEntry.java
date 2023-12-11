package assets.objects;

import org.jetbrains.annotations.NotNull;

public class HelpEntry implements Comparable<HelpEntry> {
    public int lines;
    public String group;
    public StringBuilder commands;

    public HelpEntry(int lines, String group, StringBuilder commands) {
        this.lines = lines;
        this.group = group;
        this.commands = commands;
    }

    @Override
    public int compareTo(@NotNull HelpEntry o) {
        return lines == o.lines ? 0 : o.lines - lines;
    }
}
