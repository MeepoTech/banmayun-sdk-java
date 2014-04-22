package com.banmayun.sdk.util;

public class Maybe<T> {

    private static final Maybe<Object> NOTHING = new Maybe<Object>(false, null);

    @SuppressWarnings("unchecked")
    public static <T> Maybe<T> nothing() {
        return (Maybe<T>) NOTHING;
    }

    public static <T> Maybe<T> just(T value) {
        return new Maybe<T>(true, value);
    }

    private boolean isJust = false;
    private T value = null;

    private Maybe(boolean isJust, T value) {
        this.isJust = isJust;
        this.value = value;
    }

    public boolean isNothing() {
        return !this.isJust;
    }

    public boolean isJust() {
        return this.isJust;
    }

    public T getJust() {
        return this.value;
    }

    public T get(T def) {
        if (this.isJust) {
            return this.value;
        } else {
            return def;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) {
            return false;
        }
        return equals((Maybe<?>) other);
    }

    public boolean equals(Maybe<?> other) {
        return this.isJust == other.isJust && eq(this.value, other.value);
    }

    private static boolean eq(Object a, Object b) {
        if (a == null) {
            return (b == null);
        }
        if (b == null) {
            return false;
        }
        return a.equals(b);
    }

    @Override
    public String toString() {
        if (this.isJust) {
            return "Just(" + this.value + ")";
        } else {
            return "Nothing";
        }
    }

    @Override
    public int hashCode() {
        if (!this.isJust) {
            return 0;
        }
        if (this.value == null) {
            return 1;
        }
        return this.value.hashCode();
    }
}
