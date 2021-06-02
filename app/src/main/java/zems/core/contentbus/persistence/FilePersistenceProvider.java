package zems.core.contentbus.persistence;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import zems.core.concept.Content;
import zems.core.concept.PersistenceProvider;
import zems.core.concept.Properties;
import zems.core.utils.ZemsJsonUtils;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class FilePersistenceProvider implements PersistenceProvider<FilePersistenceProvider> {

  private static final String PROPERTIES_FILENAME = ".properties.json";
  private final Path containerDirectory;
  private final ZemsJsonUtils jsonUtils = new ZemsJsonUtils().withOwnObjectMapper();

  public FilePersistenceProvider(Path containerDirectory) {
    Objects.requireNonNull(containerDirectory);
    this.containerDirectory = containerDirectory;
  }

  @Override
  public Optional<Content> read(String path) {
    ensureContainerDirExistsAndIsReadable();
    Path contentPath = containerDirectory.resolve("." + path);
    Path propertyPath = contentPath.resolve(PROPERTIES_FILENAME);

    if (PathUtils.isRegularFile(propertyPath)) {
      return Optional.of(new Content(path, jsonUtils.fromPath(propertyPath)));
    }

    return Optional.empty();
  }

  @Override
  public Optional<ByteChannel> readBinary(String contentId) {
    return Optional.empty();
  }

  @Override
  public FilePersistenceProvider write(Content content) {
    ensureContainerDirExistsAndIsWritable();
    try {
      Path propertyPath = containerDirectory.resolve("." + content.path()).resolve(PROPERTIES_FILENAME);
      PathUtils.createParentDirectories(propertyPath);
      applyProperties(propertyPath, content.properties());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
    return this;
  }

  private void applyProperties(Path path, Properties properties) throws IOException {
    FileUtils.write(path.toFile(), jsonUtils.asJsonString(properties), StandardCharsets.UTF_8);
  }

  private void ensureContainerDirExistsAndIsWritable() {
    if (!Files.isDirectory(containerDirectory) && !Files.isWritable(containerDirectory)) {
      throw new IllegalStateException("containerDirecotry " + containerDirectory + " is not writable");
    }
  }

  private void ensureContainerDirExistsAndIsReadable() {
    if (!Files.isDirectory(containerDirectory) && !Files.isReadable(containerDirectory)) {
      throw new IllegalStateException("containerDirecotry " + containerDirectory + " is not readable");
    }
  }
}