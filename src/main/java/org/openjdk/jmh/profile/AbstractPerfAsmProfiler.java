//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.openjdk.jmh.profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.results.AggregationPolicy;
import org.openjdk.jmh.results.Aggregator;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.BenchmarkResultMetaData;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.ResultRole;
import org.openjdk.jmh.util.FileUtils;
import org.openjdk.jmh.util.HashMultimap;
import org.openjdk.jmh.util.HashMultiset;
import org.openjdk.jmh.util.Multiset;
import org.openjdk.jmh.util.Multisets;
import org.openjdk.jmh.util.Utils;

public abstract class AbstractPerfAsmProfiler implements ExternalProfiler {
    protected final List<String> events;
    private final double regionRateThreshold;
    private final int regionShowTop;
    private final int regionTooBigThreshold;
    private final int printMargin;
    private final int mergeMargin;
    private final int delayMsec;
    private final boolean skipAssembly;
    private final boolean skipInterpreter;
    private final boolean skipVMStubs;
    private final boolean savePerfOutput;
    private final String savePerfOutputTo;
    private final String savePerfOutputToFile;
    private final boolean savePerfBin;
    private final String savePerfBinTo;
    private final String savePerfBinFile;
    private final boolean saveLog;
    private final String saveLogTo;
    private final String saveLogToFile;
    private final boolean printCompilationInfo;
    private final boolean intelSyntax;
    protected final String hsLog;
    protected final String perfBinData;
    protected final String perfParsedData;
    protected final OptionSet set;
    private final boolean drawIntraJumps;
    private final boolean drawInterJumps;

