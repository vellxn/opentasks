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

import android.support.annotation.NonNull;

import org.dmfs.android.contentpal.Projection;
import org.dmfs.android.contentpal.RowDataSnapshot;
import org.dmfs.android.contentpal.projections.Composite;
import org.dmfs.android.contentpal.projections.MultiProjection;
import org.dmfs.iterables.elementary.Seq;
import org.dmfs.iterators.Function;
import org.dmfs.jems.function.BiFunction;
import org.dmfs.optional.Optional;
import org.dmfs.optional.adapters.FirstPresent;
import org.dmfs.optional.composite.Zipped;
import org.dmfs.optional.decorators.DelegatingOptional;
import org.dmfs.optional.decorators.Mapped;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;
import org.dmfs.tasks.contract.TaskContract.Tasks;


/**
 * {@link Optional} for the due {@link DateTime} of a task.
 *
 * @author Gabor Keszthelyi
 */
public final class DueTime extends DelegatingOptional<DateTime>
{
    public static final Projection<Tasks> PROJECTION = new Composite<>(
            new MultiProjection<Tasks>(Tasks.DUE, Tasks.DTSTART, Tasks.DURATION),
            TaskDateTime.PROJECTION);


    public DueTime(@NonNull final RowDataSnapshot<Tasks> rowDataSnapshot)
    {
        super(new FirstPresent<>(new Seq<>(

                        // Use DUE if available
                        new Mapped<>(new Function<CharSequence, DateTime>()
                        {
                            @Override
                            public DateTime apply(CharSequence dueCharSequence)
                            {
                                return new TaskDateTime(dueCharSequence, rowDataSnapshot).value();
                            }
                        }, rowDataSnapshot.charData(Tasks.DUE)),

                        // Or use DTSTART + DURATION if both are available
                        new Zipped<>(
                                rowDataSnapshot.charData(Tasks.DTSTART),
                                rowDataSnapshot.charData(Tasks.DURATION),
                                new BiFunction<CharSequence, CharSequence, DateTime>()
                                {
                                    @Override
                                    public DateTime value(CharSequence startCharSequence, CharSequence durationCharSequence)
                                    {
                                        return new TaskDateTime(startCharSequence, rowDataSnapshot).value()
                                                .addDuration(Duration.parse(durationCharSequence.toString()));
                                    }
                                })

                        // Otherwise DueTime is absent
                ))
        );
    }

}
