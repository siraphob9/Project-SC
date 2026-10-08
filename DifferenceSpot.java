public class DifferenceSpot {
    private int x;
    private int y;
    private int radius;
    private boolean found;

    public DifferenceSpot(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.found = false;
    }

    /** TODO: เช็คว่าพิกัดคลิกอยู่ในวงกลมของจุดนี้หรือไม่ */
    public boolean contains(int clickX, int clickY) {
        long dx = clickX - x;
        long dy = clickY - y;
        return dx * dx + dy * dy <= (long) radius * radius;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getRadius() { return radius; }
    public boolean isFound() { return found; }
    public void setFound(boolean found) { this.found = found; }
}
