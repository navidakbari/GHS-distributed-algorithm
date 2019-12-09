package Events;

import se.sics.kompics.KompicsEvent;

public class InitiateMessage implements KompicsEvent {
    public String src;
    public String dst;
    public String coordinator;
    public int level;
    public String fragmentName;

    public InitiateMessage(String src, String dst, String coordinator, int level, String fragmentName) {
        this.src = src;
        this.dst = dst;
        this.coordinator = coordinator;
        this.level = level;
        this.fragmentName = fragmentName;
    }


}
