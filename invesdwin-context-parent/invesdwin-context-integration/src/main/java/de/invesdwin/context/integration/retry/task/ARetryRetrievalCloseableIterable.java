package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.hook.IRetryHook;
import de.invesdwin.context.integration.retry.hook.RetryHookSupport;
import de.invesdwin.util.collections.iterable.ADelegateCloseableIterator;
import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public abstract class ARetryRetrievalCloseableIterable<T> implements ICloseableIterable<T> {

    private final FDate fromDate;
    private final FDate toDate;

    public ARetryRetrievalCloseableIterable(final FDate fromDate, final FDate toDate) {
        if (fromDate == null) {
            throw new NullPointerException("fromDate should not be null");
        }
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public ICloseableIterator<T> iterator() {
        return new ICloseableIterator<T>() {

            private FDate curDate = fromDate.addMilliseconds(-1);
            private ICloseableIterator<? extends T> queryRetry = new ADelegateCloseableIterator<T>() {
                @Override
                protected ICloseableIterator<? extends T> newDelegate() {
                    return queryRetry();
                }
            };

            private ICloseableIterator<? extends T> queryRetry() {
                return new ICloseableIterator<T>() {

                    private ICloseableIterator<? extends T> delegate;
                    private final IRetryHook retryListener = new RetryHookSupport() {
                        @Override
                        public void onBeforeRetry(final RetryOriginator originator, final int retryCount,
                                final Throwable cause) {
                            //reinitialize delegate
                            if (delegate != null) {
                                delegate.close();
                            }
                            delegate = null;
                        }
                    };
                    private final ARetryCallable<Boolean> hasNext = new ARetryCallable<Boolean>(
                            new RetryOriginator(ARetryRetrievalCloseableIterable.class, "hasNext")) {
                        @Override
                        protected Boolean callRetry() throws Exception {
                            return getDelegate().hasNext();
                        }

                        @Override
                        protected IRetryHook getRetryListener() {
                            return retryListener;
                        }
                    };
                    private final ARetryCallable<T> next = new ARetryCallable<T>(
                            new RetryOriginator(ARetryRetrievalCloseableIterable.class, "next")) {
                        @Override
                        protected T callRetry() throws Exception {
                            return getDelegate().next();
                        }

                        @Override
                        protected IRetryHook getRetryListener() {
                            return retryListener;
                        }
                    };

                    private ICloseableIterator<? extends T> getDelegate() {
                        if (delegate == null) {
                            delegate = query(curDate.addMilliseconds(1), toDate);
                        }
                        return delegate;
                    }

                    @Override
                    public boolean hasNext() {
                        return hasNext.call();
                    }

                    @Override
                    public T next() {
                        return next.call();
                    }

                    @Override
                    public void close() {
                        if (delegate != null) {
                            delegate.close();
                        }
                        delegate = EmptyCloseableIterator.getInstance();
                    }
                };
            }

            @Override
            public boolean hasNext() {
                return queryRetry.hasNext();
            }

            @Override
            public T next() {
                final T next = queryRetry.next();
                final FDate nextDate = extractTime(next);
                if (nextDate == null) {
                    throw new NullPointerException("nextDate is null for [" + next + "]");
                }
                if (!curDate.equals(fromDate) && nextDate.isBeforeNotNullSafe(curDate)) {
                    close();
                    throw FastNoSuchElementException
                            .getInstance("ARetryRetrievalCloseableIterable: nextDate is before as curDate");
                } else {
                    curDate = nextDate;
                    return next;
                }
            }

            @Override
            public void close() {
                queryRetry.close();
                queryRetry = EmptyCloseableIterator.getInstance();
            }
        };
    }

    protected abstract FDate extractTime(T next);

    protected abstract ICloseableIterator<? extends T> query(FDate fromDate, FDate toDate);
}