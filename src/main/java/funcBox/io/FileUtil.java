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
     * <p><b>Design rules:</b></p>
     * <ul>
     *   <li>No external dependencies (JDK only).</li>
     *   <li>All methods are static; the class cannot be instantiated.</li>
     *   <li>Prefer safe defaults and predictable behavior.</li>
     * </ul>
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
     * <p><b>Guard-rails:</b></p>
     * <ul>
     *   <li>If {@code path} is null/blank =&gt; returns {@code null}</li>
     *   <li>If the resource is missing =&gt; returns {@code null}</li>
     *   <li>Never throws for "not found" (only throws for unexpected IO failures)</li>
     * </ul>
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
     * Write text to a file quickly with no backup or atomicity guarantees.
     *
     * <p>This is a thin wrapper around {@link Files#writeString(Path, CharSequence, java.nio.charset.Charset, java.nio.file.OpenOption...)}.
     * Use this when you need raw write speed and can tolerate data loss on crash.</p>
     *
     * <p>For durability with backup + rollback, use {@link #safeWrite(Path, String)} instead.</p>
     *
     * <p><b>Guard-rails:</b></p>
     * <ul>
     *   <li>{@code path} must not be null (throws {@link IllegalArgumentException}).</li>
     *   <li>Parent directories are created automatically.</li>
     *   <li>{@code content} may be null; it will be written as an empty string.</li>
     * </ul>
     *
     * @param path    destination path
     * @param content content to write (UTF-8); null becomes empty
     * @throws IllegalArgumentException if {@code path} is null
     * @throws RuntimeException         if an IO error occurs
     * @since 1.1.1
     */
    public static void write(Path path, String content) {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        String body = (content == null) ? "" : content;
        Path parent = path.toAbsolutePath().getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, body, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("write failed for path=" + path, e);
        }
    }

    /**
     * Write text to a file atomically with backup and rollback.
     *
     * <p>Use this when data durability matters. For raw performance without
     * durability guarantees, use {@link #write(Path, String)} instead.</p>
     *
     * <p><b>What it guarantees:</b></p>
     * <ul>
     *   <li>Writes to a temporary file in the same directory first.</li>
     *   <li>Moves the temp file into place using an atomic move when supported.</li>
     *   <li>If the target exists, it is backed up before replacement.</li>
     *   <li>If replacement fails after backup, the backup is restored (best-effort rollback).</li>
     * </ul>
     *
     * <p><b>Guard-rails:</b></p>
     * <ul>
     *   <li>{@code path} must not be null (throws {@link IllegalArgumentException}).</li>
     *   <li>Parent directories are created automatically.</li>
     *   <li>{@code content} may be null; it will be written as an empty string.</li>
     * </ul>
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
     * Detect a file's MIME type using a layered strategy.
     *
     * <p><b>How it works:</b></p>
     * <ol>
     *   <li>First checks a lightweight extension hint for common types.</li>
     *   <li>Then tries {@link Files#probeContentType(Path)} (OS / installed file type detectors).</li>
     *   <li>If that returns null, reads the first bytes and detects common "magic numbers".</li>
     * </ol>
     *
     * <p><b>Return rules:</b></p>
     * <ul>
     *   <li>If {@code file} is null/missing/not a file =&gt; returns {@code null}</li>
     *   <li>If the file is valid but the type cannot be detected =&gt; returns {@code "application/octet-stream"}</li>
     * </ul>
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

        // Fast-path 1: try common file extension first (zero IO, O(1))
        String byExtension = detectByExtension(file.getName());
        if (byExtension != null) {
            return byExtension;
        }

        // Fast-path 2: OS-level content type probe
        try {
            String probed = Files.probeContentType(path);
            if (probed != null && !probed.isBlank()) {
                return probed;
            }
        } catch (IOException ignored) {
            // fall through to signature detection
        }

        // Fallback: read magic-number bytes for extension-free or ambiguous files
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

    private static String detectByExtension(String fileName) {
        if (fileName == null) return null;
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) return null;
        switch (fileName.substring(dot + 1).toLowerCase(java.util.Locale.ROOT)) {
            case "png":              return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            case "gif":              return "image/gif";
            case "pdf":              return "application/pdf";
            case "zip":              return "application/zip";
            case "mp3":              return "audio/mpeg";
            case "mp4":              return "video/mp4";
            case "json":             return "application/json";
            case "txt":              return "text/plain";
            case "html": case "htm": return "text/html";
            case "xml":              return "application/xml";
            case "csv":              return "text/csv";
            case "svg":              return "image/svg+xml";
            case "webp":             return "image/webp";
            case "jar": case "war":  return "application/java-archive";
            default:                 return null;  // unknown extension → fall through to byte probe
        }
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
        if (startsWith(header, new byte[]{0x47, 0x49, 0x46, 0x38, 0x37, 0x61}) || 
            startsWith(header, new byte[]{0x47, 0x49, 0x46, 0x38, 0x39, 0x61})) {
            return "image/gif";
        }

        // PDF: %PDF
        if (startsWith(header, new byte[]{0x25, 0x50, 0x44, 0x46})) {
            return "application/pdf";
        }

        // ZIP: PK\x03\x04
        if (startsWith(header, new byte[]{0x50, 0x4B, 0x03, 0x04})) {
            return "application/zip";
        }

        // MP3: ID3
        if (startsWith(header, new byte[]{0x49, 0x44, 0x33})) {
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

