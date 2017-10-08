package dylan.tide_api.core;

public class Pair<A, B> {

    private final A a;
    private final B b;

    public Pair(A key, B value) {

	this.a = key;
	this.b = value;
    }

    public A getA() {
	return a;
    }

    public B getB() {
	return b;
    }

}
