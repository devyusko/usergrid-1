package org.apache.usergrid;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;

public class AbstractBenchmarkIT extends AbstractCoreIT {
    private static final Logger LOG = LoggerFactory.getLogger( AbstractBenchmarkIT.class );

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();
    
}
