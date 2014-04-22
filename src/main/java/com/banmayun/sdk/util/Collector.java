package com.banmayun.sdk.util;

import java.util.ArrayList;

public abstract class Collector<E, L> {

    public abstract void add(E element);

    public abstract L finish();

    public static final class ArrayListCollector<E> extends Collector<E, ArrayList<E>> {
        private ArrayList<E> list = new ArrayList<E>();

        @Override
        public void add(E element) {
            this.list.add(element);
        }

        @Override
        public ArrayList<E> finish() {
            ArrayList<E> list = this.list;
            this.list = null;
            return list;
        }
    }

    public static final class NullSkipper<E, L> extends Collector<E, L> {
        private Collector<E, L> underlying = null;

        public NullSkipper(Collector<E, L> underlying) {
            this.underlying = underlying;
        }

        public static <E, L> Collector<E, L> mk(Collector<E, L> underlying) {
            return new NullSkipper<E, L>(underlying);
        }

        @Override
        public void add(E element) {
            if (element != null) {
                this.underlying.add(element);
            }
        }

        @Override
        public L finish() {
            return this.underlying.finish();
        }
    }
}
