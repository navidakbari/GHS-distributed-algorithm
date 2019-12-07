package Events;

import se.sics.kompics.KompicsEvent;

public class JoinMessage implements KompicsEvent {
    public String src;
    public String dst;
    public int edge_weight;

    public JoinMessage(String src, String dst, int edge_weight) {
        this.src = src;
        this.dst = dst;
        this.edge_weight = edge_weight;
    }
}
