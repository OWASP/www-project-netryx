package org.owasp.netryx.mlcore.encoder;

import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.Series;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class OneHotEncoder implements Encoder {
    private String columnName;
    private Set<String> uniqueValues;

    @Override
    public void fit(DataFrame df, String columnName) {
        this.columnName = columnName;

        this.uniqueValues = new HashSet<>(df.getColumn(columnName)
                .castAs(String.class)
                .unique()
                .getData());
    }

    @Override
    public DataFrame transform(DataFrame df) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>(df.getData());
        newData.remove(columnName);

        for (var uniqueValue : uniqueValues) {
            var encodedColumn = new double[df.height()];

            for (var i = 0; i < df.height(); i++) {
                var value = df.getColumn(columnName).castAs(String.class).get(i);
                encodedColumn[i] = value.equals(uniqueValue) ? 1.0 : 0.0;
            }

            newData.put(columnName + "_" + uniqueValue, Series.ofDouble(encodedColumn));
        }

        return new DataFrame(newData);
    }

    public String getColumnName() {
        return columnName;
    }

    public Set<String> getUniqueValues() {
        return uniqueValues;
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_ENCODER);
        out.writeUTF(columnName);

        var size = uniqueValues.size();

        out.writeInt(size);
        for (var value : uniqueValues)
            out.writeUTF(value);

        out.writeInt(MLFlag.END_ENCODER);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartEncoder(in.readInt());

        columnName = in.readUTF();

        var size = in.readInt();
        this.uniqueValues = new HashSet<>(size);

        for (var i = 0; i < size; i++)
            uniqueValues.add(in.readUTF());

        MLFlag.ensureEndEncoder(in.readInt());
    }
}