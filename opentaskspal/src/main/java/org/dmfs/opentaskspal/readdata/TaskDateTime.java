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
import org.dmfs.android.contentpal.projections.MultiProjection;
import org.dmfs.jems.single.Single;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.tasks.contract.TaskContract.Tasks;

import java.util.TimeZone;


/**
 * {@link Single} for creating a {@link DateTime} from the date-time related properties of a task.
 * Takes an already present timestamp, plus the separately stored timezone and all-day flag.
 *
 * @author Gabor Keszthelyi
 */
public final class TaskDateTime implements Single<DateTime>
{
    public static final Projection<Tasks> PROJECTION = new MultiProjection<>(Tasks.TZ, Tasks.IS_ALLDAY);

    private final Long mTimestamp;
    private final String mTimeZoneId;
    private final boolean mIsAllDay;


    public TaskDateTime(@NonNull Long timestamp, @NonNull String timeZoneId, @NonNull Boolean isAllDay)
    {
        mTimestamp = timestamp;
        mTimeZoneId = timeZoneId;
        mIsAllDay = isAllDay;
    }


    public TaskDateTime(@NonNull CharSequence timestamp, @NonNull CharSequence timeZoneId, @NonNull CharSequence isAllDay)
    {
        this(Long.valueOf(timestamp.toString()), timeZoneId.toString(), "1".equals(isAllDay.toString()));
    }


    public TaskDateTime(@NonNull CharSequence timestamp, @NonNull RowDataSnapshot<Tasks> rowData)
    {
        this(timestamp, rowData.charData(Tasks.TZ).value(), rowData.charData(Tasks.IS_ALLDAY).value());
    }


    @Override
    public DateTime value()
    {
        return mIsAllDay ? new DateTime(mTimestamp).toAllDay() : new DateTime(TimeZone.getTimeZone(mTimeZoneId), mTimestamp);
    }
}
