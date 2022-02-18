package org.fisco.evm.analysis.test;

import java.io.IOException;
import org.fisco.evm.analysis.EvmAnalyser;
import org.junit.Test;

public class EvmStaticAnalysisTest {
    @Test
    public void test() throws IOException {
        String abi = "EventSubDemo.abi";
        String bin = "EventSubDemo.bin";
        boolean useSM3 = false;

        EvmAnalyser.Result result = EvmAnalyser.process(abi, bin, useSM3);
        System.out.println("==>> result : " + result);
    }
}
