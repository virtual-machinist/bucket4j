/*
 *
 *   Copyright 2015-2017 Vladimir Bukhtoyarov
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.bucket4j.distributed.proxy.optimizers.predictive;

import io.github.bucket4j.TimeMeter;
import io.github.bucket4j.distributed.proxy.AsyncCommandExecutor;
import io.github.bucket4j.distributed.proxy.CommandExecutor;
import io.github.bucket4j.distributed.proxy.RequestOptimizer;
import io.github.bucket4j.distributed.proxy.optimizers.batch.AsyncBatchingExecutor;
import io.github.bucket4j.distributed.proxy.optimizers.batch.BatchingExecutor;

public class PredictiveOptimizer implements RequestOptimizer {

    private final PredictionThresholds thresholds;
    private final TimeMeter timeMeter;

    public PredictiveOptimizer(PredictionThresholds thresholds) {
        this(thresholds, TimeMeter.SYSTEM_MILLISECONDS);
    }

    public PredictiveOptimizer(PredictionThresholds thresholds, TimeMeter timeMeter) {
        this.thresholds = thresholds;
        this.timeMeter = timeMeter;
    }

    @Override
    public CommandExecutor optimize(CommandExecutor originalExecutor) {
        PredictiveCommandExecutor predictiveCommandExecutor = new PredictiveCommandExecutor(originalExecutor, thresholds, timeMeter);
        return new BatchingExecutor(predictiveCommandExecutor);
    }

    @Override
    public AsyncCommandExecutor optimize(AsyncCommandExecutor originalExecutor) {
        PredictiveCommandExecutor predictiveCommandExecutor = new PredictiveCommandExecutor(originalExecutor, thresholds, timeMeter);
        return new AsyncBatchingExecutor(predictiveCommandExecutor);
    }

}
