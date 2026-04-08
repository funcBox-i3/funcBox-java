package funcBox.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File and resource utilities.
 *
 * <p>This class is designed to be "JAR-safe" (works the same way when packaged).
 * It provides defensive guard-rails around common IO tasks to reduce runtime
 * errors for beginners.</p>
 *
 * <p><b>Design rules:</b>
 * <ul>
 *   <li>No external dependencies (JDK only).</li>
 *   <li>All methods are static; the class cannot be instantiated.</li>
 *   <li>Prefer safe defaults and predictable behavior.</li>
 * </ul>
 * </p>
 *
 * @since 1.1.1
 */
public final class FileUtil {

    private static final DateTimeFormatter BACKUP_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private FileUtil() {
    }

    /**
     * Load a text file from the application's classpath (e.g., {@code src/main/resources}) as a UTF-8 String.
     *
     * <p>This method is JAR-safe: it uses the class loader to locate the resource
     * and reads it via {@link java.lang.ClassLoader#getResourceAsStream(String)}.</p>
     *
     * <p><b>Guard-rails:</b>
     * <ul>
     *   <li>If {@code path} is null/blank =&gt; returns {@code null}</li>
     *   <li>If the resource is missing =&gt; returns {@code null}</li>
     *   <li>Never throws for "not found" (only throws for unexpected IO failures)</li>
     * </ul>
     * </p>
     *
     * @param path resource path relative to the classpath root (example: {@code "data/sample.json"})
     * @return resource contents as UTF-8 text, or {@code null} if missing/blank
     * @throws RuntimeException if an IO error occurs while reading an existing resource
     */
    public static String loadResource(String path) {
        if (path == null) {
            return null;
        }
        String normalized = path.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = FileUtil.class.getClassLoader();
        }

