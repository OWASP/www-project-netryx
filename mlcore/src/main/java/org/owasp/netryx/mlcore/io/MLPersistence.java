package org.owasp.netryx.mlcore.io;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MLPersistence<T extends MLComponent> {
    private final T model;

    public MLPersistence(T model) {
        this.model = model;
    }

    public T load(InputStream in) {
        try (var din = new DataInputStream(in)) {
            model.load(din);
            return model;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void save(OutputStream out) {
        try (var dout = new DataOutputStream(out)) {
            model.save(dout);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    public void saveToFile(Path path) {
        try (
            var fout = new FileOutputStream(path.toFile());
            var gout = new GZIPOutputStream(fout)
        ) {
            save(gout);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public T loadFromFile(Path path) {
        try (
            var fin = new FileInputStream(path.toFile());
            var gin = new GZIPInputStream(fin)
        ) {
            load(gin);
            return model;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
