package de.invesdwin.context.integration.network.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ATransformingIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.collections.iterable.skip.ATimeRangeSkippingIterable;
import de.invesdwin.util.collections.loadingcache.historical.AGapHistoricalCache;
import de.invesdwin.util.collections.loadingcache.historical.IHistoricalEntry;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.AHistoricalCacheRangeQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.interceptor.IHistoricalCacheRangeQueryInterceptor;
import de.invesdwin.util.collections.loadingcache.historical.key.APushingHistoricalCacheAdjustKeyProvider;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class ATimeRefreshingRequestCache<E> extends AGapHistoricalCache<E> {

    private final DelegateDailyRefreshingRequest<List<E>> request;
    private final APushingHistoricalCacheAdjustKeyProvider pushingAdjustKeyProvider;

    public ATimeRefreshingRequestCache(final Callable<List<E>> originalRequest) {
        this.request = new DelegateDailyRefreshingRequest<List<E>>(originalRequest) {
            @Override
            protected void onResponse(final List<E> response) {
                super.onResponse(response);
                final FDate lastTime = getLastElementTime(response);
                pushingAdjustKeyProvider.pushHighestAllowedKey(lastTime);
            }
        };
        this.pushingAdjustKeyProvider = new APushingHistoricalCacheAdjustKeyProvider(this) {
            @Override
            protected FDate getInitialHighestAllowedKey() {
                return getLastElementTime(request.call());
            }

            @Override
            protected boolean isPullingRecursive() {
                return false;
            }
        };
        setAdjustKeyProvider(pushingAdjustKeyProvider);
    }

    protected abstract Duration getShiftKeyDuration();

    private FDate getLastElementTime(final List<E> response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        final E lastValue = response.get(response.size() - 1);
        final FDate lastTime = innerExtractKey(lastValue);

        final E firstValue = response.get(0);
        final FDate firstTime = innerExtractKey(firstValue);

        Assertions.assertThat(lastTime).as(getClass().getSimpleName()).isAfterOrEqualTo(firstTime);

        return lastTime;
    }

    @Override
    protected List<? extends E> readAllValuesAscendingFrom(final FDate key) {
        final List<E> values = getResponse();
        final List<E> list = new ArrayList<E>();
        for (int i = 0; i < values.size(); i++) {
            final E e = values.get(i);
            final FDate eKey = innerExtractKey(e);
            if (!eKey.isBeforeNotNullSafe(key)) {
                list.add(e);
            }
        }
        return list;
    }

    @Override
    protected E readLatestValueFor(final FDate key) {
        final List<E> values = getResponse();
        E previousE = null;
        for (int i = 0; i < values.size(); i++) {
            final E e = values.get(i);
            if (previousE == null) {
                previousE = e;
            } else {
                final FDate eKey = innerExtractKey(e);
                if (key.isAfterOrEqualToNotNullSafe(eKey)) {
                    previousE = e;
                } else {
                    break;
                }
            }
        }
        return previousE;
    }

    @Override
    protected FDate innerCalculatePreviousKey(final FDate key) {
        return key.addSeconds(-getShiftKeyDuration().longValue(FTimeUnit.SECONDS));
    }

    @Override
    protected FDate innerCalculateNextKey(final FDate key) {
        return key.addSeconds(getShiftKeyDuration().longValue(FTimeUnit.SECONDS));
    }

    private List<E> getResponse() {
        return request.call();
    }

    @Override
    protected IHistoricalCacheRangeQueryInterceptor<E> newRangeQueryInterceptor() {
        return new AHistoricalCacheRangeQueryInterceptor<E>(this) {
            @Override
            protected ICloseableIterable<IHistoricalEntry<E>> innerGetEntries(final FDate from, final FDate to) {
                final List<E> delegate = request.call();
                final ICloseableIterable<E> closeableIterable = WrapperCloseableIterable.maybeWrap(delegate);
                final ATimeRangeSkippingIterable<E> skippingIterable = new ATimeRangeSkippingIterable<E>(
                        closeableIterable, from, to) {
                    @Override
                    protected FDate extractEndTime(final E element) {
                        return innerExtractKey(element);
                    }

                    @Override
                    protected String getName() {
                        return "ATimeRefreshingRequestCache.innerGetEntries";
                    }
                };
                return new ATransformingIterable<E, IHistoricalEntry<E>>(skippingIterable) {

                    @Override
                    protected IHistoricalEntry<E> transform(final E value) {
                        return new IHistoricalEntry<E>() {

                            @Override
                            public FDate getKey() {
                                return innerExtractKey(value);
                            }

                            @Override
                            public E getValue() {
                                return value;
                            }

                            @Override
                            public E setValue(final E value) {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public String toString() {
                                return getKey() + " -> " + getValue();
                            }

                        };
                    }
                };
            }
        };
    }

}