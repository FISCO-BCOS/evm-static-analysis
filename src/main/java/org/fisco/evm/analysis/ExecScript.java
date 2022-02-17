package org.fisco.evm.analysis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecScript {

    private static final Logger logger = LoggerFactory.getLogger(ExecScript.class);

    private File execScript = null;

    public ExecScript() {
        try {
            initDefaultBundled();
        } catch (IOException e) {
            logger.error(" Can't init solc compiler, e: ", e);
            throw new RuntimeException("Can't init solc compiler: ", e);
        }
    }

    private void initDefaultBundled() throws IOException {

        String scriptName = getExecScriptName();
        String scriptPath = getExecScriptPath();

        File tmpDir = new File(System.getProperty("user.home"), ".fisco/");

        if (logger.isDebugEnabled()) {
            logger.debug(
                    " tmpDir: {}, scriptName: {}, scriptPath: {}",
                    tmpDir.getAbsolutePath(),
                    scriptName,
                    scriptPath);
        }

        tmpDir.mkdirs();

        File targetFile = new File(tmpDir, scriptName);
        try (InputStream fis = getClass().getResourceAsStream(getExecScriptPath()); ) {
            Files.copy(fis, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // first file in the list denotes executable
            if (logger.isDebugEnabled()) {
                logger.debug(" exec shell local path: {}", targetFile.getAbsoluteFile());
            }

            execScript = targetFile;
            execScript.setExecutable(true);
        }
    }

    private String getExecScriptName() {
        // evm_static_analysis_linux.sh
        // evm_static_analysis_macOS.sh
        return "evm_static_analysis_" + getOS() + ".sh";
    }

    private String getExecScriptPath() {
        // evm_static_analysis_linux.sh
        // evm_static_analysis_macOS.sh
        return "/native/" + "evm_static_analysis_" + getOS() + ".sh";
    }

    private String getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux")) {
            return "linux";
        } else if (osName.contains("mac")) {
            return "macOS";
        } else {
            throw new RuntimeException("Unrecognized OS: " + osName);
        }
    }

    public File getExecScript() {
        return execScript;
    }
}
