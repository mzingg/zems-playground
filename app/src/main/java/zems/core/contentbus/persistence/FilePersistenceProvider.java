package zems.core.contentbus.persistence;

import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;
import zems.core.concept.Properties;
import zems.core.utils.ZemsIoUtils;
import zems.core.utils.ZemsJsonUtils;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FilePersistenceProvider implements PersistenceProvider<FilePersistenceProvider> {

    private static final String PROPERTIES_EXTENSION = ".json";
    private static final String PROPERTIES_FILENAME = ".properties" + PROPERTIES_EXTENSION;

    private static final ZemsJsonUtils jsonUtils = new ZemsJsonUtils();

    private final Path contentPath;
    private final Path binaryPath;

    public FilePersistenceProvider(Path contentPath, Path binaryPath) {
        Objects.requireNonNull(contentPath);
        Objects.requireNonNull(binaryPath);

        this.contentPath = contentPath;
        this.binaryPath = binaryPath;
    }

    private static ParsedPath parsePath(String path) {
        final int firstArrow = path.indexOf('>');
        if (firstArrow > 0) {
            List<String> propertyPathParts = Arrays.asList(path.substring(firstArrow + 1).split(":"));
            return new ParsedPath(
              path.substring(0, firstArrow),
              String.join("/", propertyPathParts) + PROPERTIES_EXTENSION
            );
        } else {
            return new ParsedPath(path, PROPERTIES_FILENAME);
        }
    }

    @Override
    public Optional<Content> read(String path) {
        Objects.requireNonNull(path);
        ZemsIoUtils.ensureDirExistsAndIsReadable(contentPath);

        Path propertyPath = parsePath(path).resolveBelow(contentPath);
        if (Files.isRegularFile(propertyPath)) {
            return Optional.of(new Content(path, jsonUtils.fromPath(propertyPath)));
        }

        return Optional.empty();
    }

    @Override
    public Optional<ByteChannel> readBinary(String binaryId) {
        Objects.requireNonNull(binaryId);
        ZemsIoUtils.ensureDirExistsAndIsReadable(binaryPath);

        return Optional.empty();
    }

    @Override
    public FilePersistenceProvider write(Content content) {
        Objects.requireNonNull(content);
        ZemsIoUtils.ensureDirExistsAndIsWritable(contentPath);

        try {
            Path propertyPath = parsePath(content.path())
              .resolveBelow(contentPath);
            ZemsIoUtils.createParentDirectories(propertyPath);
            applyProperties(propertyPath, content.properties());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return this;
    }

    public FilePersistenceProvider initFromJson(String jsonResourcePath) {
        Objects.requireNonNull(jsonResourcePath);
        ZemsIoUtils.ensureDirExistsAndIsWritable(contentPath);

        ZemsIoUtils.cleanDirectory(contentPath);

        final Map<String, Properties> flatten = new ZemsJsonUtils()
          .loadAndFlatten(jsonResourcePath);
        flatten
          .forEach((path, props) -> write(new Content(path, props)));

        return this;
    }

    private void applyProperties(Path path, Properties properties) throws IOException {
        ZemsIoUtils.write(path, jsonUtils.asJsonString(properties));
    }

    private static record ParsedPath(String contentPath, String propertiesSubPath) {
        public Path resolveBelow(Path containerPath) {
            return containerPath
              .resolve("." + contentPath())
              .resolve(propertiesSubPath())
              .normalize();
        }
    }

}
