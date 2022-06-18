package Team;

class Vector2D {
    double x;
    double y;

    Vector2D() {
        this.x = 0.0;
        this.y = 0.0;
    }

    Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Vector2D add(Vector2D v) {
        return new Vector2D(this.x + v.x, this.y + v.y);
    }

    Vector2D subtract(Vector2D v) {
        return new Vector2D(this.x - v.x, this.y - v.y);
    }

    double length() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    double lengthSquared() {
        return Math.pow(this.x, 2) + Math.pow(this.y, 2);
    }

    Vector2D normalize() {
        double l = this.length();
        return new Vector2D(this.x / l, this.y / l);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        Vector2D v = (Vector2D)o;
        return v.x == this.x && v.y == this.y;
    }

    protected Object clone() {
        return new Vector2D(this.x, this.y);
    }
}
