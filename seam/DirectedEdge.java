/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class DirectedEdge {
    private final int v;
    private final int w;

    public DirectedEdge(int v, int w) {
        this.v = v;
        this.w = w;

    }

    public int from() {
        return v;
    }

    public int to() {
        return w;
    }

    public String toString() {
        return String.format("%d->%d ", v, w);
    }
}
