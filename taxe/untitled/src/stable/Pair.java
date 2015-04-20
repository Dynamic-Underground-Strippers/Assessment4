package stable;

public class Pair<T, Q> {
    public T first; public Q second;
    public Pair(T f, Q s) { first = f; second = s; }
    public T getFirst(){
        return this.first;
    }
    public Q getSecond(){
        return  this.second;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair p = (Pair) other;
            return getFirst().equals(p.getFirst()) && getSecond().equals(p.getSecond());
        } else {
            return false;
        }

    }

    public String toString (){
        return "( " + this.first + " , " + this.second + ")";
    }
}