    protected AbstractPerfAsmProfiler(String initLine, String... events) throws ProfilerException {
        try {
            this.hsLog = FileUtils.tempFile("hslog").getAbsolutePath();
            this.perfBinData = FileUtils.tempFile("perfbin").getAbsolutePath();
            this.perfParsedData = FileUtils.tempFile("perfparsed").getAbsolutePath();
        } catch (IOException var29) {
            throw new ProfilerException(var29);
        }

        /*

  branch-instructions OR branches                    [Hardware event]
  branch-misses                                      [Hardware event]
  bus-cycles                                         [Hardware event]
  cache-misses                                       [Hardware event]
  cache-references                                   [Hardware event]
  cpu-cycles OR cycles                               [Hardware event]
  instructions                                       [Hardware event]
  ref-cycles                                         [Hardware event]

  alignment-faults                                   [Software event]
  bpf-output                                         [Software event]
  context-switches OR cs                             [Software event]
  cpu-clock                                          [Software event]
  cpu-migrations OR migrations                       [Software event]
  dummy                                              [Software event]
  emulation-faults                                   [Software event]
  major-faults                                       [Software event]
  minor-faults                                       [Software event]
  page-faults OR faults                              [Software event]
  task-clock                                         [Software event]

  L1-dcache-load-misses                              [Hardware cache event]
  L1-dcache-loads                                    [Hardware cache event]
  L1-dcache-stores                                   [Hardware cache event]
  L1-icache-load-misses                              [Hardware cache event]
  LLC-load-misses                                    [Hardware cache event]
  LLC-loads                                          [Hardware cache event]
  LLC-store-misses                                   [Hardware cache event]
  LLC-stores                                         [Hardware cache event]
  branch-load-misses                                 [Hardware cache event]
  branch-loads                                       [Hardware cache event]
  dTLB-load-misses                                   [Hardware cache event]
  dTLB-loads                                         [Hardware cache event]
  dTLB-store-misses                                  [Hardware cache event]
  dTLB-stores                                        [Hardware cache event]
  iTLB-load-misses                                   [Hardware cache event]
  iTLB-loads                                         [Hardware cache event]
  node-load-misses                                   [Hardware cache event]
  node-loads                                         [Hardware cache event]
  node-store-misses                                  [Hardware cache event]
  node-stores                                        [Hardware cache event]

  branch-instructions OR cpu/branch-instructions/    [Kernel PMU event]
  branch-misses OR cpu/branch-misses/                [Kernel PMU event]
  bus-cycles OR cpu/bus-cycles/                      [Kernel PMU event]
  cache-misses OR cpu/cache-misses/                  [Kernel PMU event]
  cache-references OR cpu/cache-references/          [Kernel PMU event]
  cpu-cycles OR cpu/cpu-cycles/                      [Kernel PMU event]
  cstate_core/c3-residency/                          [Kernel PMU event]
  cstate_core/c6-residency/                          [Kernel PMU event]
  cstate_core/c7-residency/                          [Kernel PMU event]
  cstate_pkg/c2-residency/                           [Kernel PMU event]
  cstate_pkg/c3-residency/                           [Kernel PMU event]
  cstate_pkg/c6-residency/                           [Kernel PMU event]
  cstate_pkg/c7-residency/                           [Kernel PMU event]
  cycles-ct OR cpu/cycles-ct/                        [Kernel PMU event]
  cycles-t OR cpu/cycles-t/                          [Kernel PMU event]
  el-abort OR cpu/el-abort/                          [Kernel PMU event]

        */

        events = new String[] {
                "cycles",
                "instructions",
                "cache-references",
                "cache-misses",
                "bus-cycles",
                "L1-dcache-loads",
                "L1-dcache-load-misses",
                "L1-dcache-stores",
                "dTLB-loads",
                "dTLB-load-misses",
                "LLC-loads",
                "LLC-load-misses",
                "LLC-stores",
                "branches",
                "branch-misses",
                "context-switches",
                "page-faults"
        };

        OptionParser parser = new OptionParser();
        parser.formatHelpWith(new ProfilerOptionFormatter("perfasm"));
        ArgumentAcceptingOptionSpec optEvents = parser.accepts("events", "Events to gather.").
                withRequiredArg().ofType(String.class).withValuesSeparatedBy(",").describedAs("event").defaultsTo(events);
        ArgumentAcceptingOptionSpec optThresholdRate = parser.accepts("hotThreshold", "Cutoff threshold for hot regions. The regions with event count over threshold would be expanded with detailed disassembly.").withRequiredArg().ofType(Double.class).describedAs("rate").defaultsTo(Double.valueOf(0.1D), new Double[0]);
        ArgumentAcceptingOptionSpec optShowTop = parser.accepts("top", "Show this number of top hottest code regions.").withRequiredArg().ofType(Integer.class).describedAs("#").defaultsTo(Integer.valueOf(20), new Integer[0]);
        ArgumentAcceptingOptionSpec optThreshold = parser.accepts("tooBigThreshold", "Cutoff threshold for large region. The region containing more than this number of lines would be truncated.").withRequiredArg().ofType(Integer.class).describedAs("lines").defaultsTo(Integer.valueOf(1000), new Integer[0]);
        ArgumentAcceptingOptionSpec optPrintMargin = parser.accepts("printMargin", "Print margin. How many \"context\" lines without counters to show in each region.").withRequiredArg().ofType(Integer.class).describedAs("lines").defaultsTo(Integer.valueOf(10), new Integer[0]);
        ArgumentAcceptingOptionSpec optMergeMargin = parser.accepts("mergeMargin", "Merge margin. The regions separated by less than the margin are merged.").withRequiredArg().ofType(Integer.class).describedAs("lines").defaultsTo(Integer.valueOf(32), new Integer[0]);
        ArgumentAcceptingOptionSpec optDelay = parser.accepts("delay", "Delay collection for a given time, in milliseconds; -1 to detect automatically.").withRequiredArg().ofType(Integer.class).describedAs("ms").defaultsTo(Integer.valueOf(-1), new Integer[0]);
        ArgumentAcceptingOptionSpec optSkipAsm = parser.accepts("skipAsm", "Skip -XX:+PrintAssembly instrumentation.").withRequiredArg().ofType(Boolean.class).describedAs("bool").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        ArgumentAcceptingOptionSpec optSkipInterpreter = parser.accepts("skipInterpreter", "Skip printing out interpreter stubs. This may improve the parser performance at the expense of missing the resolution and disassembly of interpreter regions.").withRequiredArg().ofType(Boolean.class).describedAs("bool").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        ArgumentAcceptingOptionSpec optSkipVMStubs = parser.accepts("skipVMStubs", "Skip printing out VM stubs. This may improve the parser performance at the expense of missing the resolution and disassembly of VM stub regions.").withRequiredArg().ofType(Boolean.class).describedAs("bool").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        ArgumentAcceptingOptionSpec optPerfOut = parser.accepts("savePerf", "Save parsed perf output to file. Use this for debugging.").withRequiredArg().ofType(Boolean.class).describedAs("bool").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        ArgumentAcceptingOptionSpec optPerfOutTo = parser.accepts("savePerfTo", "Override the parsed perf output log location. This will use the unique file name per test. Use this for debugging.").withRequiredArg().ofType(String.class).describedAs("dir").defaultsTo(".", new String[0]);
        ArgumentAcceptingOptionSpec optPerfOutToFile = parser.accepts("savePerfToFile", "Override the perf output log filename. Use this for debugging.").withRequiredArg().ofType(String.class).describedAs("file");
        ArgumentAcceptingOptionSpec optPerfBin = parser.accepts("savePerfBin", "Save binary perf data to file. Use this for debugging.").withRequiredArg().ofType(Boolean.class).describedAs("bool").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        ArgumentAcceptingOptionSpec optPerfBinTo = parser.accepts("savePerfBinTo", "Override the binary perf data location. This will use the unique file name per test. Use this for debugging.").withRequiredArg().ofType(String.class).describedAs("dir").defaultsTo(".", new String[0]);
        ArgumentAcceptingOptionSpec optPerfBinToFile = parser.accepts("savePerfBinToFile", "Override the perf binary data filename. Use this for debugging.").withRequiredArg().ofType(String.class).describedAs("file");
        ArgumentAcceptingOptionSpec optSaveLog = parser.accepts("saveLog", "Save annotated Hotspot log to file.").withRequiredArg().ofType(Boolean.class).describedAs("bool").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        ArgumentAcceptingOptionSpec optSaveLogTo = parser.accepts("saveLogTo", "Override the annotated Hotspot log location. This will use the unique file name per test.").withRequiredArg().ofType(String.class).describedAs("dir").defaultsTo(".", new String[0]);
        ArgumentAcceptingOptionSpec optSaveLogToFile = parser.accepts("saveLogToFile", "Override the annotated Hotspot log filename.").withRequiredArg().ofType(String.class).describedAs("file");
        ArgumentAcceptingOptionSpec optPrintCompilationInfo = parser.accepts("printCompilationInfo", "Print the collateral compilation information. Enabling this might corrupt the assembly output, see https://bugs.openjdk.java.net/browse/CODETOOLS-7901102.").withRequiredArg().ofType(Boolean.class).describedAs("bool").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        ArgumentAcceptingOptionSpec optIntelSyntax = parser.accepts("intelSyntax", "Should perfasm use intel syntax?").withRequiredArg().ofType(Boolean.class).describedAs("boolean").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        ArgumentAcceptingOptionSpec optDrawIntraJumps = parser.accepts("drawIntraJumps", "Should perfasm draw jump arrows with the region?").withRequiredArg().ofType(Boolean.class).describedAs("boolean").defaultsTo(Boolean.valueOf(true), new Boolean[0]);
        ArgumentAcceptingOptionSpec optDrawInterJumps = parser.accepts("drawInterJumps", "Should perfasm draw jump arrows out of the region?").withRequiredArg().ofType(Boolean.class).describedAs("boolean").defaultsTo(Boolean.valueOf(false), new Boolean[0]);
        this.addMyOptions(parser);
        this.set = ProfilerUtils.parseInitLine(initLine, parser);

        try {
            this.events = this.set.valuesOf(optEvents);

            this.regionRateThreshold = ((Double)this.set.valueOf(optThresholdRate)).doubleValue();
            this.regionShowTop = ((Integer)this.set.valueOf(optShowTop)).intValue();
            this.regionTooBigThreshold = ((Integer)this.set.valueOf(optThreshold)).intValue();
            this.printMargin = ((Integer)this.set.valueOf(optPrintMargin)).intValue();
            this.mergeMargin = ((Integer)this.set.valueOf(optMergeMargin)).intValue();
            this.delayMsec = ((Integer)this.set.valueOf(optDelay)).intValue();
            this.skipAssembly = ((Boolean)this.set.valueOf(optSkipAsm)).booleanValue();
            this.skipInterpreter = ((Boolean)this.set.valueOf(optSkipInterpreter)).booleanValue();
            this.skipVMStubs = ((Boolean)this.set.valueOf(optSkipVMStubs)).booleanValue();
            this.savePerfOutput = ((Boolean)this.set.valueOf(optPerfOut)).booleanValue();
            this.savePerfOutputTo = (String)this.set.valueOf(optPerfOutTo);
            this.savePerfOutputToFile = (String)this.set.valueOf(optPerfOutToFile);
            this.savePerfBin = ((Boolean)this.set.valueOf(optPerfBin)).booleanValue();
            this.savePerfBinTo = (String)this.set.valueOf(optPerfBinTo);
            this.savePerfBinFile = (String)this.set.valueOf(optPerfBinToFile);
            this.saveLog = ((Boolean)this.set.valueOf(optSaveLog)).booleanValue();
            this.saveLogTo = (String)this.set.valueOf(optSaveLogTo);
            this.saveLogToFile = (String)this.set.valueOf(optSaveLogToFile);
            this.intelSyntax = ((Boolean)this.set.valueOf(optIntelSyntax)).booleanValue();
            this.printCompilationInfo = ((Boolean)this.set.valueOf(optPrintCompilationInfo)).booleanValue();
            this.drawIntraJumps = ((Boolean)this.set.valueOf(optDrawInterJumps)).booleanValue();
            this.drawInterJumps = ((Boolean)this.set.valueOf(optDrawIntraJumps)).booleanValue();
        } catch (OptionException var28) {
            throw new ProfilerException(var28.getMessage());
        }
    }

