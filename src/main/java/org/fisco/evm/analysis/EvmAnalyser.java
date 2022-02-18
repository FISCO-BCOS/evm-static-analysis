package org.fisco.evm.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvmAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(EvmAnalyser.class);

    /** singleton object */
    private static EvmAnalyser INSTANCE;
    /** evm solidity analysis */
    private ExecScript executableScript = new ExecScript();

    /**
     * @param abiFile
     * @param binFile
     * @param useSM3
     * @return
     */
    public static Result process(String abiFile, String binFile, boolean useSM3)
            throws IOException {
        return getInstance().processAbiAndBin(abiFile, binFile, useSM3);
    }

    public static class Result {

        private String errors;
        private String output;
        private boolean success;

        public Result(String errors, String output, boolean success) {
            this.errors = errors;
            this.output = output;
            this.success = success;
        }

        public boolean isFailed() {
            return !success;
        }

        public String getErrors() {
            return errors;
        }

        public void setErrors(String errors) {
            this.errors = errors;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }
    }

    private static class ParallelReader extends Thread {

        private InputStream stream;
        private StringBuilder content = new StringBuilder();

        ParallelReader(InputStream stream) {
            this.stream = stream;
        }

        public String getContent() {
            return getContent(true);
        }

        public synchronized String getContent(boolean waitForComplete) {
            if (waitForComplete) {
                while (stream != null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
            }
            return content.toString();
        }

        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                synchronized (this) {
                    stream = null;
                    notifyAll();
                }
            }
        }
    }

    private Result processAbiAndBin(String abiFile, String binFile, boolean useSM3)
            throws IOException {

        ExecScript executableScript = getInstance().getExecutableScript();
        List<String> commandParts = new ArrayList<>();
        commandParts.add(executableScript.getExecScript().getCanonicalPath());
        commandParts.add("-a " + abiFile);
        commandParts.add("-b " + binFile);
        if (useSM3) {
            commandParts.add("-g");
        }

        ProcessBuilder processBuilder =
                new ProcessBuilder(commandParts)
                        .directory(executableScript.getExecScript().getParentFile());
        processBuilder
                .environment()
                .put(
                        "LD_LIBRARY_PATH",
                        executableScript.getExecScript().getParentFile().getCanonicalPath());

        Process process = processBuilder.start();

        ParallelReader error = new ParallelReader(process.getErrorStream());
        ParallelReader output = new ParallelReader(process.getInputStream());
        error.start();
        output.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        boolean success = process.exitValue() == 0;

        return new Result(error.getContent(), output.getContent(), success);
    }

    public ExecScript getExecutableScript() {
        return executableScript;
    }

    public static EvmAnalyser getInstance() {
        if (INSTANCE == null) {
            synchronized (EvmAnalyser.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EvmAnalyser();
                }
            }
        }
        return INSTANCE;
    }
}
