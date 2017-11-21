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

package org.dmfs.tasks.data;

import android.content.ContentProviderClient;
import android.content.Context;

import org.dmfs.android.contentpal.RowDataSnapshot;
import org.dmfs.android.contentpal.RowSet;
import org.dmfs.android.contentpal.RowSnapshot;
import org.dmfs.android.contentpal.rowsets.Cached;
import org.dmfs.iterables.decorators.Mapped;
import org.dmfs.tasks.utils.rxjava.singlesource.DelegatingSingleSource;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * {@link SingleSource} that accesses the Tasks provider, runs the given {@link CpQuery}
 * and delivers the the result {@link Iterable} of {@link RowDataSnapshot}s.
 *
 * @author Gabor Keszthelyi
 */
public final class CpQuerySource<T> extends DelegatingSingleSource<Iterable<RowDataSnapshot<T>>>
{

    public CpQuerySource(final Context context, final CpQuery<T> cpQuery)
    {
        super(Single.wrap(new ContentProviderClientSource(context))
                .map(new Function<ContentProviderClient, Iterable<RowDataSnapshot<T>>>()
                {
                    @Override
                    public Iterable<RowDataSnapshot<T>> apply(@NonNull ContentProviderClient client) throws Exception
                    {
                        RowSet<T> frozen = new Cached<>(cpQuery.rowSet(client, context));
                        frozen.iterator(); // To actually freeze it

                        return new Mapped<>(frozen, new org.dmfs.iterators.Function<RowSnapshot<T>, RowDataSnapshot<T>>()
                        {
                            @Override
                            public RowDataSnapshot<T> apply(RowSnapshot<T> rowSnapshot)
                            {
                                return rowSnapshot.values();
                            }
                        });
                    }
                }));
    }

}
