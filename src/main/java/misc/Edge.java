package misc;

public class Edge {
    public String src;
    public String dst;
    public int weight;
    public enum Type{
        Basic, Branch, Rejected;
    }
    public Type type;

    public Edge(String src, String dst, int weight) {
        this.src = src;
        this.dst = dst;
        this.weight = weight;
    }

    public Type getType(){
        return this.type;
    }

    public void setBasic(int type) {
        this.type = Type.Basic;
    }

    public void setBranch(int type) {
        this.type = Type.Branch;
    }

    public void setReject(int type) {
        this.type = Type.Rejected;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "src='" + src + '\'' +
                ", dst='" + dst + '\'' +
                ", weight=" + weight +
                '}';
    }
}
