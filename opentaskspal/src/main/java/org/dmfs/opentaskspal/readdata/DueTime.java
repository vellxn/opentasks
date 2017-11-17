/*
 * Copyright 2017 dmfs GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dmfs.opentaskspal.readdata;

import org.dmfs.android.contentpal.RowDataSnapshot;
import org.dmfs.opentaskspal.readdata.temp.SinglePresent;
import org.dmfs.optional.Absent;
import org.dmfs.optional.Optional;
import org.dmfs.optional.Present;
import org.dmfs.optional.decorators.DelegatingOptional;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;
import org.dmfs.tasks.contract.TaskContract.Tasks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * {@link Optional} for the due {@link DateTime} of a task.
 *
 * @author Gabor Keszthelyi
 */
public final class DueTime extends DelegatingOptional<DateTime>
{
    public static final String[] PROJECTION;

    // TODO Either switch to Iterables (maybe at {@link View#withProjection} as well), or create support for array operations
    static
    {
        Set<String> projection = new HashSet<>();
        projection.add(Tasks.DUE);
        projection.add(Tasks.DTSTART);
        projection.add(Tasks.DURATION);
        projection.addAll(Arrays.asList(TaskDateTime.PROJECTION));
        PROJECTION = projection.toArray(new String[3 + TaskDateTime.PROJECTION.length]);
    }

    public DueTime(RowDataSnapshot<Tasks> rowDataSnapshot)
    {
        super(create(rowDataSnapshot));
    }


    private static Optional<DateTime> create(RowDataSnapshot<Tasks> data)
    {
        if (data.charData(Tasks.DUE).isPresent())
        {
            return new SinglePresent<>(new TaskDateTime(data.charData(Tasks.DUE).value(), data));
        }

        if (data.charData(Tasks.DURATION).isPresent() && data.charData(Tasks.DTSTART).isPresent())
        {
            DateTime start = new TaskDateTime(data.charData(Tasks.DTSTART).value(), data).value();
            Duration duration = Duration.parse(data.charData(Tasks.DURATION).value().toString());
            return new Present<>(start.addDuration(duration));
        }

        return Absent.absent();
    }

}
