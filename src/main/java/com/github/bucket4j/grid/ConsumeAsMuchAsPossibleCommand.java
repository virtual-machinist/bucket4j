/*
 * Copyright 2015 Vladimir Bukhtoyarov
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bucket4j.grid;

import com.github.bucket4j.Bandwidth;
import com.github.bucket4j.BucketConfiguration;
import com.github.bucket4j.BucketState;

public class ConsumeAsMuchAsPossibleCommand implements GridCommand<Long> {

    private long limit;
    private boolean bucketStateModified;

    public ConsumeAsMuchAsPossibleCommand(long limit) {
        this.limit = limit;
    }

    @Override
    public Long execute(GridBucketState gridState) {
        BucketConfiguration configuration = gridState.getBucketConfiguration();
        BucketState state = gridState.getBucketState();
        long currentTimeNanos = configuration.getTimeMeter().currentTimeNanos();
        Bandwidth[] limits = configuration.getLimitedBandwidths();
        Bandwidth guarantee = configuration.getGuaranteedBandwidth();
        state.refillAllBandwidth(limits, guarantee, currentTimeNanos);
        long availableToConsume = state.getAvailableTokens(limits, guarantee);
        long toConsume = Math.min(limit, availableToConsume);
        if (toConsume <= 0) {
            return 0l;
        }
        state.consume(limits, guarantee, toConsume);
        bucketStateModified = true;
        return toConsume;
    }

    @Override
    public boolean isBucketStateModified() {
        return bucketStateModified;
    }

}
