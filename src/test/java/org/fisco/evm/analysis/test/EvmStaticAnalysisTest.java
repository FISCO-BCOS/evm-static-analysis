package org.fisco.evm.analysis.test;

import org.fisco.evm.analysis.EvmAnalyser;
import org.junit.Test;

import java.io.IOException;

public class EvmStaticAnalysisTest {
    @Test
    public void test() throws IOException {
        String abi = "EventSubDemo.abi";
        String bin = "EventSubDemo.bin";
        boolean useSM3 = false;
        boolean armArch = true;

        EvmAnalyser.Result result = EvmAnalyser.process(abi, bin, useSM3, armArch);
        System.out.println("==>> result : " + result);
    }
}