    protected abstract void addMyOptions(OptionParser var1);

    public Collection<String> addJVMOptions(BenchmarkParams params) {
        if(!this.skipAssembly) {
            ArrayList opts = new ArrayList();
            opts.addAll(Arrays.asList(new String[]{"-XX:+UnlockDiagnosticVMOptions", "-XX:+LogCompilation", "-XX:LogFile=" + this.hsLog, "-XX:+PrintAssembly"}));
            if(!this.skipInterpreter) {
                opts.add("-XX:+PrintInterpreter");
            }

            if(!this.skipVMStubs) {
                opts.add("-XX:+PrintNMethods");
                opts.add("-XX:+PrintNativeNMethods");
                opts.add("-XX:+PrintSignatureHandlers");
                opts.add("-XX:+PrintAdapterHandlers");
                opts.add("-XX:+PrintStubCode");
            }

            if(this.printCompilationInfo) {
                opts.add("-XX:+PrintCompilation");
                opts.add("-XX:+PrintInlining");
                opts.add("-XX:+TraceClassLoading");
            }

            if(this.intelSyntax) {
                opts.add("-XX:PrintAssemblyOptions=intel");
            }

            return opts;
        } else {
            return Collections.emptyList();
        }
    }

    public void beforeTrial(BenchmarkParams params) {
    }

    public Collection<? extends Result> afterTrial(BenchmarkResult br, long pid, File stdOut, File stdErr) {
        AbstractPerfAsmProfiler.PerfResult result = this.processAssembly(br, stdOut, stdErr);
        return Collections.singleton(result);
    }

    public boolean allowPrintOut() {
        return false;
    }

    public boolean allowPrintErr() {
        return false;
    }

    protected abstract void parseEvents();

    protected abstract AbstractPerfAsmProfiler.PerfEvents readEvents(double var1);

    protected abstract String perfBinaryExtension();

