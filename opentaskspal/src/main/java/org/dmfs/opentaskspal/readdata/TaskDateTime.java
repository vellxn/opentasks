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
import org.dmfs.iterators.Function;
import org.dmfs.optional.Optional;
import org.dmfs.optional.decorators.DelegatingOptional;
import org.dmfs.optional.decorators.Mapped;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.tasks.contract.TaskContract.Tasks;


/**
 * An {@link Optional} of a specific {@link DateTime} value of a task.
 *
 * @author Marten Gajda
 */
public final class TaskDateTime extends DelegatingOptional<DateTime>
{
    public TaskDateTime(String columnName, final RowDataSnapshot<Tasks> dataSnapshot)
    {
        super(
                new Mapped<>(
                        new Function<DateTime, DateTime>()
                        {
                            @Override
                            public DateTime apply(DateTime argument)
                            {
                                return "1".equals(dataSnapshot.charData(Tasks.IS_ALLDAY).value("0").toString())
                                        ? argument.toAllDay()

                                        : argument.shiftTimeZone(new EffectiveTimezone(dataSnapshot).value());
                            }
                        },
                        new Mapped<>(
                                new Function<CharSequence, DateTime>()
                                {
                                    @Override
                                    public DateTime apply(CharSequence argument)
                                    {
                                        return new DateTime(Long.parseLong(argument.toString()));
                                    }
                                },
                                dataSnapshot.charData(columnName))));
    }
}
