//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.openjdk.jmh.profile;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.profile.AbstractPerfAsmProfiler;
import org.openjdk.jmh.profile.ProfilerException;
import org.openjdk.jmh.profile.AbstractPerfAsmProfiler.PerfEvents;
import org.openjdk.jmh.util.Deduplicator;
import org.openjdk.jmh.util.FileUtils;
import org.openjdk.jmh.util.InputStreamDrainer;
import org.openjdk.jmh.util.Multiset;
import org.openjdk.jmh.util.TreeMultiset;
import org.openjdk.jmh.util.Utils;

public class LinuxPerfAsmProfiler extends AbstractPerfAsmProfiler {
    private final long sampleFrequency;
    private OptionSpec<Long> optFrequency;

    public LinuxPerfAsmProfiler(String initLine) throws ProfilerException {
        super(initLine, new String[]{"cycles", "instructions"});
        Collection failMsg = Utils.tryWith(new String[]{"perf", "stat", "--log-fd", "2", "echo", "1"});
        if(!failMsg.isEmpty()) {
            throw new ProfilerException(failMsg.toString());
        } else {
            try {
                this.sampleFrequency = ((Long)this.set.valueOf(this.optFrequency)).longValue();
            } catch (OptionException var4) {
                throw new ProfilerException(var4.getMessage());
            }
        }
    }

    protected void addMyOptions(OptionParser parser) {
        this.optFrequency = parser.accepts("frequency", "Sampling frequency. This is synonymous to perf record --freq #").withRequiredArg().ofType(Long.class).describedAs("freq").defaultsTo(Long.valueOf(1000L), new Long[0]);
    }

    public Collection<String> addJVMInvokeOptions(BenchmarkParams params) {
        return Arrays.asList(new String[]{"perf", "record", "--freq", String.valueOf(this.sampleFrequency), "--event", Utils.join(this.events, ","), "--output", this.perfBinData});
    }

    public String getDescription() {
        return "Linux perf + PrintAssembly Profiler";
    }

    protected void parseEvents() {
        try {
            ProcessBuilder ex = new ProcessBuilder(new String[]{"perf", "script", "--fields", "time,event,ip,sym,dso", "--input", this.perfBinData});
            Process p = ex.start();
            FileOutputStream fos = new FileOutputStream(this.perfParsedData);
            InputStreamDrainer errDrainer = new InputStreamDrainer(p.getErrorStream(), fos);
            InputStreamDrainer outDrainer = new InputStreamDrainer(p.getInputStream(), fos);
            errDrainer.start();
            outDrainer.start();
            p.waitFor();
            errDrainer.join();
            outDrainer.join();
            FileUtils.safelyClose(fos);
        } catch (IOException var6) {
            throw new IllegalStateException(var6);
        } catch (InterruptedException var7) {
            throw new IllegalStateException(var7);
        }
    }

    protected PerfEvents readEvents(double skipSec) {
        FileReader fr = null;

        PerfEvents reader;
        try {
            Deduplicator e = new Deduplicator();
            fr = new FileReader(this.perfParsedData);
            BufferedReader reader1 = new BufferedReader(fr);
            HashMap methods = new HashMap();
            HashMap libs = new HashMap();
            LinkedHashMap events = new LinkedHashMap();
            Iterator startTime = this.events.iterator();

            String line;
            while(startTime.hasNext()) {
                line = (String)startTime.next();
                events.put(line, new TreeMultiset());
            }

            Double startTime1 = null;

            while((line = reader1.readLine()) != null) {
                if(!line.startsWith("#")) {
                    String[] elems = line.trim().split("[ ]+");
                    if(elems.length >= 4) {
                        String strTime = elems[0].replace(":", "");
                        String evName = elems[1].replace(":", "");
                        String strAddr = elems[2];
                        String symbol = Utils.join((String[])Arrays.copyOfRange(elems, 3, elems.length - 1), " ");
                        String lib = elems[elems.length - 1];
                        lib = lib.substring(lib.lastIndexOf("/") + 1, lib.length()).replace("(", "").replace(")", "");

                        try {
                            Double evs = Double.valueOf(strTime);
                            if(startTime1 == null) {
                                startTime1 = evs;
                            } else if(evs.doubleValue() - startTime1.doubleValue() < skipSec) {
                                continue;
                            }
                        } catch (NumberFormatException var29) {
                            continue;
                        }

                        Multiset evs1 = (Multiset)events.get(evName);
                        if(evs1 != null) {
                            Long addr;
                            try {
                                addr = Long.valueOf(strAddr, 16);
                            } catch (NumberFormatException var28) {
                                try {
                                    addr = Long.valueOf((new BigInteger(strAddr, 16)).longValue());
                                } catch (NumberFormatException var27) {
                                    addr = Long.valueOf(0L);
                                }
                            }

                            evs1.add(addr);
                            methods.put(addr, e.dedup(symbol));
                            libs.put(addr, e.dedup(lib));
                        }
                    }
                }
            }

            methods.put(Long.valueOf(0L), "<unknown>");
            PerfEvents elems1 = new PerfEvents(this.events, events, methods, libs);
            return elems1;
        } catch (IOException var30) {
            reader = new PerfEvents(this.events);
        } finally {
            FileUtils.safelyClose(fr);
        }

        return reader;
    }

    protected String perfBinaryExtension() {
        return ".perfbin";
    }
}