        try (InputStream in = cl.getResourceAsStream(normalized)) {
            if (in == null) {
                return null;
            }
            byte[] bytes = in.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + normalized, e);
        }
    }

    /**
     * Write text to a file atomically with backup and rollback.
     *
     * <p><b>What it guarantees:</b>
     * <ul>
     *   <li>Writes to a temporary file in the same directory first.</li>
     *   <li>Moves the temp file into place using an atomic move when supported.</li>
     *   <li>If the target exists, it is backed up before replacement.</li>
     *   <li>If replacement fails after backup, the backup is restored (best-effort rollback).</li>
     * </ul>
     * </p>
     *
     * <p><b>Guard-rails:</b>
     * <ul>
     *   <li>{@code path} must not be null (throws {@link IllegalArgumentException}).</li>
     *   <li>Parent directories are created automatically.</li>
     *   <li>{@code content} may be null; it will be written as an empty string.</li>
     * </ul>
     * </p>
     *
     * @param path    destination path
     * @param content content to write (UTF-8); null becomes empty
     * @throws IllegalArgumentException if {@code path} is null
     * @throws RuntimeException         if an IO error prevents a successful write
     */
    public static void safeWrite(Path path, String content) {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }

        String body = (content == null) ? "" : content;

        Path parent = path.toAbsolutePath().getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }

            String fileName = path.getFileName() == null ? "file" : path.getFileName().toString();
            Path tmp = path.resolveSibling(fileName + ".tmp");

            Files.writeString(tmp, body, StandardCharsets.UTF_8);

            Path backup = null;
            boolean targetExists = Files.exists(path);
            if (targetExists) {
                backup = path.resolveSibling(fileName + ".bak." + LocalDateTime.now().format(BACKUP_TS));
                Files.copy(path, backup, StandardCopyOption.COPY_ATTRIBUTES);
            }

            try {
                try {
                    Files.move(tmp, path,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException atomicNotSupported) {
                    // Fallback: still safe (temp file) but not guaranteed atomic.
                    Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException replaceFailed) {
                // rollback best-effort
                safeDeleteIfExists(tmp);
                if (backup != null) {
                    try {
                        Files.move(backup, path, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ignored) {
                        // If rollback fails, we still surface the original error.
                    }
                }
                throw replaceFailed;
            }

            // If we succeeded and we created a backup, leave it on disk intentionally.
            // (User can delete old backups safely.)

        } catch (IOException e) {
            throw new RuntimeException("safeWrite failed for path=" + path, e);
        }
    }

    /**
     * Detect a file's MIME type without relying on its extension.
     *
     * <p><b>How it works:</b>
     * <ol>
     *   <li>First tries {@link Files#probeContentType(Path)} (OS / installed file type detectors).</li>
     *   <li>If that returns null, reads the first bytes and detects common "magic numbers"</li>
     * </ol>
     * </p>
     *
     * <p><b>Return rules:</b>
     * <ul>
     *   <li>If {@code file} is null/missing/not a file =&gt; returns {@code null}</li>
     *   <li>If unknown =&gt; returns {@code "application/octet-stream"}</li>
     * </ul>
     * </p>
     *
     * @param file file to inspect
     * @return detected mime type, {@code application/octet-stream} when unknown, or {@code null} for invalid input
     */
    public static String getMimeType(File file) {
        if (file == null) {
            return null;
        }
        Path path = file.toPath();
        if (!Files.isRegularFile(path)) {
            return null;
        }

        try {
            String probed = Files.probeContentType(path);
            if (probed != null && !probed.isBlank()) {
                return probed;
            }
        } catch (IOException ignored) {
            // fall through to signature detection
        }

        try (InputStream in = new BufferedInputStream(Files.newInputStream(path))) {
            byte[] header = in.readNBytes(64);
            String bySig = detectBySignature(header);
            if (bySig != null) {
                return bySig;
            }
        } catch (IOException ignored) {
            // ignore and return default
        }

        return "application/octet-stream";
    }

    private static void safeDeleteIfExists(Path p) {
        if (p == null) {
            return;
        }
        try {
            Files.deleteIfExists(p);
        } catch (IOException ignored) {
        }
    }

    private static String detectBySignature(byte[] header) {
        if (header == null || header.length == 0) {
            return null;
        }

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (startsWith(header, new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A})) {
            return "image/png";
        }

        // JPEG: FF D8 FF
        if (startsWith(header, new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF})) {
            return "image/jpeg";
        }

        // GIF: GIF87a / GIF89a
        if (startsWithAscii(header, "GIF87a") || startsWithAscii(header, "GIF89a")) {
            return "image/gif";
        }

        // PDF: %PDF
        if (startsWithAscii(header, "%PDF")) {
            return "application/pdf";
        }

        // ZIP: PK\x03\x04
        if (startsWith(header, new byte[]{0x50, 0x4B, 0x03, 0x04})) {
            return "application/zip";
        }

        // MP3: ID3
        if (startsWithAscii(header, "ID3")) {
            return "audio/mpeg";
        }

        // MP4: ....ftyp
        if (header.length >= 12) {
            // bytes 4-7 should be 'f','t','y','p'
            if ((header[4] == 'f') && (header[5] == 't') && (header[6] == 'y') && (header[7] == 'p')) {
                return "video/mp4";
            }
        }

        // Quick JSON heuristic (NOT a guarantee): leading whitespace then { or [
        int i = 0;
        while (i < header.length && isJsonWhitespace(header[i])) {
            i++;
        }
        if (i < header.length && (header[i] == '{' || header[i] == '[')) {
            return "application/json";
        }

        // Plain text heuristic: mostly printable ASCII
        int printable = 0;
        int checked = Math.min(header.length, 64);
        for (int j = 0; j < checked; j++) {
            int b = header[j] & 0xFF;
            if (b == 0) {
                return null; // binary
            }
            if (b == '\n' || b == '\r' || b == '\t' || (b >= 0x20 && b <= 0x7E)) {
                printable++;
            }
        }
        if (checked > 0 && ((double) printable / checked) > 0.9) {
            return "text/plain";
        }

        return null;
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean startsWithAscii(byte[] data, String ascii) {
        if (data == null || ascii == null) {
            return false;
        }
        byte[] prefix = ascii.getBytes(StandardCharsets.US_ASCII);
        return startsWith(data, prefix);
    }

    private static boolean isJsonWhitespace(byte b) {
        return b == ' ' || b == '\n' || b == '\r' || b == '\t';
    }
}

