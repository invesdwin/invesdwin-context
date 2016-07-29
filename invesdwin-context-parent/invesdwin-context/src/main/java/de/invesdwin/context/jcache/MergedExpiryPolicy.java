package de.invesdwin.context.jcache;

import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.cache.expiry.ExpiryPolicy;

import com.google.common.base.Function;

import de.invesdwin.context.jcache.util.Durations;

@NotThreadSafe
public class MergedExpiryPolicy implements ExpiryPolicy {

    private javax.cache.expiry.Duration minExpiryForCreation;
    private javax.cache.expiry.Duration minExpiryForAccess;
    private javax.cache.expiry.Duration minExpiryForUpdate;

    public MergedExpiryPolicy(final Set<ExpiryPolicy> expiryPolicies) {
        this.minExpiryForCreation = min(expiryPolicies, new Function<ExpiryPolicy, javax.cache.expiry.Duration>() {
            @Override
            public javax.cache.expiry.Duration apply(final ExpiryPolicy input) {
                return input.getExpiryForCreation();
            }
        });
        this.minExpiryForAccess = min(expiryPolicies, new Function<ExpiryPolicy, javax.cache.expiry.Duration>() {
            @Override
            public javax.cache.expiry.Duration apply(final ExpiryPolicy input) {
                return input.getExpiryForAccess();
            }
        });
        this.minExpiryForUpdate = min(expiryPolicies, new Function<ExpiryPolicy, javax.cache.expiry.Duration>() {
            @Override
            public javax.cache.expiry.Duration apply(final ExpiryPolicy input) {
                return input.getExpiryForUpdate();
            }
        });
    }

    private javax.cache.expiry.Duration min(final Set<ExpiryPolicy> expiryPolicies,
            final Function<ExpiryPolicy, javax.cache.expiry.Duration> function) {
        javax.cache.expiry.Duration min = null;
        for (final ExpiryPolicy e : expiryPolicies) {
            final javax.cache.expiry.Duration cur = function.apply(e);
            if (min == null) {
                min = cur;
            } else if (cur != null) {
                if (cur.isZero()) {
                    //this is the shortest it can get
                    min = cur;
                    break;
                } else if (min.isEternal()) {
                    //maybe cur is shorter than eternal or the same
                    min = cur;
                } else if (Durations.fromJCache(cur).isLessThan(Durations.fromJCache(min))) {
                    //cur is shorter than min
                    min = cur;
                }
            }
        }
        return min;
    }

    @Override
    public javax.cache.expiry.Duration getExpiryForCreation() {
        return minExpiryForCreation;
    }

    @Override
    public javax.cache.expiry.Duration getExpiryForAccess() {
        return minExpiryForAccess;
    }

    @Override
    public javax.cache.expiry.Duration getExpiryForUpdate() {
        return minExpiryForUpdate;
    }
}
