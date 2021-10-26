package cn.w2n0.genghiskhan.utils.file;


import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 大文件读取
 *
 * @author 无量
 */
public class BigFileReader {
    private int threadPoolSize;
    private Charset charset;
    private int bufferSize;
    private IFileHandle handle;
    private ExecutorService executorService;
    private long fileLength;
    private RandomAccessFile rAccessFile;
    private Set<StartEndPair> startEndPairs;
    private CyclicBarrier cyclicBarrier;
    private AtomicLong counter = new AtomicLong(0);
    public static final char KEY1='\n';
    public static final char KEY2='\r';
    public BigFileReader(File file, IFileHandle handle, Charset charset, int bufferSize, int threadPoolSize) {
        this.fileLength = file.length();
        this.handle = handle;
        this.charset = charset;
        this.bufferSize = bufferSize;
        this.threadPoolSize = threadPoolSize;
        try {
            this.rAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ThreadFactory springThreadFactory = new CustomizableThreadFactory("springThread-pool-shadingFile-");
        this.executorService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),springThreadFactory);
        startEndPairs = new HashSet<>();
    }

    public void start() {
        long everySize = this.fileLength / this.threadPoolSize;
        try {
            calculateStartEnd(0, everySize);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final long startTime = System.currentTimeMillis();
        cyclicBarrier = new CyclicBarrier(startEndPairs.size(), () -> {
            System.out.println("use time: " + (System.currentTimeMillis() - startTime));
            System.out.println("all line: " + counter.get());
            shutdown();
        });
        for (StartEndPair pair : startEndPairs) {
            System.out.println("分配分片：" + pair);
            this.executorService.execute(new SliceReaderTask(pair));
        }
    }

    private void calculateStartEnd(long start, long size) throws IOException {
        if (start > fileLength - 1) {
            return;
        }
        StartEndPair pair = new StartEndPair();
        pair.start = start;
        long endPosition = start + size - 1;
        if (endPosition >= fileLength - 1) {
            pair.end = fileLength - 1;
            startEndPairs.add(pair);
            return;
        }

        rAccessFile.seek(endPosition);
        byte tmp = (byte) rAccessFile.read();
        while (tmp != KEY1 && tmp != KEY2) {
            endPosition++;
            if (endPosition >= fileLength - 1) {
                endPosition = fileLength - 1;
                break;
            }
            rAccessFile.seek(endPosition);
            tmp = (byte) rAccessFile.read();
        }
        pair.end = endPosition;
        startEndPairs.add(pair);

        calculateStartEnd(endPosition + 1, size);

    }

    public void shutdown() {
        try {
            this.rAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.executorService.shutdown();
    }

    private void handle(byte[] bytes) throws UnsupportedEncodingException {
        String line = null;
        if (this.charset == null) {
            line = new String(bytes);
        } else {
            line = new String(bytes, charset);
        }
        if (line != null && !"".equals(line)) {
            this.handle.handle(line);
            counter.incrementAndGet();
        }
    }


    private static class StartEndPair {
        public long start;
        public long end;
        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StartEndPair startEndPair = (StartEndPair) o;
            return start == startEndPair.start &&
                    end == startEndPair.end;
        }

        @Override
        public String toString() {
            return "star=" + start + ";end=" + end;
        }
    }

    private class SliceReaderTask implements Runnable {
        private long start;
        private long sliceSize;
        private byte[] readBuff;

        public SliceReaderTask(StartEndPair pair) {
            this.start = pair.start;
            this.sliceSize = pair.end - pair.start + 1;
            this.readBuff = new byte[bufferSize];
        }


        @Override
        public void run() {
            try {
                int max = Integer.MAX_VALUE - 10000;
                int n = (int) Math.ceil((double) sliceSize / max);
                long subSliceSize = 0;

                for (int c = 0; c < n; c++) {
                    if (sliceSize <= max) {
                        subSliceSize = sliceSize;
                    } else if ((c + 1) * max > sliceSize) {
                        start = start + subSliceSize;
                        subSliceSize = sliceSize - c * max;
                    } else {
                        start = start + subSliceSize;
                        subSliceSize = max;
                    }

                    MappedByteBuffer mapBuffer = rAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, start, subSliceSize);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    for (int offset = 0; offset < subSliceSize; offset += bufferSize) {
                        int readLength;
                        if (offset + bufferSize <= subSliceSize) {
                            readLength = bufferSize;
                        } else {
                            readLength = (int) (subSliceSize - offset);
                        }
                        mapBuffer.get(readBuff, 0, readLength);
                        for (int i = 0; i < readLength; i++) {
                            byte tmp = readBuff[i];
                            //碰到换行符
                            if (tmp == '\n' || tmp == '\r') {
                                handle(bos.toByteArray());
                                bos.reset();
                            } else {
                                bos.write(tmp);
                            }
                        }
                    }
                    if (bos.size() > 0) {
                        handle(bos.toByteArray());
                    }
                }
                cyclicBarrier.await();//测试性能用
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Builder {
        private int threadSize = 1;
        private Charset charset;
        private int bufferSize = 1024 * 1024;
        private IFileHandle handle;
        private File file;

        public Builder(String file, IFileHandle handle) {
            this.file = new File(file);
            if (!this.file.exists()) {
                throw new IllegalArgumentException("文件不存在！");
            }
            this.handle = handle;
        }

        public Builder threadPoolSize(int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("线程池参数必须为大于0的整数");
            }
            this.threadSize = size;
            return this;
        }

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public BigFileReader build() {
            return new BigFileReader(this.file, this.handle, this.charset, this.bufferSize, this.threadSize);
        }
    }

}
