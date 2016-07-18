package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.model.TracingPhoto_Table;

import java.util.List;

public class TracingPhotoDaoImpl implements TracingPhotoDao {
    @Override
    public TracingPhoto getFirstThumbnail(long tracingId) {
        return SQLite.select().from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracing_id.eq(tracingId))
                .querySingle();
    }

    @Override
    public List<TracingPhoto> getByTracingId(long tracingId) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracing_id.eq(tracingId))
                .and(TracingPhoto_Table.photo.isNotNull())
                .queryList();
    }

    @Override
    public TracingPhoto getById(long id) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.id.eq(id))
                .querySingle();
    }

    @Override
    public TracingPhoto getByTracingIdAndOrder(long tracingId, int order) {
        return SQLite.select()
                .from(TracingPhoto.class)
                .where(TracingPhoto_Table.tracing_id.eq(tracingId))
                .and(TracingPhoto_Table.order.eq(order))
                .querySingle();
    }
}