    private AbstractPerfAsmProfiler.PerfResult processAssembly(BenchmarkResult br, File stdOut, File stdErr) {
        this.parseEvents();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        AbstractPerfAsmProfiler.Assembly assembly = this.readAssembly(new File(this.hsLog));
        if(assembly.size() > 0) {
            pw.printf("PrintAssembly processed: %d total address lines.%n", new Object[]{Integer.valueOf(assembly.size())});
        } else if(this.skipAssembly) {
            pw.println();
            pw.println("PrintAssembly skipped, Java methods are not resolved.");
            pw.println();
        } else {
            pw.println();
            pw.println("ERROR: No address lines detected in assembly capture, make sure your JDK is PrintAssembly-enabled:\n    https://wiki.openjdk.java.net/display/HotSpot/PrintAssembly");
            pw.println();
        }

        long delayNs;
        if(this.delayMsec == -1) {
            BenchmarkResultMetaData skipSec = br.getMetadata();
            if(skipSec != null) {
                delayNs = TimeUnit.MILLISECONDS.toNanos(skipSec.getMeasurementTime() - skipSec.getStartTime());
            } else {
                IterationParams wp = br.getParams().getWarmup();
                delayNs = (long)wp.getCount() * wp.getTime().convertTo(TimeUnit.NANOSECONDS) + TimeUnit.SECONDS.toNanos(1L);
            }
        } else {
            delayNs = TimeUnit.MILLISECONDS.toNanos((long)this.delayMsec);
        }

        double var31 = 1.0D * (double)delayNs / (double)TimeUnit.SECONDS.toNanos(1L);
        final AbstractPerfAsmProfiler.PerfEvents events = this.readEvents(var31);
        if(!events.isEmpty()) {
            pw.printf("Perf output processed (skipped %.3f seconds):%n", new Object[]{Double.valueOf(var31)});
            int regions = 1;

            for(Iterator mainEvent = this.events.iterator(); mainEvent.hasNext(); ++regions) {
                String threshold = (String)mainEvent.next();
                pw.printf(" Column %d: %s (%d events)%n", new Object[]{Integer.valueOf(regions), threshold, Long.valueOf(events.get(threshold).size())});
            }

            pw.println();
        } else {
            pw.println();
            pw.println("ERROR: No perf data, make sure \"perf stat echo 1\" is indeed working;\n or the collection delay is not running past the benchmark time.");
            pw.println();
        }

        List<AbstractPerfAsmProfiler.Region> var32 = this.makeRegions(assembly, events);
        final String var33 = (String)this.events.get(0);
        Collections.sort(var32, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return Long.valueOf(((AbstractPerfAsmProfiler.Region)o2).getEventCount(events, var33)).compareTo(Long.valueOf(((AbstractPerfAsmProfiler.Region)o1).getEventCount(events, var33)));
            }
        });
        long var34 = (long)(this.regionRateThreshold * (double)events.getTotalEvents(var33).longValue());
        boolean headerPrinted = false;
        int cnt = 1;
        Iterator methodsByType = var32.iterator();

        while(true) {
            AbstractPerfAsmProfiler.Region target;
            Iterator asm;
            String e;
            do {
                if(!methodsByType.hasNext()) {
                    if(!headerPrinted) {
                        pw.printf("WARNING: No hottest code region above the threshold (%.2f%%) for disassembly.%n", new Object[]{Double.valueOf(this.regionRateThreshold * 100.0D)});
                        pw.println("Use \"hotThreshold\" profiler option to lower the filter threshold.");
                        pw.println();
                    }

                    HashMultiset var35 = new HashMultiset();
                    HashMultiset var37 = new HashMultiset();
                    this.printDottedLine(pw, "Hottest Regions");
                    int var40 = 0;
                    Iterator var43 = var32.iterator();

                    while(var43.hasNext()) {
                        AbstractPerfAsmProfiler.Region event = (AbstractPerfAsmProfiler.Region)var43.next();
                        Iterator line;
                        String count;
                        if(var40++ >= this.regionShowTop) {
                            line = this.events.iterator();

                            while(line.hasNext()) {
                                count = (String)line.next();
                                var37.add(count, event.getEventCount(events, count));
                            }
                        } else {
                            line = this.events.iterator();

                            while(line.hasNext()) {
                                count = (String)line.next();
                                printLine(pw, events, count, event.getEventCount(events, count));
                            }

                            pw.printf("[0x%x:0x%x] in %s%n", new Object[]{Long.valueOf(event.begin), Long.valueOf(event.end), event.getName()});
                        }

                        line = this.events.iterator();

                        while(line.hasNext()) {
                            count = (String)line.next();
                            var35.add(count, event.getEventCount(events, count));
                        }
                    }

                    String var47;
                    if(var32.size() - this.regionShowTop > 0) {
                        var43 = this.events.iterator();

                        while(var43.hasNext()) {
                            var47 = (String)var43.next();
                            printLine(pw, events, var47, var37.count(var47));
                        }

                        pw.println("<...other " + (var32.size() - this.regionShowTop) + " warm regions...>");
                    }

                    this.printDottedLine(pw);
                    var43 = this.events.iterator();

                    while(var43.hasNext()) {
                        var47 = (String)var43.next();
                        printLine(pw, events, var47, var35.count(var47));
                    }

                    pw.println("<totals>");
                    pw.println();
                    HashMap var36 = new HashMap();
                    Iterator var38 = this.events.iterator();

                    String var41;
                    while(var38.hasNext()) {
                        var41 = (String)var38.next();
                        var36.put(var41, new HashMultiset());
                    }

                    this.printDottedLine(pw, "Hottest Methods (after inlining)");
                    HashMap var39 = new HashMap();
                    asm = this.events.iterator();

                    while(asm.hasNext()) {
                        e = (String)asm.next();
                        var39.put(e, new HashMultiset());
                    }

                    asm = var32.iterator();

                    Iterator var50;
                    while(asm.hasNext()) {
                        AbstractPerfAsmProfiler.Region var48 = (AbstractPerfAsmProfiler.Region)asm.next();
                        var50 = this.events.iterator();

                        while(var50.hasNext()) {
                            String var54 = (String)var50.next();
                            long var58 = var48.getEventCount(events, var54);
                            ((Multiset)var39.get(var54)).add(var48.getName(), var58);
                            ((Multiset)var36.get(var54)).add(var48.getType(), var58);
                        }
                    }

                    HashMultiset var45 = new HashMultiset();
                    HashMultiset var49 = new HashMultiset();
                    int var51 = 0;
                    List var55 = Multisets.sortedDesc((Multiset)var39.get(var33));
                    Iterator var59 = var55.iterator();

                    String event1;
                    while(var59.hasNext()) {
                        event1 = (String)var59.next();
                        Iterator count1;
                        String event2;
                        if(var51++ >= this.regionShowTop) {
                            count1 = this.events.iterator();

                            while(count1.hasNext()) {
                                event2 = (String)count1.next();
                                var49.add(event2, ((Multiset)var39.get(event2)).count(event1));
                            }
                        } else {
                            count1 = this.events.iterator();

                            while(count1.hasNext()) {
                                event2 = (String)count1.next();
                                printLine(pw, events, event2, ((Multiset)var39.get(event2)).count(event1));
                            }

                            pw.printf("%s%n", new Object[]{event1});
                        }

                        count1 = this.events.iterator();

                        while(count1.hasNext()) {
                            event2 = (String)count1.next();
                            var45.add(event2, ((Multiset)var39.get(event2)).count(event1));
                        }
                    }

                    if(var55.size() - this.regionShowTop > 0) {
                        var59 = this.events.iterator();

                        while(var59.hasNext()) {
                            event1 = (String)var59.next();
                            printLine(pw, events, event1, var49.count(event1));
                        }

                        pw.println("<...other " + (var55.size() - this.regionShowTop) + " warm methods...>");
                    }

                    this.printDottedLine(pw);
                    var59 = this.events.iterator();

                    while(var59.hasNext()) {
                        event1 = (String)var59.next();
                        printLine(pw, events, event1, var45.count(event1));
                    }

                    pw.println("<totals>");
                    pw.println();
                    this.printDottedLine(pw, "Distribution by Area");
                    var38 = Multisets.sortedDesc((Multiset)var36.get(var33)).iterator();

                    while(var38.hasNext()) {
                        var41 = (String)var38.next();
                        var43 = this.events.iterator();

                        while(var43.hasNext()) {
                            var47 = (String)var43.next();
                            printLine(pw, events, var47, ((Multiset)var36.get(var47)).count(var41));
                        }

                        pw.printf("%s%n", new Object[]{var41});
                    }

                    this.printDottedLine(pw);
                    var38 = this.events.iterator();

                    while(var38.hasNext()) {
                        var41 = (String)var38.next();
                        printLine(pw, events, var41, ((Multiset)var36.get(var41)).size());
                    }

                    pw.println("<totals>");
                    pw.println();
                    HashSet var42 = new HashSet();
                    asm = assembly.addressMap.keySet().iterator();

                    Long var52;
                    while(asm.hasNext()) {
                        var52 = (Long)asm.next();
                        if(!var42.add(var52)) {
                            pw.println("WARNING: Duplicate instruction addresses detected. This is probably due to compiler reusing\n the code arena for the new generated code. We can not differentiate between methods sharing\nthe same addresses, and therefore the profile might be wrong. Increasing generated code\nstorage might help.");
                        }
                    }

                    int var44 = 0;

                    for(asm = events.totalCounts.values().iterator(); asm.hasNext(); var44 = (int)((long)var44 + var52.longValue())) {
                        var52 = (Long)asm.next();
                    }

                    if(var44 < 1000) {
                        pw.println("WARNING: The perf event count is suspiciously low (" + var44 + "). The performance data might be\n" + "inaccurate or misleading. Try to do the profiling again, or tune up the sampling frequency.");
                    }

                    String var46;
                    if(this.savePerfOutput) {
                        var46 = this.savePerfOutputToFile == null?this.savePerfOutputTo + "/" + br.getParams().id() + ".perf":this.savePerfOutputToFile;

                        try {
                            FileUtils.copy(this.perfParsedData, var46);
                            pw.println("Perf output saved to " + var46);
                        } catch (IOException var29) {
                            pw.println("Unable to save perf output to " + var46);
                        }
                    }

                    if(this.savePerfBin) {
                        var46 = this.savePerfBinFile == null?this.savePerfBinTo + "/" + br.getParams().id() + this.perfBinaryExtension():this.savePerfBinFile;

                        try {
                            FileUtils.copy(this.perfBinData, var46);
                            pw.println("Perf binary output saved to " + var46);
                        } catch (IOException var28) {
                            pw.println("Unable to save perf binary output to " + var46);
                        }
                    }

                    if(this.saveLog) {
                        var46 = this.saveLogToFile == null?this.saveLogTo + "/" + br.getParams().id() + ".log":this.saveLogToFile;

                        try {
                            FileOutputStream var53 = new FileOutputStream(var46);
                            PrintWriter var56 = new PrintWriter(var53);
                            var50 = assembly.lines.iterator();

                            while(var50.hasNext()) {
                                AbstractPerfAsmProfiler.ASMLine var57 = (AbstractPerfAsmProfiler.ASMLine)var50.next();
                                var59 = this.events.iterator();

                                while(var59.hasNext()) {
                                    event1 = (String)var59.next();
                                    long var60 = var57.addr != null?events.get(event1).count(var57.addr):0L;
                                    printLine(var56, events, event1, var60);
                                }

                                var56.println(var57.code);
                            }

                            var56.flush();
                            FileUtils.safelyClose(var53);
                            pw.println("Perf-annotated Hotspot log is saved to " + var46);
                        } catch (IOException var30) {
                            pw.println("Unable to save Hotspot log to " + var46);
                        }
                    }

                    pw.flush();
                    pw.close();
                    return new AbstractPerfAsmProfiler.PerfResult(sw.toString());
                }

                target = (AbstractPerfAsmProfiler.Region)methodsByType.next();
            } while(target.getEventCount(events, var33) <= var34);

            if(!headerPrinted) {
                pw.printf("Hottest code regions (>%.2f%% \"%s\" events):%n", new Object[]{Double.valueOf(this.regionRateThreshold * 100.0D), var33});
                headerPrinted = true;
            }

            this.printDottedLine(pw, "Hottest Region " + cnt);
            pw.printf(" [0x%x:0x%x] in %s%n%n", new Object[]{Long.valueOf(target.begin), Long.valueOf(target.end), target.getName()});
            target.printCode(pw, events);
            this.printDottedLine(pw);
            asm = this.events.iterator();

            while(asm.hasNext()) {
                e = (String)asm.next();
                printLine(pw, events, e, target.getEventCount(events, e));
            }

            pw.println("<total for region " + cnt + ">");
            pw.println();
            ++cnt;
        }
    }

    private static void printLine(PrintWriter pw, AbstractPerfAsmProfiler.PerfEvents events, String event, long count) {
        if(count > 0L) {
            pw.printf("%6.2f%%  ", new Object[]{Double.valueOf(100.0D * (double)count / (double)events.getTotalEvents(event).longValue())});
        } else {
            pw.printf("%9s", new Object[]{""});
        }

    }

    void printDottedLine(PrintWriter pw) {
        this.printDottedLine(pw, (String)null);
    }

    void printDottedLine(PrintWriter pw, String header) {
        boolean HEADER_WIDTH = true;
        pw.print("....");
        if(header != null) {
            header = "[" + header + "]";
            pw.print(header);
        } else {
            header = "";
        }

        for(int c = 0; c < 96 - header.length(); ++c) {
            pw.print(".");
        }

        pw.println();
    }

    List<AbstractPerfAsmProfiler.Region> makeRegions(AbstractPerfAsmProfiler.Assembly asms, AbstractPerfAsmProfiler.PerfEvents events) {
        ArrayList regions = new ArrayList();
        SortedSet addrs = events.getAllAddresses();
        HashSet eventfulAddrs = new HashSet();
        Long lastBegin = null;
        Long lastAddr = null;
        Iterator var8 = addrs.iterator();

        while(true) {
            while(var8.hasNext()) {
                Long addr = (Long)var8.next();
                if(addr.longValue() == 0L) {
                    regions.add(new AbstractPerfAsmProfiler.UnknownRegion());
                } else {
                    if(lastAddr == null) {
                        lastAddr = addr;
                        lastBegin = addr;
                    } else {
                        if(addr.longValue() - lastAddr.longValue() > (long)this.mergeMargin) {
                            List regionLines = asms.getLines(lastBegin.longValue(), lastAddr.longValue(), this.printMargin);
                            long minAddr = 9223372036854775807L;
                            long maxAddr = -9223372036854775808L;
                            Iterator var15 = regionLines.iterator();

                            while(var15.hasNext()) {
                                AbstractPerfAsmProfiler.ASMLine line = (AbstractPerfAsmProfiler.ASMLine)var15.next();
                                if(line.addr != null) {
                                    minAddr = Math.min(minAddr, line.addr.longValue());
                                    maxAddr = Math.max(maxAddr, line.addr.longValue());
                                }
                            }

                            if(!regionLines.isEmpty()) {
                                regions.add(new AbstractPerfAsmProfiler.GeneratedRegion(this.events, asms, minAddr, maxAddr, regionLines, eventfulAddrs, this.regionTooBigThreshold, this.drawIntraJumps, this.drawInterJumps));
                            } else {
                                regions.add(new AbstractPerfAsmProfiler.NativeRegion(events, lastBegin.longValue(), lastAddr.longValue(), eventfulAddrs));
                            }

                            lastBegin = addr;
                            eventfulAddrs = new HashSet();
                        }

                        lastAddr = addr;
                    }

                    eventfulAddrs.add(addr);
                }
            }

            return regions;
        }
    }

    Collection<Collection<String>> splitAssembly(File stdOut) {
        FileReader in = null;

        List writerId;
        try {
            HashMultimap e = new HashMultimap();
            Long writerId1 = Long.valueOf(-1L);
            Pattern pWriterThread = Pattern.compile("(.*)<writer thread=\'(.*)\'>(.*)");
            in = new FileReader(stdOut);
            BufferedReader br = new BufferedReader(in);

            String line;
            while((line = br.readLine()) != null) {
                if(line.contains("<writer thread=")) {
                    Matcher r = pWriterThread.matcher(line);
                    if(r.matches()) {
                        try {
                            writerId1 = Long.valueOf(r.group(2));
                        } catch (NumberFormatException var16) {
                            ;
                        }
                    }
                } else {
                    e.put(writerId1, line);
                }
            }

            ArrayList r1 = new ArrayList();
            Iterator var9 = e.keys().iterator();

            while(var9.hasNext()) {
                long id = ((Long)var9.next()).longValue();
                r1.add(e.get(Long.valueOf(id)));
            }

            ArrayList var21 = r1;
            return var21;
        } catch (IOException var17) {
            writerId = Collections.emptyList();
        } finally {
            FileUtils.safelyClose(in);
        }

        return writerId;
    }

    AbstractPerfAsmProfiler.Assembly readAssembly(File stdOut) {
        ArrayList lines = new ArrayList();
        TreeMap addressMap = new TreeMap();
        TreeMap methodMap = new TreeMap();
        HashSet intervals = new HashSet();
        Iterator var6 = this.splitAssembly(stdOut).iterator();

        label79:
        while(var6.hasNext()) {
            Collection cs = (Collection)var6.next();
            String method = null;
            String prevLine = "";
            Iterator var10 = cs.iterator();

            while(true) {
                String line;
                String trim;
                do {
                    if(!var10.hasNext()) {
                        continue label79;
                    }

                    line = (String)var10.next();
                    trim = line.trim();
                } while(trim.isEmpty());

                String[] elements = trim.split(" ");
                AbstractPerfAsmProfiler.ASMLine asmLine = new AbstractPerfAsmProfiler.ASMLine(line);
                if(elements.length >= 1 && elements[0].startsWith("0x")) {
                    try {
                        Long addr = Long.valueOf(elements[0].replace("0x", "").replace(":", ""), 16);
                        int idx = lines.size();
                        addressMap.put(addr, Integer.valueOf(idx));
                        if(method != null) {
                            methodMap.put(addr, method);
                            method = null;
                        }

                        asmLine = new AbstractPerfAsmProfiler.ASMLine(addr, line);
                        if(this.drawInterJumps || this.drawIntraJumps) {
                            for(int c = 1; c < elements.length; ++c) {
                                if(elements[c].startsWith("0x")) {
                                    try {
                                        Long target = Long.valueOf(elements[c].replace("0x", "").replace(":", ""), 16);
                                        intervals.add(new AbstractPerfAsmProfiler.Interval(addr.longValue(), target.longValue()));
                                    } catch (NumberFormatException var19) {
                                        ;
                                    }
                                }
                            }
                        }
                    } catch (NumberFormatException var20) {
                        ;
                    }
                } else if(line.contains("# {method}")) {
                    if(elements.length == 6) {
                        method = (elements[5].replace("/", ".") + "::" + elements[2]).replace("\'", "");
                    } else if(elements.length == 7) {
                        method = (elements[6].replace("/", ".") + "::" + elements[3]).replace("\'", "");
                    } else {
                        method = "<name unparseable>";
                    }

                    method = method.replace("&apos;", "");
                    method = method.replace("&lt;", "<");
                    method = method.replace("&gt;", ">");
                } else if(prevLine.contains("--------")) {
                    if(line.trim().endsWith("bytes")) {
                        method = "<stub: " + line.substring(0, line.indexOf("[")).trim() + ">";
                    }
                } else if(line.contains("StubRoutines::")) {
                    method = elements[0];
                }

                lines.add(asmLine);
                prevLine = line;
            }
        }

        return new AbstractPerfAsmProfiler.Assembly(lines, addressMap, methodMap, intervals);
    }

    static class UnknownRegion extends AbstractPerfAsmProfiler.Region {
        UnknownRegion() {
            super("<unknown>", 0L, 0L, Collections.singleton(Long.valueOf(0L)));
        }

        public void printCode(PrintWriter pw, AbstractPerfAsmProfiler.PerfEvents events) {
            pw.println(" <no assembly is recorded, unknown region>");
        }

        public String getType() {
            return "<unknown>";
        }
    }

    static class NativeRegion extends AbstractPerfAsmProfiler.Region {
        private final String lib;

        NativeRegion(AbstractPerfAsmProfiler.PerfEvents events, long begin, long end, Set<Long> eventfulAddrs) {
            super(generateName(events, eventfulAddrs), begin, end, eventfulAddrs);
            this.lib = resolveLib(events, eventfulAddrs);
        }

        static String generateName(AbstractPerfAsmProfiler.PerfEvents events, Set<Long> eventfulAddrs) {
            HashSet methods = new HashSet();
            Iterator var3 = eventfulAddrs.iterator();

            while(var3.hasNext()) {
                Long ea = (Long)var3.next();
                methods.add(events.methods.get(ea));
            }

            return Utils.join(methods, "; ");
        }

        static String resolveLib(AbstractPerfAsmProfiler.PerfEvents events, Set<Long> eventfulAddrs) {
            HashSet libs = new HashSet();
            Iterator var3 = eventfulAddrs.iterator();

            while(var3.hasNext()) {
                Long ea = (Long)var3.next();
                libs.add(events.libs.get(ea));
            }

            return Utils.join(libs, "; ");
        }

        public void printCode(PrintWriter pw, AbstractPerfAsmProfiler.PerfEvents events) {
            pw.println(" <no assembly is recorded, native region>");
        }

        public String getType() {
            return "<native code in (" + this.lib + ")>";
        }

        public String getName() {
            return this.method + " (" + this.lib + ")";
        }
    }

    static class GeneratedRegion extends AbstractPerfAsmProfiler.Region {
        final Collection<String> tracedEvents;
        final AbstractPerfAsmProfiler.Assembly asms;
        final Collection<AbstractPerfAsmProfiler.ASMLine> code;
        final int threshold;
        final boolean drawIntraJumps;
        final boolean drawInterJumps;

        GeneratedRegion(Collection<String> tracedEvents, AbstractPerfAsmProfiler.Assembly asms, long begin, long end, Collection<AbstractPerfAsmProfiler.ASMLine> code, Set<Long> eventfulAddrs, int threshold, boolean drawIntraJumps, boolean drawInterJumps) {
            super(generateName(asms, eventfulAddrs), begin, end, eventfulAddrs);
            this.tracedEvents = tracedEvents;
            this.asms = asms;
            this.code = code;
            this.threshold = threshold;
            this.drawIntraJumps = drawIntraJumps;
            this.drawInterJumps = drawInterJumps;
        }

        static String generateName(AbstractPerfAsmProfiler.Assembly asm, Set<Long> eventfulAddrs) {
            HashSet methods = new HashSet();
            Iterator var3 = eventfulAddrs.iterator();

            while(var3.hasNext()) {
                Long ea = (Long)var3.next();
                String m = asm.getMethod(ea.longValue());
                if(m != null) {
                    methods.add(m);
                }
            }

            return Utils.join(methods, "; ");
        }

        public void printCode(PrintWriter pw, AbstractPerfAsmProfiler.PerfEvents events) {
            if(this.code.size() > this.threshold) {
                pw.printf(" <region is too big to display, has %d lines, but threshold is %d>%n", new Object[]{Integer.valueOf(this.code.size()), Integer.valueOf(this.threshold)});
            } else {
                TreeSet interIvs = new TreeSet();
                TreeSet intraIvs = new TreeSet();
                Iterator prevAddr = this.asms.intervals.iterator();

                while(true) {
                    while(prevAddr.hasNext()) {
                        AbstractPerfAsmProfiler.Interval it = (AbstractPerfAsmProfiler.Interval)prevAddr.next();
                        boolean srcInline = this.begin < it.src && it.src < this.end;
                        boolean line = this.begin < it.dst && it.dst < this.end;
                        if(srcInline && line) {
                            if(this.drawInterJumps) {
                                interIvs.add(it);
                            }
                        } else if((srcInline || line) && this.drawIntraJumps) {
                            intraIvs.add(it);
                        }
                    }

                    long prevAddr1 = 0L;
                    Iterator srcInline1 = this.code.iterator();

                    while(srcInline1.hasNext()) {
                        AbstractPerfAsmProfiler.ASMLine line1 = (AbstractPerfAsmProfiler.ASMLine)srcInline1.next();
                        Iterator addr = this.tracedEvents.iterator();

                        long evAddr;
                        while(addr.hasNext()) {
                            String event = (String)addr.next();
                            evAddr = line1.addr != null?events.get(event).count(line1.addr):0L;
                            AbstractPerfAsmProfiler.printLine(pw, events, event, evAddr);
                        }

                        long addr1;
                        if(line1.addr == null) {
                            addr1 = prevAddr1;
                            evAddr = -1L;
                        } else {
                            addr1 = line1.addr.longValue();
                            evAddr = addr1;
                            prevAddr1 = addr1;
                        }

                        Iterator var13 = intraIvs.iterator();

                        AbstractPerfAsmProfiler.Interval it1;
                        while(var13.hasNext()) {
                            it1 = (AbstractPerfAsmProfiler.Interval)var13.next();
                            this.printInterval(pw, it1, addr1, evAddr, false);
                        }

                        var13 = interIvs.iterator();

                        while(var13.hasNext()) {
                            it1 = (AbstractPerfAsmProfiler.Interval)var13.next();
                            this.printInterval(pw, it1, addr1, evAddr, true);
                        }

                        pw.println(line1.code);
                    }
                    break;
                }
            }

        }

        private void printInterval(PrintWriter pw, AbstractPerfAsmProfiler.Interval it, long addr, long evAddr, boolean inline) {
            if(it.src < it.dst) {
                if(it.src == evAddr) {
                    pw.print("╭");
                } else if(it.dst == evAddr) {
                    pw.print("↘");
                } else if(it.src <= addr && addr < it.dst) {
                    if(inline) {
                        pw.print("│");
                    } else {
                        pw.print("╵");
                    }
                } else {
                    pw.print(" ");
                }
            } else if(it.src == evAddr) {
                pw.print("╰");
            } else if(it.dst == evAddr) {
                pw.print("↗");
            } else if(it.dst <= addr && addr < it.src) {
                if(inline) {
                    pw.print("│");
                } else {
                    pw.print("╵");
                }
            } else {
                pw.print(" ");
            }

        }

        public String getType() {
            return "<generated code>";
        }
    }

    static class Region {
        final String method;
        final long begin;
        final long end;
        final Set<Long> eventfulAddrs;
        final Map<String, Long> eventCountCache;

        Region(String method, long begin, long end, Set<Long> eventfulAddrs) {
            this.method = method;
            this.begin = begin;
            this.end = end;
            this.eventfulAddrs = eventfulAddrs;
            this.eventCountCache = new HashMap();
        }

        long getEventCount(AbstractPerfAsmProfiler.PerfEvents events, String event) {
            if(!this.eventCountCache.containsKey(event)) {
                Multiset evs = events.get(event);
                long count = 0L;

                Long addr;
                for(Iterator var6 = this.eventfulAddrs.iterator(); var6.hasNext(); count += evs.count(addr)) {
                    addr = (Long)var6.next();
                }

                this.eventCountCache.put(event, Long.valueOf(count));
            }

            return ((Long)this.eventCountCache.get(event)).longValue();
        }

        public void printCode(PrintWriter pw, AbstractPerfAsmProfiler.PerfEvents events) {
            pw.println("<no code>");
        }

        public String getName() {
            return this.method;
        }

        public String getType() {
            return "<unknown>";
        }
    }

    static class ASMLine {
        final Long addr;
        final String code;

        ASMLine(String code) {
            this((Long)null, code);
        }

        ASMLine(Long addr, String code) {
            this.addr = addr;
            this.code = code;
        }
    }

    static class Assembly {
        final List<AbstractPerfAsmProfiler.ASMLine> lines;
        final SortedMap<Long, Integer> addressMap;
        final SortedMap<Long, String> methodMap;
        final Set<AbstractPerfAsmProfiler.Interval> intervals;

        public Assembly(List<AbstractPerfAsmProfiler.ASMLine> lines, SortedMap<Long, Integer> addressMap, SortedMap<Long, String> methodMap, Set<AbstractPerfAsmProfiler.Interval> intervals) {
            this.lines = lines;
            this.addressMap = addressMap;
            this.methodMap = methodMap;
            this.intervals = intervals;
        }

        public int size() {
            return this.addressMap.size();
        }

        public List<AbstractPerfAsmProfiler.ASMLine> getLines(long begin, long end, int window) {
            SortedMap tailMap = this.addressMap.tailMap(Long.valueOf(begin));
            if(!tailMap.isEmpty()) {
                Long beginAddr = (Long)tailMap.firstKey();
                Integer beginIdx = (Integer)this.addressMap.get(beginAddr);
                SortedMap headMap = this.addressMap.headMap(Long.valueOf(end));
                if(!headMap.isEmpty()) {
                    Long endAddr = (Long)headMap.lastKey();
                    Integer endIdx = (Integer)this.addressMap.get(endAddr);
                    beginIdx = Integer.valueOf(Math.max(0, beginIdx.intValue() - window));
                    endIdx = Integer.valueOf(Math.min(this.lines.size(), endIdx.intValue() + 2 + window));
                    return beginIdx.intValue() < endIdx.intValue()?this.lines.subList(beginIdx.intValue(), endIdx.intValue()):Collections.emptyList();
                } else {
                    return Collections.emptyList();
                }
            } else {
                return Collections.emptyList();
            }
        }

        public String getMethod(long addr) {
            if(this.methodMap.containsKey(Long.valueOf(addr))) {
                return (String)this.methodMap.get(Long.valueOf(addr));
            } else {
                SortedMap head = this.methodMap.headMap(Long.valueOf(addr));
                return head.isEmpty()?"<unresolved>":(String)this.methodMap.get(head.lastKey());
            }
        }
    }

    protected static class PerfEvents {
        final Map<String, Multiset<Long>> events;
        final Map<Long, String> methods;
        final Map<Long, String> libs;
        final Map<String, Long> totalCounts;

        PerfEvents(Collection<String> tracedEvents, Map<String, Multiset<Long>> events, Map<Long, String> methods, Map<Long, String> libs) {
            this.events = events;
            this.methods = methods;
            this.libs = libs;
            this.totalCounts = new HashMap();
            Iterator var5 = tracedEvents.iterator();

            while(var5.hasNext()) {
                String event = (String)var5.next();
                this.totalCounts.put(event, Long.valueOf(((Multiset)events.get(event)).size()));
            }

        }

        public PerfEvents(Collection<String> tracedEvents) {
            this(tracedEvents, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }

        public boolean isEmpty() {
            return this.events.isEmpty();
        }

        public Multiset<Long> get(String event) {
            return (Multiset)this.events.get(event);
        }

        public SortedSet<Long> getAllAddresses() {
            TreeSet addrs = new TreeSet();
            Iterator var2 = this.events.values().iterator();

            while(var2.hasNext()) {
                Multiset e = (Multiset)var2.next();
                addrs.addAll(e.keys());
            }

            return addrs;
        }

        public Long getTotalEvents(String event) {
            return (Long)this.totalCounts.get(event);
        }
    }

    static class Interval implements Comparable<AbstractPerfAsmProfiler.Interval> {
        private final long src;
        private final long dst;

        public Interval(long src, long dst) {
            this.src = src;
            this.dst = dst;
        }

        public boolean equals(Object o) {
            if(this == o) {
                return true;
            } else if(o != null && this.getClass() == o.getClass()) {
                AbstractPerfAsmProfiler.Interval interval = (AbstractPerfAsmProfiler.Interval)o;
                return this.dst != interval.dst?false:this.src == interval.src;
            } else {
                return false;
            }
        }

        public int hashCode() {
            int result = (int)(this.src ^ this.src >>> 32);
            result = 31 * result + (int)(this.dst ^ this.dst >>> 32);
            return result;
        }

        public int compareTo(AbstractPerfAsmProfiler.Interval o) {
            return this.src < o.src?-1:(this.src > o.src?1:(this.dst < o.dst?-1:(this.dst == o.dst?0:1)));
        }
    }

    static class PerfResultAggregator implements Aggregator<AbstractPerfAsmProfiler.PerfResult> {
        PerfResultAggregator() {
        }

        public AbstractPerfAsmProfiler.PerfResult aggregate(Collection<AbstractPerfAsmProfiler.PerfResult> results) {
            String output = "";

            AbstractPerfAsmProfiler.PerfResult r;
            for(Iterator var3 = results.iterator(); var3.hasNext(); output = output + r.output) {
                r = (AbstractPerfAsmProfiler.PerfResult)var3.next();
            }

            return new AbstractPerfAsmProfiler.PerfResult(output);
        }
    }

    static class PerfResult extends Result<AbstractPerfAsmProfiler.PerfResult> {
        private static final long serialVersionUID = 6871141606856800453L;
        private final String output;

        public PerfResult(String output) {
            super(ResultRole.SECONDARY, "·asm", of(0.0D / 0.0), "---", AggregationPolicy.AVG);
            this.output = output;
        }

        protected Aggregator<AbstractPerfAsmProfiler.PerfResult> getThreadAggregator() {
            return new AbstractPerfAsmProfiler.PerfResultAggregator();
        }

        protected Aggregator<AbstractPerfAsmProfiler.PerfResult> getIterationAggregator() {
            return new AbstractPerfAsmProfiler.PerfResultAggregator();
        }

        public String toString() {
            return "(text only)";
        }

        public String extendedInfo() {
            return this.output;
        }
    }
}
