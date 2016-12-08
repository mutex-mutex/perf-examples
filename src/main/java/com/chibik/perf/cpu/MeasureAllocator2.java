package com.chibik.perf.cpu;

public class MeasureAllocator2 {


    public static class AllocaClass1 {
        private String s1;
        private String s2;
        private String s3;
    }

    public static class AllocaClass2 extends MeasureAllocator.AllocaClass1 {
        private String s4;
        private String s5;
        private String s6;
    }

    public static class AllocaClass3 extends MeasureAllocator.AllocaClass2 {
        private String s7;
        private String s8;
        private String s9;
    }

    public static class AllocaClass4 extends MeasureAllocator.AllocaClass3 {
        private String s10;
        private String s11;
        private String s12;
    }

    public static class AllocaClass5 extends MeasureAllocator.AllocaClass4 {
        private String s13;
        private String s14;
        private String s15;
    }

    public static void main(String[] args) {
        Object v = null;

        long start = System.currentTimeMillis();
        for(int i = 0; i < 1500000000; i++) {
            v = new AllocaClass1();
        }
        System.out.println(v);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed);

        System.out.println("*****");
        System.out.println("*****");
        System.out.println("*****");
        System.out.println("*****");
        System.out.println("*****");
        System.out.println("*****");

        start = System.currentTimeMillis();
        for(int i = 0; i < 1500000000; i++) {
            v = new AllocaClass5();
        }
        System.out.println(v);
        elapsed = System.currentTimeMillis() - start;
        System.out.println(elapsed);

    }
}
