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

    static Vector2D fromPolarCoords(double angle, double length) {
        double x = Math.cos(angle) * length;
        double y = Math.sin(angle) * length;
        return new Vector2D(x, y);
    }

    Vector2D add(Vector2D v) {
        return new Vector2D(this.x + v.x, this.y + v.y);
    }

    Vector2D subtract(Vector2D v) {
        return new Vector2D(this.x - v.x, this.y - v.y);
    }

    Vector2D scale(double f) {
        return new Vector2D(this.x * f, this.y * f);
    }

    Vector2D rotate(double a) {
        double rad = Math.toRadians(a);
        double x = Math.cos(rad) * this.x - Math.sin(rad) * this.y;
        double y = Math.sin(rad) * this.x + Math.cos(rad) * this.y;
        return new Vector2D(x, y);
    }

    double angle() {
        return Math.atan2(this.y, this.x);
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
