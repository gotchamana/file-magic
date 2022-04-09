package io.github.gotchamana.magic;

import static io.github.gotchamana.magic.MagicLibrary.*;
import static java.util.stream.Collectors.joining;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;

import lombok.NonNull;

public class FileMagic implements Closeable {

    private final Pointer cookie;

    public FileMagic(String... databasePaths) {
        this.cookie = checkMagicReturn(() -> INSTANCE.magic_open(MAGIC_NONE), "Cannot allocate magic cookie");
        
        checkMagicReturn(() -> INSTANCE.magic_load(cookie, combinePaths(databasePaths)));
    }

    private String combinePaths(String... databasePaths) {
        var paths = Arrays.stream(Objects.requireNonNullElse(databasePaths, new String[0]))
            .filter(Predicate.not(path -> path == null || path.isBlank()))
            .collect(joining(":"));

        return paths.isBlank() ? null : paths;
    }

    private <T> T checkMagicReturn(Supplier<T> f) {
        return checkMagicReturn(f, null);
    }

    private <T> T checkMagicReturn(Supplier<T> f, String message) {
        var result = f.get();

        if (result == null || (result instanceof Number && !result.equals(0)))
            throw new MagicException(Objects.requireNonNullElse(message, INSTANCE.magic_error(cookie)));

        return result;
    }

    public String getDescription(@NonNull String file, MagicOption... options) {
        checkFileExists(file);

        INSTANCE.magic_setflags(cookie, combineOptions(MAGIC_NONE, options));

        return checkMagicReturn(() -> INSTANCE.magic_file(cookie, file));
    }

    private void checkFileExists(String file) {
        if (file.isBlank() || !Files.exists(Path.of(file)))
            throw new IllegalArgumentException("No such file or directory: " + file);
    }

    private int combineOptions(int option, MagicOption... options) {
        return option | Arrays.stream(Objects.requireNonNullElse(options, new MagicOption[0]))
            .filter(Objects::nonNull)
            .mapToInt(MagicOption::getValue)
            .reduce(0, (o1, o2) -> o1 | o2);
    }

    public String getDescription(@NonNull byte[] content, MagicOption... options) {
        INSTANCE.magic_setflags(cookie, combineOptions(MAGIC_NONE, options));

        return checkMagicReturn(
            () -> INSTANCE.magic_buffer(cookie, ByteBuffer.wrap(content), new LibCAPI.size_t(content.length)));
    }

    public String getMime(@NonNull String file, MagicOption... options) {
        checkFileExists(file);

        INSTANCE.magic_setflags(cookie, combineOptions(MAGIC_MIME, options));

        return checkMagicReturn(() -> INSTANCE.magic_file(cookie, file));
    }

    public String getMime(@NonNull byte[] content, MagicOption... options) {
        INSTANCE.magic_setflags(cookie, combineOptions(MAGIC_MIME, options));

        return checkMagicReturn(
            () -> INSTANCE.magic_buffer(cookie, ByteBuffer.wrap(content), new LibCAPI.size_t(content.length)));
    }

    public Set<String> getExtensions(@NonNull String file, MagicOption... options) {
        checkFileExists(file);

        INSTANCE.magic_setflags(cookie, combineOptions(MAGIC_EXTENSION, options));

        var extensions = checkMagicReturn(() -> INSTANCE.magic_file(cookie, file));
        return Set.of(extensions.split("/"));
    }

    public Set<String> getExtensions(@NonNull byte[] content, MagicOption... options) {
        INSTANCE.magic_setflags(cookie, combineOptions(MAGIC_EXTENSION, options));

        var extensions = checkMagicReturn(
            () -> INSTANCE.magic_buffer(cookie, ByteBuffer.wrap(content), new LibCAPI.size_t(content.length)));
        return Set.of(extensions.split("/"));
    }

    @Override
    public void close() {
        INSTANCE.magic_close(cookie);
    }
}
