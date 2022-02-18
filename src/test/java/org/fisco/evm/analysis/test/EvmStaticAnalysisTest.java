package org.fisco.evm.analysis.test;

import java.io.IOException;
import org.fisco.evm.analysis.EvmAnalyser;
import org.junit.Test;

public class EvmStaticAnalysisTest {
    @Test
    public void test() throws IOException {
        String abi = getClass().getClassLoader().getResource("solidity/SimpleEIP20.abi").getPath();
        String bin = getClass().getClassLoader().getResource("solidity/SimpleEIP20.bin").getPath();
        System.out.println("==>> abi : " + abi);
        System.out.println("==>> bin : " + bin);

        boolean useSM3 = false;

        EvmAnalyser.Result result = EvmAnalyser.process(abi, bin, useSM3);
        System.out.println("==>> failed : " + result.isFailed());
        System.out.println("==>> error : " + result.getErrors());
        System.out.println("==>> result : " + result.getOutput());
    }
}
