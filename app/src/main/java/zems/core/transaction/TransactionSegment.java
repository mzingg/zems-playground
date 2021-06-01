package zems.core.transaction;

import org.graalvm.collections.Pair;
import zems.core.contentbus.Packable;
import zems.core.contentbus.Properties;

import java.nio.ByteBuffer;
import java.util.Objects;

import static zems.core.utils.ZemsIoUtils.packString;
import static zems.core.utils.ZemsIoUtils.unpackString;

public class TransactionSegment implements Packable<TransactionSegment> {

  private String path;
  private long sequenceId;
  private Properties data;

  public TransactionSegment() {
    this.path = "";
    this.sequenceId = 0;
    this.data = new Properties();
  }

  public String getPath() {
    return path;
  }

  public TransactionSegment setPath(String path) {
    Objects.requireNonNull(path);

    this.path = path;
    return this;
  }

  public long getSequenceId() {
    return sequenceId;
  }

  public TransactionSegment setSequenceId(long sequenceId) {
    this.sequenceId = sequenceId;
    return this;
  }

  public Properties getData() {
    return data;
  }

  public TransactionSegment setData(Properties data) {
    Objects.requireNonNull(data);

    this.data = data;
    return this;
  }

  @Override
  public void pack(ByteBuffer buffer) {
    Objects.requireNonNull(buffer);

    buffer.putInt(packSize());
    buffer.putLong(getSequenceId());
    packString(getPath(), buffer);
    data.pack(buffer);
  }

  @Override
  public TransactionSegment unpack(ByteBuffer buffer) {
    Objects.requireNonNull(buffer);

    int segmentLength = buffer.getInt();
    long sequenceId = buffer.getLong();
    Pair<String, Integer> segmentPath = unpackString(buffer);
    Properties data = new Properties().unpack(buffer);

    TransactionSegment result = new TransactionSegment();
    result.sequenceId = sequenceId;
    result.path = segmentPath.getLeft();
    result.data = data;

    return result;
  }

  @Override
  public int packSize() {
    return Integer.BYTES //segmentLength
        + Long.BYTES  // sequenceId
        + Integer.BYTES // length of path
        + path.length() * Character.BYTES // characters of path
        + data.packSize(); // and the data
  }

}
