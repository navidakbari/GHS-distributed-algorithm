package Events;

import se.sics.kompics.KompicsEvent;

import java.util.HashMap;

public class ChangeRootMessage implements KompicsEvent {
    public String src;
    public String dst;
    public String coordinator;
    public int level;
    public String fragmentName;

    public ChangeRootMessage(String src, String dst, String coordinator, int level, String fragmentName) {
        this.src = src;
        this.dst = dst;
        this.coordinator = coordinator;
        this.level = level;
        this.fragmentName = fragmentName;
    }
}